package com.epam.esm.service.impl;

import com.epam.esm.dao.GiftCertificateDao;
import com.epam.esm.dao.builder.CertificateBuilder;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.GiftCertificateTable;
import com.epam.esm.entity.ParamName;
import com.epam.esm.entity.Tag;
import com.epam.esm.service.GiftCertificateService;
import com.epam.esm.service.exception.ValidationException;
import com.epam.esm.service.validator.GiftCertificateValidator;
import com.epam.esm.util.UtilClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Errors;
import org.springframework.validation.MapBindingResult;
import org.springframework.validation.Validator;

import javax.persistence.Query;
import java.math.BigDecimal;
import java.time.Period;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class GiftCertificateServiceImpl implements GiftCertificateService {

  private Validator paramValidator;
  private Validator giftCertificateValidator;
  private final CertificateBuilder certificateBuilder;
  private final GiftCertificateDao<Long> giftCertificateDao;

  @Autowired
  @Qualifier("paramValidator")
  public void setParamValidator(Validator paramValidator) {
    this.paramValidator = paramValidator;
  }

  @Autowired
  @Qualifier("giftCertificateValidator")
  public void setGiftCertificateValidator(Validator giftCertificateValidator) {
    this.giftCertificateValidator = giftCertificateValidator;
  }

  @Autowired
  public GiftCertificateServiceImpl(
      GiftCertificateDao<Long> giftCertificateDao, CertificateBuilder certificateBuilder) {
    this.giftCertificateDao = giftCertificateDao;
    this.certificateBuilder = certificateBuilder;
  }

  @Transactional
  @Override
  public GiftCertificate create(GiftCertificate giftCertificate) {
    DataBinder dataBinder = new DataBinder(giftCertificate);
    dataBinder.addValidators(giftCertificateValidator);
    dataBinder.validate();
    if (dataBinder.getBindingResult().hasErrors()) {
      throw new ValidationException(dataBinder.getBindingResult());
    }
    return giftCertificateDao.create(giftCertificate);
  }

  @Override
  public Optional<GiftCertificate> findById(long id) {
    return giftCertificateDao.findById(id);
  }

  @Override
  public List<GiftCertificate> find(Map<String, String> params, List<Tag> tags) {
    EnumMap<ParamName, String> enumMap = UtilClass.convertToEnumMap(params);
    enumMap.remove(ParamName.TAGS);
    Errors errors = new MapBindingResult(enumMap, "enumMap");
    paramValidator.validate(enumMap, errors);
    if (errors.hasErrors()) {
      throw new ValidationException(errors);
    }
    Query query = certificateBuilder.buildQuery(enumMap, tags);
    return giftCertificateDao.find(query);
  }

  @Transactional
  @Override
  public void updateFull(GiftCertificate newCertificate) {
    DataBinder dataBinder = new DataBinder(newCertificate);
    dataBinder.addValidators(giftCertificateValidator);
    dataBinder.validate();
    if (dataBinder.getBindingResult().hasErrors()) {
      throw new ValidationException(dataBinder.getBindingResult());
    }
    giftCertificateDao.update(newCertificate);
  }

  @Transactional
  @Override
  public void updatePartial(Map<String, Object> updates, Long id) {
    if (updates.containsKey(GiftCertificateTable.GIFT_CERTIFICATE_PRICE)) {
      String price = (String) updates.get(GiftCertificateTable.GIFT_CERTIFICATE_PRICE);
      if (!GiftCertificateValidator.isPriceValid(price)) {
        throw new ValidationException("certificate.price_validation_error", price);
      }
      giftCertificateDao.update(new BigDecimal(price), id);
    }
    if (updates.containsKey(GiftCertificateTable.GIFT_CERTIFICATE_DURATION)) {
      String duration = (String) updates.get(GiftCertificateTable.GIFT_CERTIFICATE_DURATION);
      if (!GiftCertificateValidator.isDurationValid(duration)) {
        throw new ValidationException("certificate.duration_validation_error", duration);
      }
      giftCertificateDao.update(Period.parse(duration), id);
    }
  }

  @Transactional
  @Override
  public void delete(Long id) {
    giftCertificateDao.delete(id);
  }

  @Override
  public long getTotalRows() {
    return giftCertificateDao.countGiftCertificates();
  }
}
