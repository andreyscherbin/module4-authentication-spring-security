package com.epam.esm.dao.impl;

import com.epam.esm.dao.GiftCertificateDao;
import com.epam.esm.entity.*;
import org.intellij.lang.annotations.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.Period;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Repository
public class GiftCertificateDaoImpl implements GiftCertificateDao<Long> {

  private static final Instant MAX_VALUE_TIMESTAMP = Instant.parse("2038-01-19T00:14:07Z");

  @Language("SQL")
  private static final String SQL_UPDATE_ORDERS_COST =
      "UPDATE orders"
          + "  SET cost = cost - (SELECT price FROM gift_certificate_price_history gcph"
          + "                     WHERE gcph.effective_date_from <= orders.create_date AND gcph.effective_date_to >= orders.create_date"
          + "                     AND gcph.id_gift_certificate = :id"
          + "                  )"
          + "  WHERE orders.id IN"
          + " (SELECT order_gift_certificate.id_order FROM order_gift_certificate WHERE order_gift_certificate.id_gift_certificate = :id)";

  Logger logger = LoggerFactory.getLogger(GiftCertificateDaoImpl.class);

  @PersistenceContext private EntityManager entityManager;

  @Override
  public List<GiftCertificate> find(Query query) {
    return query.getResultList();
  }

  @Override
  public Optional<GiftCertificate> findById(Long id) {
    TypedQuery<GiftCertificate> query =
        entityManager.createQuery(
            "SELECT gc FROM gift_certificate gc LEFT JOIN FETCH gc.tags where gc.id = :id",
            GiftCertificate.class);
    query.setParameter(GiftCertificateTable.GIFT_CERTIFICATE_ID, id);
    try {
      GiftCertificate gc = query.getSingleResult();
      return Optional.of(gc);
    } catch (NoResultException ex) {
      return Optional.empty();
    }
  }

  @Transactional
  @Override
  public void delete(Long id) {
    entityManager
        .createNativeQuery(SQL_UPDATE_ORDERS_COST)
        .setParameter(GiftCertificateTable.GIFT_CERTIFICATE_ID, id)
        .executeUpdate();
    GiftCertificate gc = entityManager.find(GiftCertificate.class, id);
    Iterator<Order> it = gc.getOrders().iterator();
    while (it.hasNext()) {
      Order order = it.next();
      gc.removeOrder(order, it);
    }
    entityManager.remove(gc);
    entityManager.flush();
  }

  @Transactional
  @Override
  public void update(GiftCertificate gc) {
    entityManager.merge(gc);
    entityManager.flush();
    changePriceHistory(gc.getId());
  }

  private void changePriceHistory(long id) {
    GiftCertificate gc = entityManager.find(GiftCertificate.class, id);
    GiftCertificatePriceHistory giftCertificatePriceHistory = new GiftCertificatePriceHistory();
    giftCertificatePriceHistory.setPrice(gc.getPrice());
    giftCertificatePriceHistory.setEffectiveDateFrom(gc.getLastUpdateDate());
    giftCertificatePriceHistory.setEffectiveDateTo(MAX_VALUE_TIMESTAMP);
    giftCertificatePriceHistory.setGiftCertificate(gc);
    entityManager.persist(giftCertificatePriceHistory);

    Instant lastUpdateDate = gc.getLastUpdateDate();
    long idGiftCertificate = gc.getId();

    Query query =
        entityManager.createQuery(
            "UPDATE gift_certificate_price_history SET effectiveDateTo = ?1 "
                + "WHERE giftCertificate.id = ?2 AND effectiveDateFrom < ?3 AND effectiveDateTo = ?4");
    query.setParameter(1, lastUpdateDate);
    query.setParameter(2, idGiftCertificate);
    query.setParameter(3, lastUpdateDate);
    query.setParameter(4, MAX_VALUE_TIMESTAMP).executeUpdate();
  }

  private void addPriceHistory(Long id) {
    GiftCertificate gc = entityManager.find(GiftCertificate.class, id);
    GiftCertificatePriceHistory giftCertificatePriceHistory = new GiftCertificatePriceHistory();
    giftCertificatePriceHistory.setPrice(gc.getPrice());
    giftCertificatePriceHistory.setEffectiveDateFrom(gc.getCreateDate());
    giftCertificatePriceHistory.setEffectiveDateTo(MAX_VALUE_TIMESTAMP);
    giftCertificatePriceHistory.setGiftCertificate(gc);
    entityManager.persist(giftCertificatePriceHistory);
  }

  @Transactional
  @Override
  public GiftCertificate create(GiftCertificate gc) {
    entityManager.persist(gc);
    addPriceHistory(gc.getId());
    return gc;
  }

  @Transactional
  @Override
  public void update(BigDecimal price, Long id) {
    GiftCertificate gc = entityManager.find(GiftCertificate.class, id);
    gc.setPrice(price);
    entityManager.flush();
    changePriceHistory(gc.getId());
  }

  @Transactional
  @Override
  public void update(Period duration, Long id) {
    GiftCertificate gc = entityManager.find(GiftCertificate.class, id);
    gc.setDuration(duration);
  }

  @Override
  public long countGiftCertificates() {
    Query query = entityManager.createQuery("select count(*) from gift_certificate");
    return (long) query.getSingleResult();
  }
}
