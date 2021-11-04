package com.epam.esm.web.security.jwt;

import com.epam.esm.entity.Role;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.util.*;

@Component
public class JwtTokenProvider {

  private static final String BEARER_TOKEN = "Bearer ";
  private static final String AUTHORIZATION_HEADER = "Authorization";

  @Value("${jwt.token.secret}")
  private String secret;

  @Value("${jwt.token.expired}")
  private long validityInMilliseconds;

  @Autowired private UserDetailsService userDetailsService;

  @PostConstruct
  protected void init() {
    secret = Base64.getEncoder().encodeToString(secret.getBytes());
  }

  public String createToken(String username, Set<Role> roles) {
    List<Role> listRoles = new ArrayList<>(roles);
    Claims claims = Jwts.claims().setSubject(username);
    claims.put("roles", getRoleNames(listRoles));

    LocalDateTime now = LocalDateTime.now();
    LocalDateTime validity =
        now.plus(validityInMilliseconds, ChronoField.MILLI_OF_DAY.getBaseUnit());

    return Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(Date.from(now.toInstant(ZoneOffset.UTC)))
        .setExpiration(Date.from(validity.toInstant(ZoneOffset.UTC)))
        .signWith(SignatureAlgorithm.HS256, secret)
        .compact();
  }

  public Authentication getAuthentication(String token) {
    UserDetails userDetails = this.userDetailsService.loadUserByUsername(getUsername(token));
    return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
  }

  private String getUsername(String token) {
    return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
  }

  public String resolveToken(HttpServletRequest req) {
    String bearerToken = req.getHeader(AUTHORIZATION_HEADER);
    if (bearerToken != null && bearerToken.startsWith(BEARER_TOKEN)) {
      return bearerToken.substring(BEARER_TOKEN.length());
    }
    return null;
  }

  public boolean validateToken(String token) {
    try {
      Jws<Claims> claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
      return !claims.getBody().getExpiration().before(new Date());
    } catch (JwtException | IllegalArgumentException e) {
      throw new JwtAuthenticationException(("JWT token is expired or invalid"));
    }
  }

  private List<String> getRoleNames(List<Role> userRoles) {
    List<String> result = new ArrayList<>();
    userRoles.forEach(
        (role -> {
          result.add(role.getName());
        }));
    return result;
  }
}
