package com.epam.esm.service;

import com.epam.esm.entity.Tokens;
import com.epam.esm.entity.User;

import java.util.Collection;
import java.util.List;

public interface TokenService {

  Tokens create(Tokens tokens);

  void delete(User user);

  List<Tokens> findByAccessToken(String accessToken);

  List<Tokens> findByRefreshToken(String refreshToken);

  void invalidRefreshTokens(Tokens newTokens);
}
