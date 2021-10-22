package com.epam.esm.dao.builder.impl;

import com.epam.esm.dao.builder.OrderBuilder;
import com.epam.esm.entity.Order;
import com.epam.esm.entity.ParamName;
import com.epam.esm.entity.User;
import com.epam.esm.entity.UserTable;
import org.hibernate.annotations.QueryHints;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.EnumMap;

import static com.epam.esm.entity.OrderTable.ORDER_COST;
import static com.epam.esm.entity.OrderTable.ORDER_CREATE_DATE;
import static com.epam.esm.entity.ParamName.*;
import static com.epam.esm.entity.SQLClauses.ASCENDING_ORDER;
import static com.epam.esm.entity.SQLClauses.DESCENDING_ORDER;


@Component
public class OrderBuilderImpl implements OrderBuilder {

  @PersistenceContext
  private EntityManager entityManager;

  private static final int DEFAULT_LIMIT = 100000;
  private static final int DEFAULT_OFFSET = 0;
  private static final String DEFAULT_ORDER_BY = DESCENDING_ORDER;

  @Override
  public Query buildQuery(EnumMap<ParamName, String> params) {

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Order> orderCriteria = cb.createQuery(Order.class);
    Root<Order> orderRoot = orderCriteria.from(Order.class);
    orderRoot.fetch("certificates", JoinType.LEFT);
    orderCriteria.select(orderRoot).distinct(true);
    if (params.containsKey(ID)) {
      Join<Order, User> join = orderRoot.join("user", JoinType.INNER);
      Predicate userPredicate = cb.equal(join.get(UserTable.USER_ID), params.get(ID));
      orderCriteria.where(userPredicate);
    }
    if (isSortNeed(params)) {
      String sortBy = null;
      String orderBy = null;
      if(params.containsKey(SORT_BY) && !params.containsKey(ORDER_BY)){
        sortBy = params.get(SORT_BY);
        orderBy = DEFAULT_ORDER_BY;
      }
      else if(!params.containsKey(SORT_BY) && params.containsKey(ORDER_BY)){
        throw new IllegalArgumentException("please, input sort by");
      }
      else if(params.containsKey(SORT_BY) && params.containsKey(ORDER_BY)){
        sortBy = params.get(SORT_BY);
        orderBy = params.get(ORDER_BY);
      }
      sort(sortBy, orderBy, cb, orderCriteria, orderRoot);
    }
    if(isPageNeed(params)){
      int limit = 0;
      int offset = 0;
      int pageSize;
      int pageNum;
      if(params.containsKey(ParamName.PAGE_SIZE) && !params.containsKey(ParamName.PAGE_NUM)){
        pageSize = Integer.valueOf(params.get(ParamName.PAGE_SIZE));
        limit = pageSize;
        offset = DEFAULT_OFFSET;
      }
      else if(!params.containsKey(ParamName.PAGE_SIZE) && params.containsKey(ParamName.PAGE_NUM)){
        limit = DEFAULT_LIMIT;
        pageNum = Integer.valueOf(params.get(ParamName.PAGE_NUM));
        offset = (pageNum - 1) * limit;
      }
      else if(params.containsKey(ParamName.PAGE_SIZE) && params.containsKey(ParamName.PAGE_NUM)){
        pageSize = Integer.valueOf(params.get(ParamName.PAGE_SIZE));
        pageNum = Integer.valueOf(params.get(ParamName.PAGE_NUM));
        limit = pageSize;
        offset = (pageNum - 1) * limit;
      }
      TypedQuery<Order> typedQuery = entityManager.createQuery(orderCriteria);
      typedQuery.setFirstResult(offset);
      typedQuery.setMaxResults(limit);
      return typedQuery;
    }
    return entityManager.createQuery(orderCriteria).setHint(QueryHints.PASS_DISTINCT_THROUGH, false);
  }

  private boolean isSortNeed(EnumMap<ParamName, String> params) {
    return params.containsKey(SORT_BY) || params.containsKey(ParamName.ORDER_BY);
  }

  private void sort(
          String sortBy, String orderBy, CriteriaBuilder cb, CriteriaQuery<Order> cq, Root<Order> from) {
    switch (sortBy.toLowerCase()) {
      case ORDER_CREATE_DATE -> {
        switch (orderBy.toUpperCase()) {
          case ASCENDING_ORDER -> cq.orderBy(cb.asc(from.get("createDate")));
          case DESCENDING_ORDER -> cq.orderBy(cb.desc(from.get("createDate")));
          default -> throw new IllegalArgumentException("Invalid order: " + orderBy);
        }
      }
      case ORDER_COST -> {
        switch (orderBy.toUpperCase()) {
          case ASCENDING_ORDER -> cq.orderBy(cb.asc(from.get(ORDER_COST)));
          case DESCENDING_ORDER -> cq.orderBy(cb.desc(from.get(ORDER_COST)));
          default -> throw new IllegalArgumentException("Invalid order: " + orderBy);
        }
      }
      default -> throw new IllegalArgumentException("Invalid column: " + sortBy);
    }
  }

  private boolean isPageNeed(EnumMap<ParamName, String> params) {
    return params.containsKey(ParamName.PAGE_SIZE) || params.containsKey(ParamName.PAGE_NUM);
  }
}
