package com.epam.esm.service.impl;

import com.epam.esm.dao.OrderDao;
import com.epam.esm.dao.builder.OrderBuilder;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Order;
import com.epam.esm.entity.OrderInfo;
import com.epam.esm.entity.ParamName;
import com.epam.esm.service.OrderService;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.epam.esm.service.exception.ValidationException;
import com.epam.esm.util.UtilClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.validation.MapBindingResult;
import org.springframework.validation.Validator;

import javax.persistence.Query;

@Service
public class OrderServiceImpl implements OrderService {

  private final OrderDao<Long> orderDao;

  private Validator paramValidator;
  private final OrderBuilder orderBuilder;

  @Autowired
  @Qualifier("paramValidator")
  public void setParamValidator(Validator paramValidator) {
    this.paramValidator = paramValidator;
  }

  public OrderServiceImpl(OrderDao<Long> orderDao, OrderBuilder orderBuilder) {
    this.orderBuilder = orderBuilder;
    this.orderDao = orderDao;
  }

  @Override
  @Transactional
  public Order create(Order order) {
    List<GiftCertificate> certificates =
        order.getCertificates().stream().distinct().collect(Collectors.toList());
    order.setCertificates(certificates);
    return orderDao.create(order);
  }

  @Override
  public Optional<Order> findById(long id) {
    return orderDao.findById(id);
  }

  @Override
  public List<Order> find(Map<String, String> params) {
    EnumMap<ParamName, String> enumMap = UtilClass.convertToEnumMap(params);
    Errors errors = new MapBindingResult(enumMap, "enumMap");
    paramValidator.validate(enumMap, errors);
    if (errors.hasErrors()) {
      throw new ValidationException(errors);
    }
    Query query = orderBuilder.buildQuery(enumMap);
    return orderDao.find(query);
  }

  @Override
  public Optional<OrderInfo> findOrderInfoByUser(Long userId, Long orderId) {
    return orderDao.findOrderInfoByUser(userId, orderId);
  }

  @Override
  public long getTotalRows() {
    return orderDao.countOrders();
  }
}
