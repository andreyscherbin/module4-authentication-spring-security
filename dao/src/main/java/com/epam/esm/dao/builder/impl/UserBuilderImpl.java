package com.epam.esm.dao.builder.impl;

import com.epam.esm.dao.builder.UserBuilder;
import com.epam.esm.entity.ParamName;
import com.epam.esm.entity.User;
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
import static com.epam.esm.entity.UserTable.USER_NAME;

@Component
public class UserBuilderImpl implements UserBuilder {

    private static final int DEFAULT_LIMIT = 100000;
    private static final int DEFAULT_OFFSET = 0;
    private static final String DEFAULT_ORDER_BY = DESCENDING_ORDER;

    @PersistenceContext
    private EntityManager em;

    @Override
    public Query buildQuery(EnumMap<ParamName, String> params) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> from = cq.from(User.class);
        CriteriaQuery<User> select = cq.select(from);
        if (params.containsKey(NAME)) {
        Predicate namePredicate = cb.like(from.get(USER_NAME), "%" + params.get(NAME) + "%");
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
            sort(sortBy, orderBy, cb, select, from);
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
            TypedQuery<User> typedQuery = em.createQuery(select);
            typedQuery.setFirstResult(offset);
            typedQuery.setMaxResults(limit);
            return typedQuery;
        }
        return em.createQuery(select);
    }

    private boolean isSortNeed(EnumMap<ParamName, String> params) {
        return params.containsKey(SORT_BY) || params.containsKey(ParamName.ORDER_BY);
    }

    private void sort(
            String sortBy, String orderBy, CriteriaBuilder cb, CriteriaQuery<User> cq, Root<User> from) {
        switch (sortBy.toLowerCase()) {
            case USER_NAME -> {
                switch (orderBy.toUpperCase()) {
                    case ASCENDING_ORDER -> cq.orderBy(cb.asc(from.get(USER_NAME)));
                    case DESCENDING_ORDER -> cq.orderBy(cb.desc(from.get(USER_NAME)));
                    default -> throw new IllegalArgumentException("Invalid order: " + orderBy);
                }
                break;
            }
            default -> throw new IllegalArgumentException("Invalid column: " + sortBy);
        }
    }

    private boolean isPageNeed(EnumMap<ParamName, String> params) {
        return params.containsKey(ParamName.PAGE_SIZE) || params.containsKey(ParamName.PAGE_NUM);
    }
}
