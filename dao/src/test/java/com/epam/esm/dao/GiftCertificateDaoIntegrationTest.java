package com.epam.esm.dao;

import com.epam.esm.dao.builder.CertificateBuilder;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.ParamName;
import com.epam.esm.entity.Tag;
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
import java.math.BigDecimal;
import java.time.Period;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Optional;

import static com.epam.esm.entity.ParamName.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {TestConfig.class})
@ExtendWith(SpringExtension.class)
@ActiveProfiles(value = "dev")
@Sql(
    value = {"/before-test-method.sql"},
    executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(
    value = {"/after-test-method.sql"},
    executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
class GiftCertificateDaoIntegrationTest {

  @Autowired GiftCertificateDao<Long> giftCertificateDao;

  @Autowired CertificateBuilder certificateBuilder;

  GiftCertificate giftCertificate;

  EnumMap<ParamName, String> actualParams;
  EnumMap<ParamName, String> expectedParams;
  List<Tag> tags;
  List<Tag> emptyTags;

  @BeforeEach
  void setUp() {
    tags = List.of(new Tag("tag1"));
    emptyTags = List.of();
    actualParams = new EnumMap<>(ParamName.class);
    expectedParams = new EnumMap<>(ParamName.class);
    giftCertificate = new GiftCertificate();
    giftCertificate.setId(4L);
    giftCertificate.setName("name4");
    giftCertificate.setDescription("description4");
    giftCertificate.setPrice(new BigDecimal("10.99"));
    giftCertificate.setDuration(Period.ofDays(1));
    giftCertificate.setTags(
        List.of(new Tag(1L, "tag1"), new Tag(11L, "tag11"), new Tag(12L, "tag12")));
  }

  @AfterEach
  void tearDown() {}

  @Test
  void findAll() {
    Query query = certificateBuilder.buildQuery(actualParams, emptyTags);
    List<GiftCertificate> giftCertificateList = giftCertificateDao.find(query);
    assertEquals(4, giftCertificateList.size());
  }

  @Test
  void findAllAndSort() {
    actualParams.put(SORT_BY, "create_date");
    actualParams.put(ORDER_BY, "asc");
    Query queryForSort = certificateBuilder.buildQuery(actualParams, emptyTags);
    Query queryWithoutSort = certificateBuilder.buildQuery(expectedParams, emptyTags);
    List<GiftCertificate> expected = giftCertificateDao.find(queryWithoutSort);
    expected.sort(Comparator.comparing(GiftCertificate::getCreateDate));
    List<GiftCertificate> actual = giftCertificateDao.find(queryForSort);
    assertEquals(expected, actual);
  }

  @Test
  void findById() {
    Optional<GiftCertificate> actual = giftCertificateDao.findById(4L);
    actual.get().setLastUpdateDate(null);
    actual.get().setCreateDate(null);
    assertEquals(Optional.of(giftCertificate), actual);
  }

  @Test
  void findByTag() {
    Query query = certificateBuilder.buildQuery(actualParams, tags);
    List<GiftCertificate> actual = giftCertificateDao.find(query);
    assertTrue(
        actual.stream()
            .allMatch(
                (gc) -> gc.getTags().stream().anyMatch((tag) -> tag.getName().equals("tag1"))));
  }

  @Test
  void findByTagAndSort() {
    actualParams.put(SORT_BY, "create_date");
    actualParams.put(ORDER_BY, "asc");

    Query actualQuery = certificateBuilder.buildQuery(actualParams, tags);
    Query expectedQuery = certificateBuilder.buildQuery(expectedParams, tags);
    List<GiftCertificate> expected = giftCertificateDao.find(expectedQuery);
    expected.sort(Comparator.comparing(GiftCertificate::getCreateDate));
    List<GiftCertificate> actual = giftCertificateDao.find(actualQuery);
    assertEquals(expected, actual);
    assertTrue(
        actual.stream()
            .allMatch(
                (gc) -> gc.getTags().stream().anyMatch((tag) -> tag.getName().equals("tag1"))));
    assertEquals(expected, actual);
  }

  @Test
  void findByNameAndDescription() {
    actualParams.put(NAME, "name1");
    actualParams.put(DESCRIPTION, "description1");
    Query query = certificateBuilder.buildQuery(actualParams, emptyTags);
    List<GiftCertificate> actual = giftCertificateDao.find(query);
    assertTrue(
        actual.stream()
            .allMatch(
                (gc) ->
                    gc.getName().contains("name") && gc.getDescription().contains("description")));
  }

  @Test
  void findByNameAndDescriptionAndSort() {
    actualParams.put(NAME, "name1");
    actualParams.put(DESCRIPTION, "description1");
    actualParams.put(SORT_BY, "create_date");
    actualParams.put(ORDER_BY, "asc");
    expectedParams.put(NAME, "name1");
    expectedParams.put(DESCRIPTION, "description1");

    Query actualQuery = certificateBuilder.buildQuery(actualParams, emptyTags);
    Query expectedQuery = certificateBuilder.buildQuery(expectedParams, emptyTags);
    List<GiftCertificate> expected = giftCertificateDao.find(expectedQuery);
    expected.sort(Comparator.comparing(GiftCertificate::getCreateDate));
    List<GiftCertificate> actual = giftCertificateDao.find(actualQuery);
    assertTrue(
        actual.stream()
            .allMatch(
                (gc) ->
                    gc.getName().contains("name") && gc.getDescription().contains("description")));
    assertEquals(expected, actual);
  }

  @Test
  void findByTagAndNameAndDescription() {
    actualParams.put(NAME, "name1");
    actualParams.put(DESCRIPTION, "description1");
    Query query = certificateBuilder.buildQuery(actualParams, tags);
    List<GiftCertificate> actual = giftCertificateDao.find(query);
    assertTrue(
        actual.stream()
            .allMatch(
                (gc) ->
                    gc.getName().contains("name")
                        && gc.getDescription().contains("description")
                        && gc.getTags().stream().anyMatch((tag) -> tag.getName().equals("tag1"))));
  }

  @Test
  void findByTagAndNameAndDescriptionAndSort() {
    actualParams.put(NAME, "name1");
    actualParams.put(DESCRIPTION, "description1");
    actualParams.put(SORT_BY, "create_date");
    actualParams.put(ORDER_BY, "desc");

    expectedParams.put(NAME, "name1");
    expectedParams.put(DESCRIPTION, "description1");
    Query actualQuery = certificateBuilder.buildQuery(actualParams, tags);
    Query expectedQuery = certificateBuilder.buildQuery(expectedParams, tags);
    List<GiftCertificate> expected = giftCertificateDao.find(expectedQuery);
    expected.sort(Comparator.comparing(GiftCertificate::getCreateDate).reversed());
    List<GiftCertificate> actual = giftCertificateDao.find(actualQuery);
    assertTrue(
        actual.stream()
            .allMatch(
                (gc) ->
                    gc.getName().contains("name")
                        && gc.getDescription().contains("description")
                        && gc.getTags().stream().anyMatch((tag) -> tag.getName().equals("tag1"))));
    assertEquals(expected, actual);
  }

  @Test
  void findBySeverTagsTest() {
    List<Tag> severalTags = List.of(new Tag("tag1"), new Tag("tag2"), new Tag("tag3"));
    Query query = certificateBuilder.buildQuery(actualParams, severalTags);
    List<GiftCertificate> actual = giftCertificateDao.find(query);
    assertTrue(
        actual.stream()
            .allMatch(
                (gc) ->
                    gc.getTags().stream()
                        .anyMatch(
                            (tag) ->
                                tag.getName().equals("tag1")
                                    || tag.getName().equals("tag2")
                                    || tag.getName().equals("tag3"))));
  }

  @Transactional
  @Test
  void delete() {
    giftCertificateDao.delete(giftCertificate.getId());
    Optional<GiftCertificate> deletedGiftCertificate =
        giftCertificateDao.findById(giftCertificate.getId());
    assertFalse(deletedGiftCertificate.isPresent());
  }

  @Transactional
  @Test
  void update() {
    giftCertificate.setName("updatedName");
    giftCertificate.setDescription("updatedDescription");
    giftCertificate.setPrice(new BigDecimal("777.77"));
    giftCertificate.setTags(
        List.of(new Tag(8L, "tag8"), new Tag(9L, "tag9"), new Tag(10L, "tag10")));
    giftCertificateDao.update(giftCertificate);
    Optional<GiftCertificate> expected = Optional.of(giftCertificate);
    Optional<GiftCertificate> actual = giftCertificateDao.findById(giftCertificate.getId());
    actual.get().setLastUpdateDate(null);
    actual.get().setCreateDate(null);
    assertEquals(expected, actual);
  }

  @Transactional
  @Test
  void updatePrice100() {
    giftCertificateDao.update(new BigDecimal("100"), 1L);
    Optional<GiftCertificate> actual = giftCertificateDao.findById(1L);
    assertEquals("100", actual.get().getPrice().toString());
  }

  @Transactional
  @Test
  void updatePrice200() {
    giftCertificateDao.update(new BigDecimal("200.23"), 4L);
    Optional<GiftCertificate> actual = giftCertificateDao.findById(4L);
    assertEquals("200.23", actual.get().getPrice().toString());
  }

  @Transactional
  @Test
  void updateDuration100() {
    giftCertificateDao.update(Period.parse("P100D"), 1L);
    Optional<GiftCertificate> actual = giftCertificateDao.findById(1L);
    assertEquals(100, actual.get().getDuration().getDays());
  }

  @Transactional
  @Test
  void updateDuration200() {
    giftCertificateDao.update(Period.parse("P200D"), 3L);
    Optional<GiftCertificate> actual = giftCertificateDao.findById(3L);
    assertEquals(200, actual.get().getDuration().getDays());
  }

  @Transactional
  @Test
  void create() {
    giftCertificate.setId(0);
    GiftCertificate actual = giftCertificateDao.create(giftCertificate);
    Optional<GiftCertificate> expected = giftCertificateDao.findById(actual.getId());
    assertEquals(expected.get(), actual);
  }
}
