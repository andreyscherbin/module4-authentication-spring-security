package com.epam.esm.dao.impl;

import com.epam.esm.dao.UserDao;
import com.epam.esm.entity.User;
import com.epam.esm.entity.UserTable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
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
  public Optional<User> findByUsername(String username) {
    try {
      TypedQuery<User> query =
          entityManager.createQuery("select u from users u  where  u.name = :name", User.class);
      query.setParameter(UserTable.USER_NAME, username);
      return Optional.of(query.getSingleResult());
    } catch (NoResultException ex) {
      return Optional.empty();
    }
  }

  @Override
  public long countUsers() {
    Query query = entityManager.createQuery("select count(*) from users");
    return (long) query.getSingleResult();
  }

  @Override
  @Transactional
  public User create(User user) {
    entityManager.persist(user);
    return user;
  }
}
