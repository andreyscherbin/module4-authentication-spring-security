package com.epam.esm.dao;

import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.ParamName;
import com.epam.esm.entity.Tag;

import javax.persistence.Query;
import java.math.BigDecimal;
import java.time.Period;
import java.util.EnumMap;
import java.util.List;
import java.util.Optional;

/**
 * Interface for gift certificate dao. This dao is responsible for the persistence gift certificate
 * domain entity
 *
 * @author Andrei Shcherbin
 * @version 1.0
 * @since 26.08.2021
 */
public interface GiftCertificateDao<K> {

  /**
   * Return all gift certificates from storage
   *
   * @param query query to be executed
   * @return all gift certificates
   */
  List<GiftCertificate> find(Query query);

  /**
   * Find gift certificate by its id
   *
   * @param id of the gift certificate
   * @return the gift certificate wih the given id or {@link Optional#empty()} if none found
   */
  Optional<GiftCertificate> findById(K id);

  /**
   * Delete from storage gift certificate with the given id
   *
   * @param id of the deleted gift certificate
   */
  void delete(K id);

  /**
   * Update info about given gift certificate
   *
   * @param gc updated information
   */
  void update(GiftCertificate gc);

  /**
   * Create given gift certificate
   *
   * @param gc to be created without id
   * @return created gift certificate with id
   */
  GiftCertificate create(GiftCertificate gc);

  void update(BigDecimal price, Long id);

  void update(Period duration, Long id);

  long countGiftCertificates();
}
