package com.epam.esm.dao.impl;

import com.epam.esm.dao.RoleDao;
import com.epam.esm.entity.Role;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Repository
public class RoleDaoImpl implements RoleDao<Long> {

  @PersistenceContext private EntityManager entityManager;

  @Override
  public Role findByName(String name) {
    TypedQuery<Role> query =
        entityManager.createQuery("from roles r where r.name = ?1", Role.class);
    query.setParameter(1, name);
    return query.getSingleResult();
  }
}
