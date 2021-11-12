package com.epam.esm.entity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class AuthenticationRequestDto {

  @Size(max = 30)
  @NotBlank
  private String username;

  @NotBlank
  @Size(max = 80)
  private String password;

  public AuthenticationRequestDto(String username, String password) {
    this.username = username;
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    AuthenticationRequestDto that = (AuthenticationRequestDto) o;

    if (username != null ? !username.equals(that.username) : that.username != null) return false;
    return password != null ? password.equals(that.password) : that.password == null;
  }

  @Override
  public int hashCode() {
    int result = username != null ? username.hashCode() : 0;
    result = 31 * result + (password != null ? password.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "AuthenticationRequestDto{"
        + "username='"
        + username
        + '\''
        + ", password='"
        + password
        + '\''
        + '}';
  }
}
