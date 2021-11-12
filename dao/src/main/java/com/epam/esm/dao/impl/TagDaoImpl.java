package com.epam.esm.dao.impl;

import com.epam.esm.dao.TagDao;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Order;
import com.epam.esm.entity.Tag;
import org.intellij.lang.annotations.Language;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Repository
public class TagDaoImpl implements TagDao<Long> {

  @Language("SQL")
  private static final String SQL_SELECT_MOST_POPULAR_TAG =
      "SELECT tag.id, tag.name, COUNT(tag.id) AS COUNT FROM gift_certificate_tag\n"
          + "JOIN tag ON tag.id = id_tag\n"
          + "WHERE gift_certificate_tag.id_gift_certificate\n"
          + "         IN\n"
          + "            (SELECT DISTINCT ogc.id_gift_certificate FROM orders\n"
          + "             LEFT OUTER JOIN order_gift_certificate ogc ON ogc.id_order = orders.id\n"
          + "             WHERE orders.id_user = \n"
          + "                                     ( \n"
          + "                                       SELECT orders.id_user\n"
          + "                                       FROM orders\n"
          + "                                       GROUP BY orders.id_user \n"
          + "                                       HAVING SUM(cost) = ( \n"
          + "                                       SELECT MAX(SUM) FROM (\n"
          + "                                       SELECT SUM(cost) AS SUM\n"
          + "                                       FROM orders\n"
          + "                                       GROUP BY orders.id_user ) AS SUM_COST_ORDERS_CLIENTS )\n"
          + "                                      )\n"
          + "\t\t\t   )\n"
          + "GROUP BY tag.id\n"
          + "HAVING COUNT(tag.id) =  ( SELECT MAX(COUNT) FROM (\n"
          + "\n"
          + "SELECT tag.id, COUNT(tag.id) AS COUNT FROM gift_certificate_tag\n"
          + "JOIN tag ON tag.id = id_tag\n"
          + "WHERE gift_certificate_tag.id_gift_certificate\n"
          + "         IN\n"
          + "            (SELECT DISTINCT ogc.id_gift_certificate FROM orders\n"
          + "             LEFT OUTER JOIN order_gift_certificate ogc ON ogc.id_order = orders.id\n"
          + "             WHERE orders.id_user = \n"
          + "                                     (\n"
          + "                                       SELECT orders.id_user\n"
          + "                                       FROM orders\n"
          + "                                       GROUP BY orders.id_user \n"
          + "                                       HAVING SUM(cost) = ( \n"
          + "                                       SELECT MAX(SUM) FROM (\n"
          + "                                       SELECT SUM(cost) AS SUM\n"
          + "                                       FROM orders\n"
          + "                                       GROUP BY orders.id_user ) AS SUM_COST_ORDERS_CLIENTS )\n"
          + "                                      )\n"
          + "\t\t\t   )\n"
          + "GROUP BY tag.id        ) AS COUNT_TAGS\n"
          + "                        )";

  @PersistenceContext private EntityManager entityManager;

  @Override
  public List<Tag> find(Query query) {
    return query.getResultList();
  }

  @Override
  public Optional<Tag> findTagById(Long id) {
    Tag tag = entityManager.find(Tag.class, id);
    return tag != null ? Optional.of(tag) : Optional.empty();
  }

  @Transactional
  @Override
  public void delete(Long id) {
    Tag tag = entityManager.find(Tag.class, id);
    Iterator<GiftCertificate> it = tag.getCertificates().iterator();
    while (it.hasNext()) {
      GiftCertificate gc = it.next();
      tag.removeGiftCertificate(gc, it);
    }
    entityManager.remove(tag);
    entityManager.flush();
  }

  @Transactional
  @Override
  public void update(Tag tag) {
    Tag findTag = entityManager.find(Tag.class, tag.getId());
    findTag.setName(tag.getName());
  }

  @Transactional
  @Override
  public Tag create(Tag tag) {
    entityManager.persist(tag);
    return tag;
  }

  @Override
  public List<Tag> findMostPopularTags() {
    Query nativeQuery = entityManager.createNativeQuery(SQL_SELECT_MOST_POPULAR_TAG);
    List<Object[]> objects = nativeQuery.getResultList();
    List<Tag> tags = new ArrayList<>();
    for (Object[] o : objects) {
      Tag tag = new Tag();
      tag.setId(((BigInteger) o[0]).longValue());
      tag.setName((String) o[1]);
      tags.add(tag);
    }
    return tags;
  }

  @Override
  public long countTags() {
    Query query = entityManager.createQuery("select count(*) from tags");
    return (long) query.getSingleResult();
  }
}
