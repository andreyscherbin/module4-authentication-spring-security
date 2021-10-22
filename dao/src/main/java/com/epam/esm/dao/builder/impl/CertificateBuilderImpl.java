package com.epam.esm.dao.builder.impl;

import com.epam.esm.dao.builder.CertificateBuilder;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.ParamName;
import com.epam.esm.entity.Tag;
import org.hibernate.annotations.QueryHints;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import static com.epam.esm.entity.GiftCertificateTable.*;
import static com.epam.esm.entity.ParamName.*;
import static com.epam.esm.entity.SQLClauses.ASCENDING_ORDER;
import static com.epam.esm.entity.SQLClauses.DESCENDING_ORDER;
import static com.epam.esm.entity.TagTable.TAG_NAME;

@Component
public class CertificateBuilderImpl implements CertificateBuilder {

  @PersistenceContext private EntityManager entityManager;

  private static final int DEFAULT_LIMIT = 100000;
  private static final int DEFAULT_OFFSET = 0;
  private static final String DEFAULT_ORDER_BY = DESCENDING_ORDER;

  @Override
  public Query buildQuery(EnumMap<ParamName, String> params, List<Tag> tags) {
    List<Predicate> predicates = new ArrayList<>();
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<GiftCertificate> giftCertificateCriteria = cb.createQuery(GiftCertificate.class);
    Root<GiftCertificate> giftCertificateRoot = giftCertificateCriteria.from(GiftCertificate.class);
    giftCertificateRoot.fetch("tags", JoinType.LEFT);
    giftCertificateCriteria.select(giftCertificateRoot).distinct(true);
    if (!tags.isEmpty()) {
      List<Predicate> subQueryPredicates = new ArrayList<>();
      Subquery<GiftCertificate> subquery = giftCertificateCriteria.subquery(GiftCertificate.class);
      Root<GiftCertificate> root = subquery.from(GiftCertificate.class);
      Join<Object, Object> subqueryJoin = root.join("tags", JoinType.INNER);
      Predicate tagPredicate = cb.equal((subqueryJoin).get(TAG_NAME), tags.get(0).getName());
      subQueryPredicates.add(tagPredicate);
      if (tags.size() > 1) {
        for (int i = 1; i < tags.size(); i++) {
          Subquery<GiftCertificate> tagSubquery = giftCertificateCriteria.subquery(GiftCertificate.class);
          Root<GiftCertificate> tagRoot = tagSubquery.from(GiftCertificate.class);
          Join<Object, Object> subqueryJoinTag = tagRoot.join("tags", JoinType.INNER);
          Predicate tagName = cb.equal((subqueryJoinTag).get(TAG_NAME), tags.get(i).getName());
          tagSubquery.select(tagRoot).where(tagName);
          Predicate certificateId = root.get(GIFT_CERTIFICATE_ID).in(tagSubquery);
          subQueryPredicates.add(certificateId);
        }
      }
      if (params.containsKey(NAME)){
        Predicate namePredicate = cb.like(giftCertificateRoot.get(GIFT_CERTIFICATE_NAME), "%" + params.get(NAME) + "%");
        subQueryPredicates.add(namePredicate);
      }
      if (params.containsKey(DESCRIPTION)){
        Predicate descriptionPredicate = cb.like(giftCertificateRoot.get(GIFT_CERTIFICATE_DESCRIPTION), "%" + params.get(DESCRIPTION) + "%");
        subQueryPredicates.add(descriptionPredicate);
      }
      if(!subQueryPredicates.isEmpty()){
        subquery.select(root).where(subQueryPredicates.toArray(Predicate[]::new));
      }
      giftCertificateCriteria.select(giftCertificateRoot).where(giftCertificateRoot.get(GIFT_CERTIFICATE_ID).in(subquery));
    }
    else{
      if(params.containsKey(NAME)){
      Predicate namePredicate = cb.like(giftCertificateRoot.get(GIFT_CERTIFICATE_NAME), "%" + params.get(NAME) + "%");
      predicates.add(namePredicate);
      }

    if (params.containsKey(DESCRIPTION)){
      Predicate descriptionPredicate = cb.like(giftCertificateRoot.get(GIFT_CERTIFICATE_DESCRIPTION), "%" + params.get(DESCRIPTION) + "%");
      predicates.add(descriptionPredicate);
    }
    if(!predicates.isEmpty()){
      giftCertificateCriteria.where(predicates.toArray(Predicate[]::new));
    }
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
      sort(sortBy, orderBy, cb, giftCertificateCriteria, giftCertificateRoot);
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
      TypedQuery<GiftCertificate> typedQuery = entityManager.createQuery(giftCertificateCriteria);
      typedQuery.setFirstResult(offset);
      typedQuery.setMaxResults(limit);
      return typedQuery;
    }
    return entityManager.createQuery(giftCertificateCriteria).setHint(QueryHints.PASS_DISTINCT_THROUGH, false);
  }

  private boolean isSortNeed(EnumMap<ParamName, String> params) {
    return params.containsKey(SORT_BY) || params.containsKey(ParamName.ORDER_BY);
  }

  private void sort(
          String sortBy, String orderBy, CriteriaBuilder cb, CriteriaQuery<GiftCertificate> cq, Root<GiftCertificate> from) {
    switch (sortBy.toLowerCase()) {
      case GIFT_CERTIFICATE_CREATE_DATE -> {
        switch (orderBy.toUpperCase()) {
          case ASCENDING_ORDER -> cq.orderBy(cb.asc(from.get("createDate")));
          case DESCENDING_ORDER -> cq.orderBy(cb.desc(from.get("createDate")));
          default -> throw new IllegalArgumentException("Invalid order: " + orderBy);
        }
      }
      case GIFT_CERTIFICATE_NAME -> {
        switch (orderBy.toUpperCase()) {
          case ASCENDING_ORDER -> cq.orderBy(cb.asc(from.get(GIFT_CERTIFICATE_NAME)));
          case DESCENDING_ORDER -> cq.orderBy(cb.desc(from.get(GIFT_CERTIFICATE_NAME)));
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
