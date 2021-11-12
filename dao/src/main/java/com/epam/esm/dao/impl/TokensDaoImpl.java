package com.epam.esm.dao.impl;

import com.epam.esm.dao.TokensDao;
import com.epam.esm.entity.Tokens;
import com.epam.esm.entity.TokensTable;
import com.epam.esm.entity.User;
import com.epam.esm.entity.UserTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

import static com.epam.esm.entity.TokensTable.*;

@Repository
public class TokensDaoImpl implements TokensDao {

  Logger logger = LoggerFactory.getLogger(TokensDaoImpl.class);

  @PersistenceContext private EntityManager entityManager;

  @Transactional
  @Override
  public Tokens create(Tokens tokens) {
    entityManager.persist(tokens);
    return tokens;
  }

  @Transactional
  @Override
  public void delete(User user) {
    Query query = entityManager.createQuery("DELETE from tokens t WHERE t.user = :id");
    int deletedTokens = query.setParameter(UserTable.USER_ID, user).executeUpdate();
    logger.info("number of deleted tokens is " + deletedTokens);
  }

  @Override
  public List<Tokens> findByRefreshTokenAndUser(String refreshToken, User user) {
    TypedQuery<Tokens> query =
        entityManager.createQuery(
            "SELECT t FROM tokens t WHERE t.refreshToken = :refresh_token AND t.validRefreshToken = true AND t.user = :id_user",
            Tokens.class);
    query.setParameter(TOKENS_REFRESH_TOKEN, refreshToken);
    query.setParameter(TOKENS_ID_USER, user);
    return query.getResultList();
  }

  @Transactional
  @Override
  public void update(User user, boolean validRefreshToken) {
    Query query =
        entityManager.createQuery(
            "UPDATE tokens t SET t.validRefreshToken = :valid_refresh_token  WHERE t.user = :id_user");
    int updatedTokens =
        query
            .setParameter(TokensTable.TOKENS_ID_USER, user)
            .setParameter(TokensTable.TOKENS_VALID_REFRESH_TOKEN, validRefreshToken)
            .executeUpdate();
    logger.info("number of updated tokens is " + updatedTokens);
  }
}
