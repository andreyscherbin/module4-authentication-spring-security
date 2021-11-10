package com.epam.esm.dao;

import com.epam.esm.entity.Tokens;
import com.epam.esm.entity.User;

import java.util.List;

public interface TokensDao {

    Tokens create(Tokens tokens);

    void delete(User id);

    List<Tokens> findByRefreshTokenAndUser(String refreshToken, User user);

    void update(User user, boolean validRefreshToken);
}
