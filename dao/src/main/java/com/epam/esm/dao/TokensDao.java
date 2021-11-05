package com.epam.esm.dao;

import com.epam.esm.entity.Tokens;
import com.epam.esm.entity.User;

import java.util.List;

public interface TokensDao {

    Tokens create(Tokens tokens);

    void delete(User id);

    List<Tokens> findByAccessToken(String accessToken);

    List<Tokens> findByRefreshToken(String refreshToken);

    void update(User user, boolean validRefreshToken);
}
