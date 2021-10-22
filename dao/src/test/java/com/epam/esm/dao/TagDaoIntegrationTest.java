package com.epam.esm.dao;

import com.epam.esm.entity.Tag;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest(classes = TestConfig.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles(value = "dev")
@Sql(
    value = {"/before-test-method.sql"},
    executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(
    value = {"/after-test-method.sql"},
    executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
public class TagDaoIntegrationTest {

    @Autowired
    TagDao<Long> tagDao;

    Tag tag;

    @BeforeEach
    void setUp() {
        tag = new Tag(1L,"tag1");
    }

    @AfterEach
    void tearDown() {
        tag = null;
    }

    @Test
    void findMostPopularTagsTest(){
        List<Tag> actual = tagDao.findMostPopularTags();
        assertEquals(List.of(tag), actual);
    }

    @Transactional
    @Test
    void delete() {
        tagDao.delete(tag.getId());
        Optional<Tag> deletedTag =
                tagDao.findTagById(tag.getId());
        assertFalse(deletedTag.isPresent());
    }
}
