package com.epam.esm.dao.impl;

import com.epam.esm.dao.UserDao;
import com.epam.esm.entity.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.Optional;

@Repository
public class UserDaoImpl implements UserDao<Long> {

  @PersistenceContext private EntityManager entityManager;

  @Override
  public List<User> find(Query query) {
    return query.getResultList();
  }

  @Override
  public Optional<User> findById(Long id) {
    User user = entityManager.find(User.class, id);
    return user != null ? Optional.of(user) : Optional.empty();
  }

  @Override
  public long countUsers() {
    Query query = entityManager.createQuery("select count(*) from users");
    return (long) query.getSingleResult();
  }

  @Override
  public User create(User user) {
    entityManager.persist(user);
    return user;
  }
}
