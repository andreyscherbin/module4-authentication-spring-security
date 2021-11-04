package com.epam.esm.web.api;

import com.epam.esm.entity.AuthenticationRequestDto;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.User;
import com.epam.esm.service.UserService;
import com.epam.esm.web.exception.UserAlreadyExistAuthenticationException;
import com.epam.esm.web.hateoas.HateoasSupportUser;
import com.epam.esm.web.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class AuthenticationRestController {

  private final AuthenticationManager authenticationManager;

  private final JwtTokenProvider jwtTokenProvider;

  private final UserService userService;

  @Autowired
  public AuthenticationRestController(
      AuthenticationManager authenticationManager,
      JwtTokenProvider jwtTokenProvider,
      UserService userService) {
    this.authenticationManager = authenticationManager;
    this.jwtTokenProvider = jwtTokenProvider;
    this.userService = userService;
  }

  @PostMapping(value = "/login", consumes = "application/json")
  public ResponseEntity login(@RequestBody @Valid AuthenticationRequestDto requestDto) {
    try {
      String username = requestDto.getUsername();
      authenticationManager.authenticate(
          (new UsernamePasswordAuthenticationToken(username, requestDto.getPassword())));
      Optional<User> user = userService.findByUsername(username);
      if (user.isEmpty()) {
        throw new UsernameNotFoundException("User with username: " + username + "not found");
      }
      String token = jwtTokenProvider.createToken(username, user.get().getRoles());
      Map<Object, Object> response = new HashMap<>();
      response.put("username", username);
      response.put("token", token);
      return ResponseEntity.ok(response);
    } catch (AuthenticationException e) {
      throw new BadCredentialsException("login.error");
    }
  }

  @PostMapping(value = "/register", consumes = "application/json")
  @ResponseStatus(HttpStatus.CREATED)
  public EntityModel<User> register(@RequestBody @Valid AuthenticationRequestDto requestDto) {
    try {
      String username = requestDto.getUsername();
      String password = requestDto.getPassword();
      Optional<User> user = userService.findByUsername(username);
      if (!user.isEmpty()) {
        throw new UserAlreadyExistAuthenticationException(
            "User with username: " + username + "already exists");
      }
      User registeredUser = userService.register(username, password);
      return HateoasSupportUser.getModel(registeredUser);
    } catch (AuthenticationException e) {
      throw new BadCredentialsException("register.error");
    }
  }
}
