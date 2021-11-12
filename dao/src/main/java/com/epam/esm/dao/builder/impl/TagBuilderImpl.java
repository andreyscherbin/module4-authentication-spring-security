package com.epam.esm.dao.builder.impl;

import com.epam.esm.dao.builder.TagBuilder;
import com.epam.esm.entity.ParamName;
import com.epam.esm.entity.Tag;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.EnumMap;

import static com.epam.esm.entity.ParamName.*;
import static com.epam.esm.entity.SQLClauses.ASCENDING_ORDER;
import static com.epam.esm.entity.SQLClauses.DESCENDING_ORDER;
import static com.epam.esm.entity.TagTable.TAG_NAME;

@Component
public class TagBuilderImpl implements TagBuilder {

  @PersistenceContext private EntityManager em;

  private static final int DEFAULT_LIMIT = 100000;
  private static final int DEFAULT_OFFSET = 0;
  private static final String DEFAULT_ORDER_BY = DESCENDING_ORDER;

  @Override
  public Query buildQuery(EnumMap<ParamName, String> params) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Tag> cq = cb.createQuery(Tag.class);
    Root<Tag> from = cq.from(Tag.class);
    cq.select(from);
    if (params.containsKey(NAME)) {
      Predicate namePredicate = cb.equal(from.get(TAG_NAME), params.get(NAME));
      cq.where(namePredicate);
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
      sort(sortBy, orderBy, cb, cq, from);
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
      TypedQuery<Tag> typedQuery = em.createQuery(cq);
      typedQuery.setFirstResult(offset);
      typedQuery.setMaxResults(limit);
      return typedQuery;
    }
    return em.createQuery(cq);
  }

  private boolean isSortNeed(EnumMap<ParamName, String> params) {
    return params.containsKey(SORT_BY) || params.containsKey(ParamName.ORDER_BY);
  }

  private void sort(
      String sortBy, String orderBy, CriteriaBuilder cb, CriteriaQuery<Tag> cq, Root<Tag> from) {
    switch (sortBy.toLowerCase()) {
      case TAG_NAME -> {
        switch (orderBy.toUpperCase()) {
          case ASCENDING_ORDER -> cq.orderBy(cb.asc(from.get(TAG_NAME)));
          case DESCENDING_ORDER -> cq.orderBy(cb.desc(from.get(TAG_NAME)));
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
