package com.epam.esm.web.exception;

import java.util.List;

public class ApiError {

  private List<String> errorMessages;
  private String errorCode;

  public ApiError(List<String> errorMessage, String errorCode) {
    this.errorMessages = errorMessage;
    this.errorCode = errorCode;
  }

  public List<String> getErrorMessages() {
    return errorMessages;
  }

  public void setErrorMessages(List<String> errorMessages) {
    this.errorMessages = errorMessages;
  }

  public String getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(String errorCode) {
    this.errorCode = errorCode;
  }
}
