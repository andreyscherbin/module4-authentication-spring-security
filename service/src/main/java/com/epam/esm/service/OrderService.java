package com.epam.esm.service;

import com.epam.esm.entity.Order;
import com.epam.esm.entity.OrderInfo;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface OrderService {
  Order create(Order order);

  Optional<Order> findById(long id);

  List<Order> find(Map<String, String> params);

  Optional<OrderInfo> findOrderInfoByUser(Long userId, Long orderId);

  long getTotalRows();
}
