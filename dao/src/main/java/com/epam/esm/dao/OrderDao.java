package com.epam.esm.dao;

import com.epam.esm.entity.Order;
import com.epam.esm.entity.OrderInfo;

import javax.persistence.Query;
import java.util.List;
import java.util.Optional;

public interface OrderDao<K> {

  List<Order> find(Query query);

  Optional<Order> findById(K id);

  Order create(Order order);

  Optional<OrderInfo> findOrderInfoByUser(K userId, K orderId);

  long countOrders();
}
