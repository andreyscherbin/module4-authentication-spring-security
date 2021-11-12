package com.epam.esm.service.validator;

import com.epam.esm.entity.ParamName;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.EnumMap;

import static com.epam.esm.entity.ParamName.*;

@Component
public class ParamValidator implements Validator {

  private static final String REGEX_PARAM = ".{1,30}";
  private static final String REGEX_DIGITS_EXCLUDE_ZERO = "^[1-9]\\d*$";
  private static final String REGEX_ASC_DESC = "^(desc|asc|ASC|DESC)$";
  private static final String REGEX_NOT_BLANK = "(.|\\s)*\\S(.|\\s)*";

  public static boolean isParamValid(String param) {
    return param.matches(REGEX_PARAM);
  }

  public static boolean isDigit(String param) {
    return param.matches(REGEX_DIGITS_EXCLUDE_ZERO);
  }

  public static boolean isAscDesc(String param) {
    return param.matches(REGEX_ASC_DESC);
  }

  public static boolean isNotBlank(String param) {
    return param.matches(REGEX_NOT_BLANK);
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return EnumMap.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    EnumMap<ParamName, String> params = (EnumMap<ParamName, String>) target;
    params.forEach((k,v) -> {
      switch(k){
        case ID -> {
          if(!isDigit(v)){
            errors.rejectValue(ID.toString(), "javax.validation.constraints.Digits");
          }
        }
        case NAME -> {
          if (!isNotBlank(v)) {
            errors.rejectValue(NAME.toString(), "javax.validation.constraints.NotBlank.message");
          }
          if (!isParamValid(v)) {
            errors.rejectValue(NAME.toString(), "javax.validation.constraints.Size.message");
          }
        }
        case DESCRIPTION -> {
          if (!isNotBlank(v)) {
            errors.rejectValue(DESCRIPTION.toString(), "javax.validation.constraints.NotBlank.message");
          }
          if (!isParamValid(v)) {
            errors.rejectValue(DESCRIPTION.toString(), "javax.validation.constraints.Size.message");
          }
        }
        case PAGE_SIZE -> {
          if (!isNotBlank(v)) {
            errors.rejectValue(PAGE_SIZE.toString(), "javax.validation.constraints.NotBlank.message");
          }
          if (!isDigit(v)) {
            errors.rejectValue(PAGE_SIZE.toString(), "javax.validation.constraints.Digits");
          }
        }
        case PAGE_NUM -> {
          if (!isNotBlank(v)) {
            errors.rejectValue(PAGE_NUM.toString(), "javax.validation.constraints.NotBlank.message");
          }
          if (!isDigit(v)) {
            errors.rejectValue(PAGE_NUM.toString(), "javax.validation.constraints.Digits");
          }
        }
        case SORT_BY -> {
          if (!isNotBlank(v)) {
            errors.rejectValue(SORT_BY.toString(), "javax.validation.constraints.NotBlank.message");
          }
        }
        case ORDER_BY -> {
          if (!isNotBlank(v)) {
            errors.rejectValue(ORDER_BY.toString(), "javax.validation.constraints.NotBlank.message");
          }
          if(!isAscDesc(v)){
            errors.rejectValue(ORDER_BY.toString(), "javax.validation.constraints.AscDesc");
          }
        }
    }
  });
    }
}
