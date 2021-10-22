package com.epam.esm.service;

import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Interface for gift certificate service. This service is responsible for the work with gift
 * certificates
 */
@Validated
public interface GiftCertificateService {

  /**
   * Create given gift certificate
   *
   * @param gc to be created without id
   * @return created gift certificate with id
   */
  GiftCertificate create(GiftCertificate gc);

  /**
   * Find gift certificate by its id
   *
   * @param id of the gift certificate
   * @return the gift certificate wih the given id or {@link Optional#empty()} if none found
   */
  Optional<GiftCertificate> findById(long id);

  /**
   * Return all gift certificates from storage
   *
   * @return all gift certificates
   */
  List<GiftCertificate> find(Map<String, String> params, List<Tag> tags);

  /**
   * Update info about gift certificate. If new information not full, then the rest of the
   * information is taken from the old gift certificate
   *  @param newCertificate new information
   *
   */
  void updateFull(GiftCertificate newCertificate);

  void updatePartial(Map<String, Object> updates, Long id);

  /**
   * Delete from storage gift certificate with the given id
   *
   * @param id of the deleted gift certificate
   */
  void delete(Long id);

  long getTotalRows();
}
