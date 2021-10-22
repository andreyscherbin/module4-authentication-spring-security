package com.epam.esm.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.collections4.CollectionUtils;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;

@Entity(name = "orders")
@Table(name = "orders")
public class Order {

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Valid
  @NotNull
  @ManyToOne
  @JoinColumn(name = "id_user")
  private User user;

  @Digits(integer = 10, fraction = 2)
  private BigDecimal cost;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @Column(name = "create_date", updatable = false)
  private Instant createDate;

  @Valid
  @NotNull
  @NotEmpty
  @ManyToMany
  @JoinTable(
      name = "order_gift_certificate",
      joinColumns = @JoinColumn(name = "id_order"),
      inverseJoinColumns = @JoinColumn(name = "id_gift_certificate"))
  private List<GiftCertificate> certificates;

  public BigDecimal getCost() {
    return cost;
  }

  public void setCost(BigDecimal cost) {
    this.cost = cost;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public List<GiftCertificate> getCertificates() {
    return certificates;
  }

  public void setCertificates(List<GiftCertificate> certificates) {
    this.certificates = certificates;
  }

  public Instant getCreateDate() {
    return createDate;
  }

  public void setCreateDate(Instant createDate) {
    this.createDate = createDate;
  }

  @PrePersist
  public void onPrePersist() {
    createDate = Instant.now().truncatedTo(ChronoUnit.MILLIS);
  }

  @PreUpdate
  public void onPreUpdate() {}

  @PreRemove
  public void onPreRemove() {}

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Order order = (Order) o;

    if (id != order.id) return false;
    if (user != null ? !user.equals(order.user) : order.user != null) return false;
    if (cost != null ? !cost.equals(order.cost) : order.cost != null) return false;
    if (createDate != null ? !createDate.equals(order.createDate) : order.createDate != null)
      return false;
    return certificates != null
        ? CollectionUtils.isEqualCollection(certificates, order.certificates)
        : order.certificates == null;
  }

  @Override
  public int hashCode() {
    int result = (int) (id ^ (id >>> 32));
    result = 31 * result + (user != null ? user.hashCode() : 0);
    result = 31 * result + (cost != null ? cost.hashCode() : 0);
    result = 31 * result + (createDate != null ? createDate.hashCode() : 0);
    result = 31 * result + (certificates != null ? new HashSet<>(certificates).hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "Order{"
        + "id="
        + id
        + ", user="
        + user
        + ", cost="
        + cost
        + ", createDate="
        + createDate
        + ", certificates="
        + certificates
        + '}';
  }
}
