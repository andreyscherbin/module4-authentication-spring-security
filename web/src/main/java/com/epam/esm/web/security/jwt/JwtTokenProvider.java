package com.epam.esm.web.security.jwt;

import com.epam.esm.entity.User;
import com.epam.esm.service.TokenService;
import com.epam.esm.service.UserService;
import com.epam.esm.web.exception.BadRefreshTokenException;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@Component
public class JwtTokenProvider {

  private static final String BEARER_TOKEN = "Bearer ";
  private static final String AUTHORIZATION_HEADER = "Authorization";

  @Value("${jwt.token.secret}")
  private String secret;

  @Value("${jwt.access_token.expired}")
  private long validityInMillisecondsAccessToken;

  @Value("${jwt.refresh_token.expired}")
  private long validityInMillisecondsRefreshToken;

  @Autowired private UserDetailsService userDetailsService;

  @Autowired private TokenService tokenService;

  @Autowired private UserService userService;

  @PostConstruct
  protected void init() {
    secret = Base64.getEncoder().encodeToString(secret.getBytes());
  }

  private String createToken(String username, boolean isRefreshToken) {
    Claims claims = Jwts.claims().setSubject(username);

    Date now = new Date();
    Date validity;
    if (!isRefreshToken) {
      validity = new Date(validityInMillisecondsAccessToken + now.getTime());
    } else {
      validity = new Date(validityInMillisecondsRefreshToken + now.getTime());
    }

    return Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(now)
        .setExpiration(validity)
        .signWith(SignatureAlgorithm.HS256, secret)
        .compact();
  }

  public String createRefreshToken(String username) {
    return createToken(username, true);
  }

  public String createAccessToken(String username) {
    return createToken(username, false);
  }

  public Authentication getAuthentication(String token) {
    UserDetails userDetails = this.userDetailsService.loadUserByUsername(getUsername(token));
    return new UsernamePasswordAuthenticationToken(
        userDetails, token, userDetails.getAuthorities());
  }

  public String getUsername(String token) {
    return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
  }

  public String resolveToken(HttpServletRequest req) {
    String bearerToken = req.getHeader(AUTHORIZATION_HEADER);
    if (bearerToken != null && bearerToken.startsWith(BEARER_TOKEN)) {
      return bearerToken.substring(BEARER_TOKEN.length());
    }
    return null;
  }

  public boolean validateAccessToken(String accessToken) {
    try {
      Jws<Claims> claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(accessToken);
      if (claims.getBody().getExpiration().before(new Date())) {
        throw new JwtException("access token is expired");
      }
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      throw new JwtAuthenticationException(("invalid.access_token"));
    }
  }

  public boolean validateRefreshToken(String refreshToken) {
    try {
      Jws<Claims> claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(refreshToken);
      if (claims.getBody().getExpiration().before(new Date())) {
        throw new JwtException("refresh token is expired");
      }
      String username = getUsername(refreshToken);
      Optional<User> user = userService.findByUsername(username);
      if (user.isEmpty()) {
        throw new UsernameNotFoundException("User with username: " + username + "not found");
      }
      if (tokenService.findByRefreshTokenAndUser(refreshToken, user.get()).isEmpty()) {
        throw new BadRefreshTokenException(("invalid.refresh_token"));
      }
      return true;
    } catch (JwtException | IllegalArgumentException | UsernameNotFoundException e) {
      throw new BadRefreshTokenException(("invalid.refresh_token"));
    }
  }
}
