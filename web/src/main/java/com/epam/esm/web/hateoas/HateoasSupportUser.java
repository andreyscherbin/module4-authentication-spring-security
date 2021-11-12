package com.epam.esm.web.hateoas;

import com.epam.esm.entity.LoginResult;
import com.epam.esm.entity.RegisterResult;
import com.epam.esm.entity.User;
import com.epam.esm.web.api.UserController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class HateoasSupportUser {

  public static EntityModel<User> getModel(User user) {
    EntityModel<User> model = EntityModel.of(user);
    Link selfLink = linkTo(UserController.class).slash(user.getId()).withSelfRel();
    return model.add(selfLink);
  }

  public static CollectionModel<EntityModel<User>> getCollectionModel(List<User> users) {
    List<EntityModel<User>> list = new ArrayList<>();
    for (User user : users) {
      EntityModel<User> userModel = EntityModel.of(user);
      Link selfLink = linkTo(UserController.class).slash(user.getId()).withSelfRel();
      userModel.add(selfLink);
      list.add(userModel);
    }
    Link link = linkTo(UserController.class).withSelfRel();
    return CollectionModel.of(list, link);
  }

  public static void addParams(
      CollectionModel<EntityModel<User>> collectionModel, Map<String, String> params) {
    Link link = linkTo(methodOn(UserController.class).getUsers(params)).withSelfRel();
    collectionModel.removeLinks();
    collectionModel.add(link);
  }

  public static EntityModel<RegisterResult> getModel(RegisterResult registerResult) {
    EntityModel<RegisterResult> model = EntityModel.of(registerResult);
    Link selfLink =
        linkTo(UserController.class).slash(registerResult.getUser().getId()).withSelfRel();
    return model.add(selfLink);
  }

  public static EntityModel<LoginResult> getModel(LoginResult loginResult) {
    EntityModel<LoginResult> model = EntityModel.of(loginResult);
    Link selfLink = linkTo(UserController.class).slash(loginResult.getUser().getId()).withSelfRel();
    return model.add(selfLink);
  }
}
