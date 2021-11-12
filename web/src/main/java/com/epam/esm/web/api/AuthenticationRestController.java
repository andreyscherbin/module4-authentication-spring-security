package com.epam.esm.web.api;

import com.epam.esm.entity.*;
import com.epam.esm.service.TokenService;
import com.epam.esm.service.UserService;
import com.epam.esm.web.exception.UserAlreadyExistAuthenticationException;
import com.epam.esm.web.hateoas.HateoasSupportUser;
import com.epam.esm.web.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
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
import java.util.Optional;

@RestController
public class AuthenticationRestController {

  private final AuthenticationManager authenticationManager;

  private final JwtTokenProvider jwtTokenProvider;

  private final UserService userService;

  private final TokenService tokenService;

  @Autowired
  public AuthenticationRestController(
      AuthenticationManager authenticationManager,
      JwtTokenProvider jwtTokenProvider,
      UserService userService,
      TokenService tokenService) {
    this.authenticationManager = authenticationManager;
    this.jwtTokenProvider = jwtTokenProvider;
    this.userService = userService;
    this.tokenService = tokenService;
  }

  @PostMapping(value = "/login", consumes = "application/json")
  public EntityModel<LoginResult> login(@RequestBody @Valid AuthenticationRequestDto requestDto) {
    try {
      String username = requestDto.getUsername();
      String password = requestDto.getPassword();
      Optional<User> user = userService.findByUsername(username);
      if (user.isEmpty()) {
        throw new UsernameNotFoundException("User with username: " + username + "not found");
      }
      authenticationManager.authenticate(
          (new UsernamePasswordAuthenticationToken(username, password)));
      String accessToken = jwtTokenProvider.createAccessToken(username);
      String refreshToken = jwtTokenProvider.createRefreshToken(username);
      tokenService.delete(user.get());
      tokenService.create(new Tokens(refreshToken, user.get()));
      return HateoasSupportUser.getModel(new LoginResult(user.get(), accessToken, refreshToken));
    } catch (AuthenticationException e) {
      throw new BadCredentialsException("login.error");
    }
  }

  @PostMapping(value = "/register", consumes = "application/json")
  @ResponseStatus(HttpStatus.CREATED)
  public EntityModel<RegisterResult> register(
      @RequestBody @Valid AuthenticationRequestDto requestDto) {
    try {
      String username = requestDto.getUsername();
      String password = requestDto.getPassword();
      Optional<User> user = userService.findByUsername(username);
      if (user.isPresent()) {
        throw new UserAlreadyExistAuthenticationException(
            "User with username: " + username + "already exists");
      }
      User registeredUser = userService.register(username, password);
      authenticationManager.authenticate(
          (new UsernamePasswordAuthenticationToken(username, password)));
      String accessToken = jwtTokenProvider.createAccessToken(username);
      String refreshToken = jwtTokenProvider.createRefreshToken(username);
      tokenService.create(new Tokens(refreshToken, registeredUser));
      return HateoasSupportUser.getModel(
          new RegisterResult(registeredUser, accessToken, refreshToken));
    } catch (AuthenticationException e) {
      throw new BadCredentialsException("register.error");
    }
  }

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping(value = "/refresh", consumes = "application/json")
  public Tokens refresh(@RequestBody @Valid Tokens tokens) {
    String refreshToken = tokens.getRefreshToken();
    jwtTokenProvider.validateRefreshToken(refreshToken);
    String username = jwtTokenProvider.getUsername(refreshToken);
    Optional<User> user = userService.findByUsername(username);
    String newAccessToken = jwtTokenProvider.createAccessToken(username);
    String newRefreshToken = jwtTokenProvider.createRefreshToken(username);
    Tokens newTokens = new Tokens(newAccessToken, newRefreshToken, user.get());
    tokenService.invalidateRefreshTokens(newTokens);
    tokenService.create(newTokens);
    return newTokens;
  }
}
