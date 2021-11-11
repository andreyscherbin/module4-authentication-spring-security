package com.epam.esm.web.api;

import com.epam.esm.entity.DTO;
import com.epam.esm.entity.Tag;
import com.epam.esm.service.TagService;
import com.epam.esm.web.exception.ResourceException;
import com.epam.esm.web.hateoas.HateoasSupportTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.epam.esm.entity.ErrorCode.TAG_ERROR_CODE;

@RestController
@RequestMapping("/tags")
@Validated
public class TagController {

  private final TagService tagService;

  @Autowired
  public TagController(TagService tagService) {
    this.tagService = tagService;
  }

  @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
  @GetMapping(produces = "application/json")
  public DTO getTags(@RequestParam Map<String, String> params) {
    List<Tag> tags = tagService.find(params);
    DTO dto = new DTO();
    dto.setResult(HateoasSupportTag.getCollectionModel(tags));
    dto.setTotalRows(tagService.getTotalRows());
    HateoasSupportTag.addParams((CollectionModel<EntityModel<Tag>>) dto.getResult(), params);
    return dto;
  }

  @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
  @GetMapping(value = "/{id}", produces = "application/json")
  public EntityModel<Tag> getTag(@PathVariable Long id) {
    Tag tag =
        tagService
            .findTagById(id)
            .orElseThrow(
                () ->
                    new ResourceException(
                        "tag.not_found", HttpStatus.NOT_FOUND, TAG_ERROR_CODE, id));
    return HateoasSupportTag.getModel(tag);
  }

  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  @PostMapping(consumes = "application/json")
  @ResponseStatus(HttpStatus.CREATED)
  public EntityModel<Tag> saveTag(@RequestBody @Valid Tag tag) {
    Tag createdTag =
        tagService
            .createTag(tag)
            .orElseThrow(
                () ->
                    new ResourceException(
                        "tag.already_exists", HttpStatus.CONFLICT, TAG_ERROR_CODE));
    return HateoasSupportTag.getModel(createdTag);
  }

  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  @PutMapping(value = "/{id}", consumes = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public EntityModel<Tag> updateTag(@PathVariable Long id, @RequestBody @Valid Tag tag) {
    tag.setId(id);
    EntityModel<Tag> model;
    Optional<Tag> foundTag = tagService.findTagById(id);
    if (foundTag.isPresent()) {
      if (tagService.update(tag)) {
        model = HateoasSupportTag.getModel(tag);
      } else {
        throw new ResourceException("tag.already_exists", HttpStatus.CONFLICT, TAG_ERROR_CODE);
      }
    } else {
      throw new ResourceException("tag.not_found", HttpStatus.NOT_FOUND, TAG_ERROR_CODE, id);
    }
    return model;
  }

  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  @DeleteMapping(value = "/{id}", produces = "application/json")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteTag(@PathVariable Long id) {
    tagService
        .findTagById(id)
        .orElseThrow(
            () -> new ResourceException("tag.not_found", HttpStatus.NOT_FOUND, TAG_ERROR_CODE, id));
    tagService.delete(id);
  }

  @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
  @GetMapping(value = "/popular", produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public CollectionModel<EntityModel<Tag>> getMostPopularTag() {
    List<Tag> tags = tagService.getMostPopularTags();
    return HateoasSupportTag.getCollectionModel(tags);
  }
}
