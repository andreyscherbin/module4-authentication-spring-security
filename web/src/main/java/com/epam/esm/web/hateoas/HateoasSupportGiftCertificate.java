package com.epam.esm.web.hateoas;

import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.web.api.GiftCertificateController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class HateoasSupportGiftCertificate {

  public static EntityModel<GiftCertificate> getModel(GiftCertificate giftCertificate) {
    EntityModel<GiftCertificate> model = EntityModel.of(giftCertificate);
    Link selfLink =
        linkTo(GiftCertificateController.class).slash(giftCertificate.getId()).withSelfRel();
    return model.add(selfLink);
  }

  public static CollectionModel<EntityModel<GiftCertificate>> getCollectionModel(
      List<GiftCertificate> certificates) {
    List<EntityModel<GiftCertificate>> list = new ArrayList<>();
    for (GiftCertificate gc : certificates) {
      EntityModel<GiftCertificate> certificateModel = EntityModel.of(gc);
      Link selfLink = linkTo(GiftCertificateController.class).slash(gc.getId()).withSelfRel();
      certificateModel.add(selfLink);

      list.add(certificateModel);
    }
    Link link = linkTo(GiftCertificateController.class).withSelfRel();
    return CollectionModel.of(list, link);
  }

  public static void addParams(
      CollectionModel<EntityModel<GiftCertificate>> collectionModel, Map<String, String> params) {
    StringBuilder builder = new StringBuilder("");
    String host = linkTo(GiftCertificateController.class).toString();
    builder.append(host).append("?");
    params.forEach((k, v) -> builder.append(k).append("=").append(v).append("&"));
    Link selfLink = Link.of(builder.toString());
    collectionModel.removeLinks();
    collectionModel.add(selfLink);
  }
}
