package com.epam.esm.web.exception;

import org.springframework.http.HttpStatus;

public class ResourceException extends RuntimeException {

  private long[] resourceId;
  private HttpStatus status;
  private int resourceType;

  public ResourceException(long[] resourceId) {
    this.resourceId = resourceId;
  }

  public long[] getResourceId() {
    return resourceId;
  }

  public HttpStatus getStatus() {
    return status;
  }

  public int getResourceType() {
    return resourceType;
  }

  public void setStatus(HttpStatus status) {
    this.status = status;
  }

  public ResourceException(String message, Throwable cause) {
    super(message, cause);
  }

  public ResourceException(
      String message, HttpStatus status, int resourceType, long... resourceId) {
    super(message);
    this.resourceId = resourceId;
    this.status = status;
    this.resourceType = resourceType;
  }

  public ResourceException(Throwable cause) {
    super(cause);
  }
}
