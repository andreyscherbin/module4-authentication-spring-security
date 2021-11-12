package com.epam.esm.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import java.math.BigDecimal;
import java.time.Instant;

@Entity(name = "gift_certificate_price_history")
@Table(name = "gift_certificate_price_history")
public class GiftCertificatePriceHistory {

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Digits(integer = 10, fraction = 2)
  private BigDecimal price;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @Column(name = "effective_date_from")
  private Instant effectiveDateFrom;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @Column(name = "effective_date_to")
  private Instant effectiveDateTo;

  @ManyToOne
  @JoinColumn(name = "id_gift_certificate")
  private GiftCertificate giftCertificate;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public Instant getEffectiveDateFrom() {
    return effectiveDateFrom;
  }

  public void setEffectiveDateFrom(Instant effectiveDateFrom) {
    this.effectiveDateFrom = effectiveDateFrom;
  }

  public Instant getEffectiveDateTo() {
    return effectiveDateTo;
  }

  public void setEffectiveDateTo(Instant effectiveDateTo) {
    this.effectiveDateTo = effectiveDateTo;
  }

  public GiftCertificate getGiftCertificate() {
    return giftCertificate;
  }

  public void setGiftCertificate(GiftCertificate giftCertificate) {
    this.giftCertificate = giftCertificate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    GiftCertificatePriceHistory that = (GiftCertificatePriceHistory) o;

    if (id != that.id) return false;
    if (price != null ? !price.equals(that.price) : that.price != null) return false;
    if (effectiveDateFrom != null
        ? !effectiveDateFrom.equals(that.effectiveDateFrom)
        : that.effectiveDateFrom != null) return false;
    if (effectiveDateTo != null
        ? !effectiveDateTo.equals(that.effectiveDateTo)
        : that.effectiveDateTo != null) return false;
    return giftCertificate != null
        ? giftCertificate.equals(that.giftCertificate)
        : that.giftCertificate == null;
  }

  @Override
  public int hashCode() {
    int result = (int) (id ^ (id >>> 32));
    result = 31 * result + (price != null ? price.hashCode() : 0);
    result = 31 * result + (effectiveDateFrom != null ? effectiveDateFrom.hashCode() : 0);
    result = 31 * result + (effectiveDateTo != null ? effectiveDateTo.hashCode() : 0);
    result = 31 * result + (giftCertificate != null ? giftCertificate.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "GiftCertificatePriceHistory{"
        + "id="
        + id
        + ", price="
        + price
        + ", effectiveDateFrom="
        + effectiveDateFrom
        + ", effectiveDateTo="
        + effectiveDateTo
        + ", giftCertificate="
        + giftCertificate
        + '}';
  }
}
