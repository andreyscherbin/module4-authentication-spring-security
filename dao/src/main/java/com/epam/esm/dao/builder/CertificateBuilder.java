package com.epam.esm.dao.builder;

import com.epam.esm.entity.ParamName;
import com.epam.esm.entity.Tag;

import javax.persistence.Query;
import java.util.EnumMap;
import java.util.List;

public interface CertificateBuilder {
    Query buildQuery(EnumMap<ParamName, String> params, List<Tag> tags);
}
