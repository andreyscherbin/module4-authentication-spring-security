package com.epam.esm.entity;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class RegisterResult {

  private final @NonNull User user;

  private final @NonNull String accessToken;

  private final @NonNull String refreshToken;
}
