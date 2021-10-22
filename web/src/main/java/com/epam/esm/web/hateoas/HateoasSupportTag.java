package com.epam.esm.web.hateoas;

import com.epam.esm.entity.Tag;
import com.epam.esm.web.api.TagController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class HateoasSupportTag {

  public static EntityModel<Tag> getModel(Tag tag) {
    EntityModel<Tag> model = EntityModel.of(tag);
    Link selfLink = linkTo(TagController.class).slash(tag.getId()).withSelfRel();
    return model.add(selfLink);
  }

  public static CollectionModel<EntityModel<Tag>> getCollectionModel(List<Tag> tags) {
    List<EntityModel<Tag>> list = new ArrayList<>();
    for (Tag tag : tags) {
      EntityModel<Tag> tagModel = EntityModel.of(tag);
      Link selfLink = linkTo(TagController.class).slash(tag.getId()).withSelfRel();
      tagModel.add(selfLink);
      list.add(tagModel);
    }
    Link link = linkTo(TagController.class).withSelfRel();
    return CollectionModel.of(list, link);
  }

  public static void addParams(
      CollectionModel<EntityModel<Tag>> collectionModel, Map<String, String> params) {
    Link link = linkTo(methodOn(TagController.class).getTags(params)).withSelfRel();
    collectionModel.removeLinks();
    collectionModel.add(link);
  }
}
