package com.epam.esm.service.impl;

import com.epam.esm.dao.TokensDao;
import com.epam.esm.entity.Tokens;
import com.epam.esm.entity.User;
import com.epam.esm.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TokenServiceImpl implements TokenService {

  private final TokensDao tokensDao;

  @Autowired
  public TokenServiceImpl(TokensDao tokensDao) {
    this.tokensDao = tokensDao;
  }

  @Override
  public Tokens create(Tokens tokens) {
    return tokensDao.create(tokens);
  }

  @Override
  public void delete(User user) {
    tokensDao.delete(user);
  }

  @Override
  public List<Tokens> findByRefreshTokenAndUser(String refreshToken, User user) {
    return tokensDao.findByRefreshTokenAndUser(refreshToken, user);
  }

  @Override
  public void invalidateRefreshTokens(Tokens newTokens) {
    tokensDao.update(newTokens.getUser(), false);
  }
}
