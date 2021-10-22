package com.epam.esm.dao;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("spring.datasource")
public class DBConfiguration {

  private String driverClassName;
  private String url;
  private String username;
  private String password;
}
