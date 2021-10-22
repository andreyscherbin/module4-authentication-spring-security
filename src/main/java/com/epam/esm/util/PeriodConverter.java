package com.epam.esm.util;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.time.Period;

@Converter(autoApply = true)
public class PeriodConverter implements AttributeConverter<Period, Integer> {

  @Override
  public Integer convertToDatabaseColumn(Period attribute) {
    return attribute.getDays();
  }

  @Override
  public Period convertToEntityAttribute(Integer duration) {
    return Period.ofDays(duration);
  }
}
