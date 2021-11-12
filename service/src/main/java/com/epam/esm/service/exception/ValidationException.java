package com.epam.esm.service.exception;

import org.springframework.validation.Errors;

public class ValidationException extends RuntimeException {

  private Errors errors;

  private Object object;

  public ValidationException(Errors errors) {
    this.errors = errors;
  }

  public Errors getErrors() {
    return errors;
  }

  public ValidationException(String message, Throwable cause) {
    super(message, cause);
  }

  public ValidationException(String message) {
    super(message);
  }

  public ValidationException(String message, Object object) {
    super(message);
    this.object = object;
  }

  public ValidationException(Throwable cause) {
    super(cause);
  }
}
