package com.epam.esm.dao.builder;

import com.epam.esm.entity.ParamName;

import javax.persistence.Query;
import java.util.EnumMap;

public interface TagBuilder {
    Query buildQuery(EnumMap<ParamName, String> params);
}
