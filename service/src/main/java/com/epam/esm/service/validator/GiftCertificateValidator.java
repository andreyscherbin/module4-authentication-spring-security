package com.epam.esm.service.validator;

import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.GiftCertificateTable;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class GiftCertificateValidator implements Validator {

  private static final String PRICE_REGEX = "^[\\d]{1,8}([\\.]?[\\d]{1,2})?$";
  private static final String DURATION_REGEX = "^[Pp][\\d]{1,9}[Dd]$";
  private static final String REGEX_NOT_BLANK = "^(?=\\s*\\S).*$";

  @Override
  public boolean supports(Class<?> clazz) {
    return GiftCertificate.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    GiftCertificate gc = (GiftCertificate) target;
    if (gc.getName() == null || !isNotBlank(gc.getName())) {
      errors.rejectValue(
          GiftCertificateTable.GIFT_CERTIFICATE_NAME,
          "javax.validation.constraints.NotBlank.message");
    }
    if (gc.getDescription() == null || !isNotBlank(gc.getDescription())) {
      errors.rejectValue(
          GiftCertificateTable.GIFT_CERTIFICATE_DESCRIPTION,
          "javax.validation.constraints.NotBlank.message");
    }
    if (gc.getPrice() == null) {
      errors.rejectValue(
          GiftCertificateTable.GIFT_CERTIFICATE_PRICE,
          "javax.validation.constraints.NotBlank.message");
    }
    if (gc.getDuration() == null) {
      errors.rejectValue(
          GiftCertificateTable.GIFT_CERTIFICATE_DURATION,
          "javax.validation.constraints.NotBlank.message");
    }
    if (gc.getTags() == null || gc.getTags().isEmpty()) {
      errors.rejectValue(
          GiftCertificateTable.GIFT_CERTIFICATE_TAGS,
          "javax.validation.constraints.NotBlank.message");
    }
  }

  public static boolean isPriceValid(String price) {
    return price.matches(PRICE_REGEX);
  }

  public static boolean isDurationValid(String duration) {
    return duration.matches(DURATION_REGEX);
  }

  public static boolean isNotBlank(String field) {
    return field.matches(REGEX_NOT_BLANK);
  }
}
