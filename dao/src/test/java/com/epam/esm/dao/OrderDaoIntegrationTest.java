package com.epam.esm.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.epam.esm.dao.builder.OrderBuilder;
import com.epam.esm.entity.*;

import java.math.BigDecimal;
import java.time.Period;
import java.util.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;

@SpringBootTest(classes = TestConfig.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles(value = "dev")
@Sql(
    value = {"/before-test-method.sql"},
    executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(
    value = {"/after-test-method.sql"},
    executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
public class OrderDaoIntegrationTest {

  @Autowired OrderDao<Long> orderDao;

  @Autowired OrderBuilder orderBuilder;

  EnumMap<ParamName, String> actualParams;

  Order order;

  @BeforeEach
  void setUp() {
    actualParams = new EnumMap<>(ParamName.class);
    order = new Order();
    order.setId(1L);
    order.setUser(new User(1L, "andrey", "12345"));
    List<GiftCertificate> certificates = new ArrayList<>();
    GiftCertificate certificate = new GiftCertificate(1L);
    certificate.setName("name1");
    certificate.setDescription("description1");
    certificate.setPrice(new BigDecimal("10.99"));
    certificate.setDuration(Period.ofDays(10));
    certificate.setTags(
        List.of(
            new Tag(1L, "tag1"), new Tag(2L, "tag2"), new Tag(3L, "tag3"), new Tag(4L, "tag4")));

    certificates.add(certificate);
    certificate = new GiftCertificate(2L);
    certificate.setName("name2");
    certificate.setDescription("description2");
    certificate.setPrice(new BigDecimal("10.99"));
    certificate.setDuration(Period.ofDays(100));
    certificate.setTags(List.of(new Tag(1L, "tag1"), new Tag(2L, "tag2"), new Tag(5L, "tag5")));
    certificates.add(certificate);
    certificate = new GiftCertificate(3L);
    certificate.setName("name3");
    certificate.setDescription("description3");
    certificate.setPrice(new BigDecimal("10.99"));
    certificate.setDuration(Period.ofDays(5));
    certificate.setTags(List.of(new Tag(6L, "tag6"), new Tag(7L, "tag7"), new Tag(8L, "tag8")));
    certificates.add(certificate);
    certificate = new GiftCertificate(4L);
    certificate.setName("name4");
    certificate.setDescription("description4");
    certificate.setPrice(new BigDecimal("10.99"));
    certificate.setDuration(Period.ofDays(1));
    certificate.setTags(List.of(new Tag(1L, "tag1"), new Tag(11L, "tag11"), new Tag(12L, "tag12")));
    certificates.add(certificate);
    order.setCertificates(certificates);
    order.setCost(new BigDecimal("43.96"));
  }

  @AfterEach
  void tearDown() {
    order = null;
  }

  @Transactional
  @Test
  void createTest() {
    order.setId(0);
    long id = orderDao.create(order).getId();
    order.setCreateDate(null);
    Optional<Order> actual = orderDao.findById(id);
    for (GiftCertificate gc : actual.get().getCertificates()) {
      gc.setCreateDate(null);
    }
    actual.get().setCreateDate(null);
    assertEquals(order, actual.get());
  }

  @Test
  void findAllTest() {
    Query query = orderBuilder.buildQuery(actualParams);
    List<Order> actual = orderDao.find(query);
    for (GiftCertificate gc : actual.get(0).getCertificates()) {
      gc.setCreateDate(null);
    }
    actual.get(0).setCreateDate(null);
    assertEquals(order, actual.get(0));
  }

  @Test
  void findAllWithPagingTest() {
    actualParams.put(ParamName.PAGE_SIZE, "1");
    actualParams.put(ParamName.PAGE_NUM, "1");
    Query query = orderBuilder.buildQuery(actualParams);
    List<Order> actual = orderDao.find(query);
    for (GiftCertificate gc : actual.get(0).getCertificates()) {
      gc.setCreateDate(null);
    }
    actual.get(0).setCreateDate(null);
    assertEquals(List.of(order), actual);
  }

  @Test
  void findByIdTest() {
    Optional<Order> actual = orderDao.findById(1L);
    for (GiftCertificate gc : actual.get().getCertificates()) {
      gc.setCreateDate(null);
    }
    actual.get().setCreateDate(null);
    assertEquals(order, actual.get());
  }

  @Test
  void findOrdersByUserTest() {
    actualParams.put(ParamName.ID, "1");
    Query query = orderBuilder.buildQuery(actualParams);
    List<Order> actual = orderDao.find(query);
    for (GiftCertificate gc : actual.get(0).getCertificates()) {
      gc.setCreateDate(null);
    }
    actual.get(0).setCreateDate(null);
    assertEquals(List.of(order), actual);
  }

  @Test
  void findOrderInfoByUser() {
    Optional<OrderInfo> actual = orderDao.findOrderInfoByUser(1L, 1L);
    actual.get().setCreateDate(null);
    OrderInfo expected = new OrderInfo();
    expected.setCost(new BigDecimal("43.96"));
    assertEquals(expected, actual.get());
  }
}
