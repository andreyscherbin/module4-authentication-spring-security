package com.epam.esm.entity;

import java.math.BigDecimal;
import java.time.Instant;

public class OrderInfo {

  private Instant createDate;
  private BigDecimal cost;

  public OrderInfo() {}

  public Instant getCreateDate() {
    return createDate;
  }

  public void setCreateDate(Instant createDate) {
    this.createDate = createDate;
  }

  public BigDecimal getCost() {
    return cost;
  }

  public void setCost(BigDecimal cost) {
    this.cost = cost;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    OrderInfo orderInfo = (OrderInfo) o;

    if (createDate != null
        ? !createDate.equals(orderInfo.createDate)
        : orderInfo.createDate != null) {
      return false;
    }
    return cost != null ? cost.equals(orderInfo.cost) : orderInfo.cost == null;
  }

  @Override
  public int hashCode() {
    int result = createDate != null ? createDate.hashCode() : 0;
    result = 31 * result + (cost != null ? cost.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("OrderInfo{");
    sb.append("createDate=").append(createDate);
    sb.append(", cost=").append(cost);
    sb.append('}');
    return sb.toString();
  }
}
