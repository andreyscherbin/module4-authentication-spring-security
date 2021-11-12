package com.epam.esm.service.impl;

import com.epam.esm.dao.TagDao;
import com.epam.esm.dao.builder.TagBuilder;
import com.epam.esm.entity.ParamName;
import com.epam.esm.entity.Tag;
import com.epam.esm.service.TagService;
import com.epam.esm.service.exception.ValidationException;
import com.epam.esm.util.UtilClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.validation.MapBindingResult;
import org.springframework.validation.Validator;

import javax.persistence.Query;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TagServiceImpl implements TagService {

  private final TagDao<Long> tagDao;

  private Validator paramValidator;
  private final TagBuilder tagBuilder;

  @Autowired
  @Qualifier("paramValidator")
  public void setParamValidator(Validator paramValidator) {
    this.paramValidator = paramValidator;
  }

  @Autowired
  public TagServiceImpl(TagDao<Long> tagDao, TagBuilder tagBuilder) {
    this.tagDao = tagDao;
    this.tagBuilder = tagBuilder;
  }

  @Transactional
  @Override
  public Optional<Tag> createTag(Tag tag) {
    Optional<Tag> createdTag;
    if (findTagsByName(tag).isEmpty()) {
      createdTag = Optional.of(tagDao.create(tag));
    } else {
      createdTag = Optional.empty();
    }
    return createdTag;
  }

  @Transactional
  @Override
  public List<Tag> createTags(List<Tag> tags) {
    if (tags == null) {
      return new ArrayList<>();
    }
    List<Tag> tagsWithoutDuplicates = tags.stream().distinct().collect(Collectors.toList());
    for (Tag tag : tagsWithoutDuplicates) {
      List<Tag> list = findTagsByName(tag);
      if (list.isEmpty()) {
        tagDao.create(tag);
      } else {
        tag.setId(list.get(0).getId());
      }
    }
    return tagsWithoutDuplicates;
  }

  @Override
  public Optional<Tag> findTagById(long id) {
    return tagDao.findTagById(id);
  }

  @Override
  public List<Tag> find(Map<String, String> params) {
    EnumMap<ParamName, String> enumMap = UtilClass.convertToEnumMap(params);
    Errors errors = new MapBindingResult(enumMap, "enumMap");
    paramValidator.validate(enumMap, errors);
    if (errors.hasErrors()) {
      throw new ValidationException(errors);
    }
    Query query = tagBuilder.buildQuery(enumMap);
    return tagDao.find(query);
  }

  @Transactional
  @Override
  public boolean update(Tag tag) {
    boolean result;
    long id = tag.getId();
    List<Tag> list = findTagsByName(tag);
    if (list.isEmpty()) {
      tagDao.update(tag);
      result = true;
    } else {
      if (list.get(0).getId() == id) {
        tagDao.update(tag);
        result = true;
      } else {
        result = false;
      }
    }
    return result;
  }

  private List<Tag> findTagsByName(Tag tag) {
    String name = tag.getName();
    EnumMap<ParamName, String> params = new EnumMap<>(ParamName.class);
    params.put(ParamName.NAME, name);
    Query query = tagBuilder.buildQuery(params);
    return tagDao.find(query);
  }

  @Transactional
  @Override
  public void delete(Long id) {
    tagDao.delete(id);
  }

  @Override
  public List<Tag> getMostPopularTags() {
    return tagDao.findMostPopularTags();
  }

  @Override
  public long getTotalRows() {
    return tagDao.countTags();
  }
}
