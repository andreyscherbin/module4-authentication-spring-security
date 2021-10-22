package com.epam.esm.dao.impl;

import com.epam.esm.dao.OrderDao;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Order;
import com.epam.esm.entity.OrderInfo;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.epam.esm.entity.OrderTable.ORDER_ID;

@Repository
public class OrderDaoImpl implements OrderDao<Long> {

  @PersistenceContext private EntityManager entityManager;

  @Override
  public List<Order> find(Query query) {
    return query.getResultList();
  }

  @Override
  public Optional<Order> findById(Long id) {
    TypedQuery<Order> query =
        entityManager.createQuery(
            "from orders o left join fetch o.certificates where o.id = :id", Order.class);
    query.setParameter(ORDER_ID, id);
    try {
      Order order = query.getSingleResult();
      return Optional.of(order);
    } catch (NoResultException ex) {
      return Optional.empty();
    }
  }

  @Transactional
  @Override
  public Order create(Order order) {
    BigDecimal cost = calculateCost(order);
    order.setCost(cost);
    entityManager.persist(order);
    entityManager.flush();
    entityManager.detach(order);
    return order;
  }

  private BigDecimal calculateCost(Order order) {
    List<Long> certificates =
        order.getCertificates().stream().map(GiftCertificate::getId).collect(Collectors.toList());
    TypedQuery<BigDecimal> query =
        entityManager.createQuery(
            "select sum(gc.price) from gift_certificate gc where gc.id IN :certificates",
            BigDecimal.class);
    query.setParameter("certificates", certificates);
    return query.getSingleResult();
  }

  @Override
  public Optional<OrderInfo> findOrderInfoByUser(Long userId, Long orderId) {
    TypedQuery<Order> query =
        entityManager.createQuery(
            "from orders o  where  o.id  = :ordersId and o.user.id = :usersId", Order.class);
    query.setParameter("usersId", userId).setParameter("ordersId", orderId);
    try {
      Order order = query.getSingleResult();
      OrderInfo orderInfo = new OrderInfo();
      orderInfo.setCost(order.getCost());
      orderInfo.setCreateDate(order.getCreateDate());
      return Optional.of(orderInfo);
    } catch (NoResultException ex) {
      return Optional.empty();
    }
  }

  @Override
  public long countOrders() {
    Query query = entityManager.createQuery("select count(*) from orders");
    return (long) query.getSingleResult();
  }
}
