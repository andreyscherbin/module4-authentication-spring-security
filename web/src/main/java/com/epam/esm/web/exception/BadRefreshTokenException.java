package com.epam.esm.web.exception;

public class BadRefreshTokenException extends RuntimeException {

  public BadRefreshTokenException(String msg) {
    super(msg);
  }

  public BadRefreshTokenException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
