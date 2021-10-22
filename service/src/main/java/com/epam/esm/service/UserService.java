package com.epam.esm.service;

import com.epam.esm.entity.User;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserService {

  Optional<User> findById(long id);

  List<User> find(Map<String, String> params);

  long getTotalRows();

  User register(User user);
}
