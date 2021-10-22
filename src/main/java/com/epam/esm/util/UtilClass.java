package com.epam.esm.util;

import com.epam.esm.entity.ParamName;

import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Collectors;

public class UtilClass {

  public static EnumMap<ParamName, String> convertToEnumMap(Map<String, String> map) {
    EnumMap<ParamName, String> enumMap = new EnumMap<>(ParamName.class);
    map.forEach(
        (k, v) -> {
          try {
            enumMap.put(ParamName.valueOf(k.toUpperCase()), v);
          } catch (IllegalArgumentException ex) {
            throw new EnumConstantNotPresentException(ParamName.class, k);
          }
        });
    return enumMap;
  }

  public static Map<String, String> convertToMap(EnumMap<ParamName, String> map) {
    Map<String, String> newMap =
        map.entrySet().stream()
            .collect(
                Collectors.toMap(e -> e.getKey().toString().toLowerCase(), Map.Entry::getValue));
    return newMap;
  }
}
