package com.epam.esm.web.api;

import com.epam.esm.entity.*;
import com.epam.esm.service.OrderService;
import com.epam.esm.service.UserService;
import com.epam.esm.web.exception.ResourceException;
import com.epam.esm.web.hateoas.HateoasSupportOrder;
import com.epam.esm.web.hateoas.HateoasSupportUser;
import com.epam.esm.web.security.jwt.JwtAuthenticationException;
import com.epam.esm.web.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.epam.esm.entity.ErrorCode.ORDER_ERROR_CODE;
import static com.epam.esm.entity.ErrorCode.USER_ERROR_CODE;

@RestController
@RequestMapping("/users")
public class UserController {

  private final UserService userService;
  private final OrderService orderService;
  private final JwtTokenProvider jwtTokenProvider;

  @Autowired
  public UserController(
      UserService userService, OrderService orderService, JwtTokenProvider jwtTokenProvider) {
    this.userService = userService;
    this.orderService = orderService;
    this.jwtTokenProvider = jwtTokenProvider;
  }

  @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
  @GetMapping(value = "/{id}", produces = "application/json")
  public EntityModel<User> getUserById(@PathVariable Long id) {
    try {
      String token =
          (String) SecurityContextHolder.getContext().getAuthentication().getCredentials();
      String username = jwtTokenProvider.getUsername(token);
      Optional<User> findUser = userService.findByUsername(username);
      if (findUser.isEmpty()) {
        throw new UsernameNotFoundException("User with username: " + username + "not found");
      }
      if (findUser.get().getId() != id
          && findUser.get().getRoles().stream()
              .noneMatch(role -> role.getName().equals("ROLE_ADMIN"))) {
        throw new AccessDeniedException("illegal access for " + username);
      }
      User user =
          userService
              .findById(id)
              .orElseThrow(
                  () ->
                      new ResourceException(
                          "user.not_found", HttpStatus.NOT_FOUND, USER_ERROR_CODE, id));
      return HateoasSupportUser.getModel(user);
    } catch (AuthenticationException e) {
      throw new JwtAuthenticationException(("invalid.access_token"));
    }
  }

  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  @GetMapping(produces = "application/json")
  public DTO getUsers(@RequestParam Map<String, String> params) {
    List<User> users = userService.find(params);
    DTO dto = new DTO();
    dto.setResult(HateoasSupportUser.getCollectionModel(users));
    dto.setTotalRows(userService.getTotalRows());
    HateoasSupportUser.addParams((CollectionModel<EntityModel<User>>) dto.getResult(), params);
    return dto;
  }

  @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
  @GetMapping(value = "/{id}/orders", produces = "application/json")
  public CollectionModel<EntityModel<Order>> getOrdersByUser(
      @PathVariable Long id, @RequestParam(required = false) Map<String, String> params) {
    Optional<User> user = userService.findById(id);
    if (user.isEmpty()) {
      throw new ResourceException("user.not_found", HttpStatus.NOT_FOUND, USER_ERROR_CODE, id);
    }
    if (params != null) {
      params.put(ParamName.ID.toString(), id.toString());
      List<Order> result = orderService.find(params);
      return HateoasSupportOrder.getCollectionModel(result);
    } else {
      List<Order> result = orderService.find(Map.of(ParamName.ID.toString(), id.toString()));
      return HateoasSupportOrder.getCollectionModel(result);
    }
  }

  @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
  @GetMapping(value = "/{userId}/orders/{orderId}", produces = "application/json")
  public OrderInfo getOrderInfoByUser(@PathVariable Long userId, @PathVariable Long orderId) {
    Optional<User> user = userService.findById(userId);
    if (user.isEmpty()) {
      throw new ResourceException("user.not_found", HttpStatus.NOT_FOUND, USER_ERROR_CODE, userId);
    }
    Optional<Order> order = orderService.findById(orderId);
    if (order.isEmpty()) {
      throw new ResourceException(
          "order.not_found", HttpStatus.NOT_FOUND, ORDER_ERROR_CODE, orderId);
    }
    Optional<OrderInfo> orderInfo = orderService.findOrderInfoByUser(userId, orderId);
    if (orderInfo.isEmpty()) {
      throw new ResourceException(
          "user_order.not_found", HttpStatus.NOT_FOUND, ORDER_ERROR_CODE, userId, orderId);
    } else if (orderInfo.get().getCost() == null) {
      throw new ResourceException(
          "user_empty_order", HttpStatus.NOT_FOUND, ORDER_ERROR_CODE, userId, orderId);
    }
    return orderInfo.get();
  }
}
