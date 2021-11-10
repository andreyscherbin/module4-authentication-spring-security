package com.epam.esm.web.api;

import com.epam.esm.entity.DTO;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Order;
import com.epam.esm.entity.User;
import com.epam.esm.service.GiftCertificateService;
import com.epam.esm.service.OrderService;
import com.epam.esm.service.UserService;
import com.epam.esm.web.exception.ResourceException;
import com.epam.esm.web.hateoas.HateoasSupportOrder;
import com.epam.esm.web.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.epam.esm.entity.ErrorCode.GIFT_CERTIFICATE_ERROR_CODE;
import static com.epam.esm.entity.ErrorCode.USER_ERROR_CODE;

@RestController
@RequestMapping("/orders")
public class OrderController {

  private OrderService orderService;
  private UserService userService;
  private GiftCertificateService giftCertificateService;
  private final JwtTokenProvider jwtTokenProvider;

  @Autowired
  public OrderController(
      OrderService orderService,
      UserService userService,
      GiftCertificateService giftCertificateService,
      JwtTokenProvider jwtTokenProvider) {
    this.orderService = orderService;
    this.userService = userService;
    this.giftCertificateService = giftCertificateService;
    this.jwtTokenProvider = jwtTokenProvider;
  }

  @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
  @PostMapping(consumes = "application/json")
  @ResponseStatus(HttpStatus.CREATED)
  public EntityModel<Order> makeOrder(@RequestBody @Valid Order order) {
    String token = (String) SecurityContextHolder.getContext().getAuthentication().getCredentials();
    String username = jwtTokenProvider.getUsername(token);
    Optional<User> user = userService.findByUsername(username);
    if (user.isEmpty()) {
      throw new UsernameNotFoundException("User with username: " + username + "not found");
    }
    order.setUser(user.get());
    for (GiftCertificate gc : order.getCertificates()) {
      Optional<GiftCertificate> findGiftCertificate = giftCertificateService.findById(gc.getId());
      if (findGiftCertificate.isEmpty()) {
        throw new ResourceException(
            "certificate.not_found", HttpStatus.NOT_FOUND, GIFT_CERTIFICATE_ERROR_CODE, gc.getId());
      }
    }
    long idResource = orderService.create(order).getId();
    Optional<Order> savedOrder = orderService.findById(idResource);
    return HateoasSupportOrder.getModel(savedOrder.get());
  }

  @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
  @GetMapping(value = "/{id}", produces = "application/json")
  public EntityModel<Order> getOrderById(@PathVariable Long id) {
    Order order =
        orderService
            .findById(id)
            .orElseThrow(
                () ->
                    new ResourceException(
                        "order.not_found", HttpStatus.NOT_FOUND, USER_ERROR_CODE, id));
    return HateoasSupportOrder.getModel(order);
  }

  @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
  @GetMapping(produces = "application/json")
  public DTO getOrders(@RequestParam Map<String, String> params) {
    List<Order> orders = orderService.find(params);
    DTO dto = new DTO();
    dto.setResult(HateoasSupportOrder.getCollectionModel(orders));
    dto.setTotalRows(orderService.getTotalRows());
    HateoasSupportOrder.addParams((CollectionModel<EntityModel<Order>>) dto.getResult(), params);
    return dto;
  }
}
