package com.epam.esm.service;

import com.epam.esm.dao.GiftCertificateDao;
import com.epam.esm.dao.builder.CertificateBuilder;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.GiftCertificateTable;
import com.epam.esm.entity.Tag;
import com.epam.esm.service.exception.ValidationException;
import com.epam.esm.service.impl.GiftCertificateServiceImpl;
import com.epam.esm.service.validator.GiftCertificateValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Period;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GiftCertificateServiceImplTest {

  @Mock GiftCertificateDao<Long> giftCertificateDao;

  @Mock CertificateBuilder certificateBuilder;

  GiftCertificateServiceImpl giftCertificateServiceImpl;

  GiftCertificate giftCertificate;

  @BeforeEach
  void setUp() {
    giftCertificateServiceImpl =
        new GiftCertificateServiceImpl(giftCertificateDao, certificateBuilder);
    giftCertificateServiceImpl.setGiftCertificateValidator(new GiftCertificateValidator());
  }

  @AfterEach
  void tearDown() {
    giftCertificateServiceImpl = null;
  }

  @Test
  void updatePrice() {
    giftCertificateServiceImpl.updatePartial(
        Map.of(GiftCertificateTable.GIFT_CERTIFICATE_PRICE, "200"), 1L);
    verify(giftCertificateDao, VerificationModeFactory.times(1)).update(new BigDecimal("200"), 1L);
  }

  @Test
  void updateDuration() {
    giftCertificateServiceImpl.updatePartial(
            Map.of(GiftCertificateTable.GIFT_CERTIFICATE_DURATION, "P100D"), 1L);
    verify(giftCertificateDao, VerificationModeFactory.times(1)).update(Period.parse("P100D"), 1L);
  }

  @Test()
  public void whenNotValidPrice1_thenShouldGiveValidationException() {
    RuntimeException exception = assertThrows(ValidationException.class, () -> giftCertificateServiceImpl.updatePartial(
            Map.of(GiftCertificateTable.GIFT_CERTIFICATE_PRICE, "999999999.99"), 1L));
    assertEquals("certificate.price_validation_error", exception.getMessage());
  }

  @Test()
  public void whenNotValidPrice2_thenShouldGiveValidationException() {
    RuntimeException exception = assertThrows(ValidationException.class, () -> giftCertificateServiceImpl.updatePartial(
            Map.of(GiftCertificateTable.GIFT_CERTIFICATE_PRICE, "200.222"), 1L));
    assertEquals("certificate.price_validation_error", exception.getMessage());
  }

  @Test()
  public void whenNotValidDuration1_thenShouldGiveValidationException() {
    RuntimeException exception = assertThrows(ValidationException.class, () -> giftCertificateServiceImpl.updatePartial(
            Map.of(GiftCertificateTable.GIFT_CERTIFICATE_DURATION, "P11111111111111111111111111D"), 1L));
    assertEquals("certificate.duration_validation_error", exception.getMessage());
  }

  @Test()
  public void whenNotValidDuration2_thenShouldGiveValidationException() {
    RuntimeException exception = assertThrows(ValidationException.class, () -> giftCertificateServiceImpl.updatePartial(
            Map.of(GiftCertificateTable.GIFT_CERTIFICATE_DURATION, "P11111111111111111111111111Dd"), 1L));
    assertEquals("certificate.duration_validation_error", exception.getMessage());
  }

  @Test()
  public void whenNotValidDuration3_thenShouldGiveValidationException() {
    RuntimeException exception = assertThrows(ValidationException.class, () -> giftCertificateServiceImpl.updatePartial(
            Map.of(GiftCertificateTable.GIFT_CERTIFICATE_DURATION, "pP11111111111111111111111111Dd"), 1L));
    assertEquals("certificate.duration_validation_error", exception.getMessage());
  }

  @Test
  void createWithNoTags() {
    giftCertificate = new GiftCertificate();
    giftCertificate.setName("name4");
    giftCertificate.setDescription("description4");
    giftCertificate.setPrice(new BigDecimal("10.99"));
    giftCertificate.setDuration(Period.ofDays(1));
    giftCertificate.setTags(null);
    ValidationException exception =
            assertThrows(
                    ValidationException.class,
                    () -> {
                      giftCertificateServiceImpl.create(giftCertificate);
                    });
    verify(giftCertificateDao, VerificationModeFactory.times(0)).create(giftCertificate);
    assertEquals(
            "javax.validation.constraints.NotBlank.message",
            exception.getErrors().getFieldError(GiftCertificateTable.GIFT_CERTIFICATE_TAGS).getCode());
  }

  @Test
  void createWithNoPrice() {
    giftCertificate = new GiftCertificate();
    giftCertificate.setName("name4");
    giftCertificate.setDescription("description4");
    giftCertificate.setPrice(null);
    giftCertificate.setDuration(Period.ofDays(1));
    giftCertificate.setTags(
            List.of(new Tag(1L, "tag1"), new Tag(11L, "tag11"), new Tag(12L, "tag12")));
    ValidationException exception =
            assertThrows(
                    ValidationException.class,
                    () -> {
                      giftCertificateServiceImpl.create(giftCertificate);
                    });
    verify(giftCertificateDao, VerificationModeFactory.times(0)).create(giftCertificate);
    assertEquals(
            "javax.validation.constraints.NotBlank.message",
            exception.getErrors().getFieldError(GiftCertificateTable.GIFT_CERTIFICATE_PRICE).getCode());
  }

  @Test
  void createWithDuration() {
    giftCertificate = new GiftCertificate();
    giftCertificate.setName("name4");
    giftCertificate.setDescription("description4");
    giftCertificate.setPrice(new BigDecimal("10.99"));
    giftCertificate.setDuration(null);
    giftCertificate.setTags(
            List.of(new Tag(1L, "tag1"), new Tag(11L, "tag11"), new Tag(12L, "tag12")));
    ValidationException exception =
            assertThrows(
                    ValidationException.class,
                    () -> {
                      giftCertificateServiceImpl.create(giftCertificate);
                    });
    verify(giftCertificateDao, VerificationModeFactory.times(0)).create(giftCertificate);
    assertEquals(
            "javax.validation.constraints.NotBlank.message",
            exception.getErrors().getFieldError(GiftCertificateTable.GIFT_CERTIFICATE_DURATION).getCode());
  }
}
