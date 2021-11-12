package com.epam.esm.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity(name = "tokens")
@Table(name = "tokens")
public class Tokens {

  @JsonIgnore
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @Transient
  private String accessToken;

  @Column(name = "refresh_token")
  @Size(max = 256)
  @NotBlank
  private String refreshToken;

  @JsonIgnore
  @Column(name = "valid_refresh_token")
  private boolean validRefreshToken = true;

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "id_user")
  private User user;

  public Tokens(String newAccessToken, String newRefreshToken, User user) {
    this.accessToken = newAccessToken;
    this.refreshToken = newRefreshToken;
    this.user = user;
  }

  public Tokens() {}

  public Tokens(String refreshToken, User user) {
    this.refreshToken = refreshToken;
    this.user = user;
  }

  public boolean isValidRefreshToken() {
    return validRefreshToken;
  }

  public void setValidRefreshToken(boolean validRefreshToken) {
    this.validRefreshToken = validRefreshToken;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  @Override
  public String toString() {
    return "Tokens{"
        + "id="
        + id
        + ", accessToken='"
        + accessToken
        + '\''
        + ", refreshToken='"
        + refreshToken
        + '\''
        + ", validRefreshToken="
        + validRefreshToken
        + ", user="
        + user
        + '}';
  }
}
