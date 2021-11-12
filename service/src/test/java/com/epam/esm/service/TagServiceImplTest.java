package com.epam.esm.service;

import com.epam.esm.dao.TagDao;
import com.epam.esm.dao.builder.CertificateBuilder;
import com.epam.esm.dao.builder.TagBuilder;
import com.epam.esm.dao.builder.impl.TagBuilderImpl;
import com.epam.esm.entity.Tag;
import com.epam.esm.service.impl.TagServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TagServiceImplTest {

  @Mock TagDao<Long> tagDao;

  @Mock TagBuilder tagBuilder;

  TagService tagService;

  private Tag mockedTag;
  private static final String tagName = "tag1";

  @BeforeEach
  void setUp() {
    tagService = new TagServiceImpl(tagDao, tagBuilder);
    createMockedTag();
  }

  @AfterEach
  void tearDown() {
    tagService = null;
    mockedTag = null;
  }

  @Test
  void createTags() {
    List<Tag> userTags = List.of(new Tag(tagName));
    when(tagDao.create(any(Tag.class))).thenReturn(mockedTag);
    List<Tag> expectedTags = tagService.createTags(userTags);
    verify(tagDao, VerificationModeFactory.times(1)).create(any(Tag.class));
    assertEquals(expectedTags, List.of(mockedTag));
  }

  private void createMockedTag() {
    mockedTag = new Tag();
    mockedTag.setName(tagName);
  }
}
