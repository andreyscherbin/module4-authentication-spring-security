package com.epam.esm.web.security;

import com.epam.esm.entity.User;
import com.epam.esm.service.UserService;
import com.epam.esm.web.security.jwt.JwtUser;
import com.epam.esm.web.security.jwt.JwtUserFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class JwtUserDetailsService implements UserDetailsService {

  Logger log = LoggerFactory.getLogger(JwtUserDetailsService.class);

  private final UserService userService;

  @Autowired
  public JwtUserDetailsService(UserService userService) {
    this.userService = userService;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Optional<User> user = userService.findByUsername(username);
    if (user.isEmpty()) {
      throw new UsernameNotFoundException("User with userName: " + username + " not found");
    }

    JwtUser jwtUser = JwtUserFactory.create(user.get());
    log.info("IN loadUserByUsername - user with username: {} successfully loaded", username);

    return jwtUser;
  }
}
