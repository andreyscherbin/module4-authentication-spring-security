package com.epam.esm.service;

import com.epam.esm.entity.Tag;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Interface for the tag service. This service is responsible for the work with tags */
public interface TagService {

  /**
   * Create given tag if not already exists
   *
   * @param tag to be created
   * @return created tag or {@link Optional#empty()} if already exists
   */
  Optional<Tag> createTag(Tag tag);

  /**
   * Create given tags if not already exists
   *
   * @param tags to be created
   * @return list of created tags
   */
  List<Tag> createTags(List<Tag> tags);

  /**
   * Retrieves a tag by its id
   *
   * @param id of the tag
   * @return the tag with the given id or {@link Optional#empty()} if none found
   */
  Optional<Tag> findTagById(long id);

  /**
   * Return all tags from storage
   *
   * @return all tags
   */
  List<Tag> find(Map<String, String> params);

  /**
   * Updates a tag by id given inside param
   *
   * @param tag with the updated information
   * @return true if was updated otherwise false
   */
  boolean update(Tag tag);

  /**
   * Deletes a tag by given id
   *
   * @param id of the deleted tag
   */
  void delete(Long id);

  List<Tag> getMostPopularTags();

  long getTotalRows();
}
