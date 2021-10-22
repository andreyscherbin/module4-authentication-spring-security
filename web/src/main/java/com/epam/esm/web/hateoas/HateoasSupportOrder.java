package com.epam.esm.web.hateoas;

import com.epam.esm.entity.Order;
import com.epam.esm.web.api.OrderController;
import com.epam.esm.web.api.UserController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class HateoasSupportOrder {

  public static EntityModel<Order> getModel(Order order) {
    EntityModel<Order> model = EntityModel.of(order);
    Link selfLink = linkTo(OrderController.class).slash(order.getId()).withSelfRel();
    Link userLink = linkTo(UserController.class).slash(order.getUser().getId()).withRel("user");
    Link costLink =
        linkTo(UserController.class)
            .slash(order.getUser().getId())
            .slash("orders")
            .slash(order.getId())
            .withRel("userOrderInfo");
    Link allOrdersLink =
        linkTo(UserController.class)
            .slash(order.getUser().getId())
            .slash("orders")
            .withRel("userOrdersInfo");
    return model.add(selfLink).add(userLink).add(costLink).add(allOrdersLink);
  }

  public static CollectionModel<EntityModel<Order>> getCollectionModel(List<Order> orders) {
    List<EntityModel<Order>> list = new ArrayList<>();
    for (Order order : orders) {
      EntityModel<Order> orderModel = EntityModel.of(order);
      Link selfLink = linkTo(OrderController.class).slash(order.getId()).withSelfRel();
      Link userLink = linkTo(UserController.class).slash(order.getUser().getId()).withRel("user");
      Link costLink =
          linkTo(UserController.class)
              .slash(order.getUser().getId())
              .slash("orders")
              .slash(order.getId())
              .withRel("userOrderInfo");
      Link allOrdersLink =
          linkTo(UserController.class)
              .slash(order.getUser().getId())
              .slash("orders")
              .withRel("userOrdersInfo");
      orderModel.add(selfLink).add(userLink).add(costLink).add(allOrdersLink);
      list.add(orderModel);
    }
    Link link = linkTo(OrderController.class).withSelfRel();
    return CollectionModel.of(list, link);
  }

  public static void addParams(
      CollectionModel<EntityModel<Order>> collectionModel, Map<String, String> params) {
    Link link = linkTo(methodOn(OrderController.class).getOrders(params)).withSelfRel();
    collectionModel.removeLinks();
    collectionModel.add(link);
  }
}
