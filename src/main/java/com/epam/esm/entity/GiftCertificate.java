package com.epam.esm.entity;

import com.epam.esm.util.PeriodConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.collections4.CollectionUtils;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Entity(name = "gift_certificate")
@Table(name = "gift_certificate")
public class GiftCertificate {

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Size(max = 30)
  private String name;

  @Size(max = 30)
  private String description;

  @Positive
  @Digits(integer = 10, fraction = 2)
  private BigDecimal price;

  @Convert(converter = PeriodConverter.class)
  private Period duration;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @Column(name = "create_date", updatable = false)
  private Instant createDate;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @Column(name = "last_update_date", insertable = false)
  private Instant lastUpdateDate;

  @Valid
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "gift_certificate_tag",
      joinColumns = @JoinColumn(name = "id_gift_certificate"),
      inverseJoinColumns = @JoinColumn(name = "id_tag"))
  private List<Tag> tags;

  @JsonIgnore
  @OneToMany(mappedBy = "giftCertificate")
  private Set<GiftCertificatePriceHistory> priceHistory;

  @JsonIgnore
  @ManyToMany(mappedBy = "certificates")
  private List<Order> orders;

  public GiftCertificate() {}

  public GiftCertificate(long id) {
    this.id = id;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public Period getDuration() {
    return duration;
  }

  public void setDuration(Period duration) {
    this.duration = duration;
  }

  public Instant getCreateDate() {
    return createDate;
  }

  public void setCreateDate(Instant createDate) {
    this.createDate = createDate;
  }

  public Instant getLastUpdateDate() {
    return lastUpdateDate;
  }

  public void setLastUpdateDate(Instant lastUpdateDate) {
    this.lastUpdateDate = lastUpdateDate;
  }

  public List<Tag> getTags() {
    return tags;
  }

  public void setTags(List<Tag> tags) {
    this.tags = tags;
  }

  public List<Order> getOrders() {
    return orders;
  }

  public void setOrders(List<Order> orders) {
    this.orders = orders;
  }

  public Set<GiftCertificatePriceHistory> getPriceHistory() {
    return priceHistory;
  }

  public void setPriceHistory(Set<GiftCertificatePriceHistory> priceHistory) {
    this.priceHistory = priceHistory;
  }

  @PrePersist
  public void onPrePersist() {
    createDate = Instant.now().truncatedTo(ChronoUnit.MILLIS);
  }

  @PreUpdate
  public void onPreUpdate() {
    lastUpdateDate = Instant.now().truncatedTo(ChronoUnit.MILLIS);
  }

  @PreRemove
  public void onPreRemove() {}

  public void removeOrder(Order order, Iterator<Order> it) {
    it.remove();
    order.getCertificates().remove(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    GiftCertificate that = (GiftCertificate) o;

    if (id != that.id) return false;
    if (name != null ? !name.equals(that.name) : that.name != null) return false;
    if (description != null ? !description.equals(that.description) : that.description != null)
      return false;
    if (price != null ? !price.equals(that.price) : that.price != null) return false;
    if (duration != null ? !duration.equals(that.duration) : that.duration != null) return false;
    if (createDate != null ? !createDate.equals(that.createDate) : that.createDate != null)
      return false;
    if (lastUpdateDate != null
        ? !lastUpdateDate.equals(that.lastUpdateDate)
        : that.lastUpdateDate != null) return false;
    return tags != null ? CollectionUtils.isEqualCollection(tags, that.tags) : that.tags == null;
  }

  @Override
  public int hashCode() {
    int result = (int) (id ^ (id >>> 32));
    result = 31 * result + (name != null ? name.hashCode() : 0);
    result = 31 * result + (description != null ? description.hashCode() : 0);
    result = 31 * result + (price != null ? price.hashCode() : 0);
    result = 31 * result + (duration != null ? duration.hashCode() : 0);
    result = 31 * result + (createDate != null ? createDate.hashCode() : 0);
    result = 31 * result + (lastUpdateDate != null ? lastUpdateDate.hashCode() : 0);
    result = 31 * result + (tags != null ? new HashSet<>(tags).hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "GiftCertificate{"
        + "id="
        + id
        + ", name='"
        + name
        + '\''
        + ", description='"
        + description
        + '\''
        + ", price="
        + price
        + ", duration="
        + duration
        + ", createDate="
        + createDate
        + ", lastUpdateDate="
        + lastUpdateDate
        + ", tags="
        + tags
        + '}';
  }
}
