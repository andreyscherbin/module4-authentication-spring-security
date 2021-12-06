package com.epam.esm.web.security;

import com.epam.esm.web.exception.RestAuthenticationEntryPoint;
import com.epam.esm.web.filter.FilterChainExceptionHandler;
import com.epam.esm.web.security.jwt.JwtConfigurer;
import com.epam.esm.web.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  private static final String LOGIN_ENDPOINT = "/login";
  private static final String REFRESH_ENDPOINT = "/refresh";
  private static final String REGISTER_ENDPOINT = "/register";
  private static final String MAIN_ENTITY = "/certificates/**";
  private final JwtTokenProvider jwtTokenProvider;

  @Autowired private FilterChainExceptionHandler filterChainExceptionHandler;
  @Autowired private MessageSource messageSource;

  @Autowired
  public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
    this.jwtTokenProvider = jwtTokenProvider;
  }

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.httpBasic()
        .disable()
        .csrf()
        .disable()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .authorizeRequests()
        .antMatchers(LOGIN_ENDPOINT, REGISTER_ENDPOINT, REFRESH_ENDPOINT)
        .permitAll()
        .antMatchers(HttpMethod.GET, MAIN_ENTITY)
        .permitAll()
        .anyRequest()
        .authenticated()
        .and()
        .apply(new JwtConfigurer(jwtTokenProvider))
        .and()
        .addFilterBefore(filterChainExceptionHandler, LogoutFilter.class)
        .exceptionHandling()
        .authenticationEntryPoint(new RestAuthenticationEntryPoint(messageSource));
  }
}
