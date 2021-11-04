package com.epam.esm.dao;

import com.epam.esm.entity.Order;
import com.epam.esm.entity.User;

import javax.persistence.Query;
import java.util.List;
import java.util.Optional;

public interface UserDao<K> {

  List<User> find(Query query);

  Optional<User> findById(K id);

  Optional<User> findByUsername(String username);

  long countUsers();

  User create(User user);
}
