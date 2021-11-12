package com.epam.esm.web.exception;

import org.springframework.security.core.AuthenticationException;

public class UserAlreadyExistAuthenticationException extends AuthenticationException {

  /**
   * Constructs a <code>UserAlreadyExistAuthenticationException</code> with the specified message.
   *
   * @param msg the detail message.
   */
  public UserAlreadyExistAuthenticationException(String msg) {
    super(msg);
  }

  /**
   * Constructs a {@code UserAlreadyExistAuthenticationException} with the specified message and
   * root cause.
   *
   * @param msg the detail message.
   * @param cause root cause
   */
  public UserAlreadyExistAuthenticationException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
