package com.epam.esm.dao;

import com.epam.esm.entity.ParamName;
import com.epam.esm.entity.Tag;

import javax.persistence.Query;
import java.util.*;

/**
 * Interface for tag dao. This dao is responsible for the persistence tag domain entity
 *
 * @author Andrei Shcherbin
 * @version 1.0
 * @since 26.08.2021
 */
public interface TagDao<K> {

    /**
     * Return all tags from storage
     *
     * @return all tags
     */
    List<Tag> find(Query query);

    /**
     * Retrieves a tag by its id
     *
     * @param id of the tag
     * @return the tag with the given id or {@link Optional#empty()} if none found
     */
    Optional<Tag> findTagById(K id);

    /**
     * Deletes a tag by given id
     *
     * @param id of the deleted tag
     */
    void delete(K id);

    /**
     * Updates a tag by id given inside param
     *
     * @param tag with the updated information
     */
    void update(Tag tag);

    /**
     * Creates a given tag
     *
     * @param tag to be created without id
     * @return the created tag with id
     */
    Tag create(Tag tag);

    List<Tag> findMostPopularTags();

    long countTags();
}
