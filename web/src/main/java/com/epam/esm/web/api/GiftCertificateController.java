package com.epam.esm.web.api;

import com.epam.esm.entity.DTO;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import com.epam.esm.service.GiftCertificateService;
import com.epam.esm.service.TagService;
import com.epam.esm.web.exception.ResourceException;
import com.epam.esm.web.hateoas.HateoasSupportGiftCertificate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.ApplicationScope;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.epam.esm.entity.ErrorCode.GIFT_CERTIFICATE_ERROR_CODE;

@ApplicationScope
@RestController
@RequestMapping("/certificates")
@Validated
public class GiftCertificateController {

  private GiftCertificateService giftCertificateService;
  private TagService tagService;

  @Autowired
  public GiftCertificateController(
      GiftCertificateService giftCertificateService,
      TagService tagService) {
    this.giftCertificateService = giftCertificateService;
    this.tagService = tagService;
  }

  @GetMapping(produces = "application/json")
  public DTO getGiftCertificates(
      @RequestParam Map<String, String> params,
      @RequestParam(defaultValue = "") @Valid List<Tag> tags) {
    List<GiftCertificate> certificates = giftCertificateService.find(params, tags);
    DTO dto = new DTO();
    dto.setResult(HateoasSupportGiftCertificate.getCollectionModel(certificates));
    dto.setTotalRows(giftCertificateService.getTotalRows());
    HateoasSupportGiftCertificate.addParams(
        (CollectionModel<EntityModel<GiftCertificate>>) dto.getResult(), params);
    return dto;
  }

  @GetMapping(value = "/{id}", produces = "application/json")
  public EntityModel<GiftCertificate> getGiftCertificateById(@PathVariable Long id) {
    GiftCertificate gc =
        giftCertificateService
            .findById(id)
            .orElseThrow(
                () ->
                    new ResourceException(
                        "certificate.not_found",
                        HttpStatus.NOT_FOUND,
                        GIFT_CERTIFICATE_ERROR_CODE,
                        id));
    return HateoasSupportGiftCertificate.getModel(gc);
  }

  @PostMapping(consumes = "application/json")
  @ResponseStatus(HttpStatus.CREATED)
  public EntityModel<GiftCertificate> saveGiftCertificate(
      @RequestBody @Valid GiftCertificate giftCertificate) {
    List<Tag> tags = tagService.createTags(giftCertificate.getTags());
    giftCertificate.setTags(tags);
    long idResource = giftCertificateService.create(giftCertificate).getId();
    Optional<GiftCertificate> createdGift = giftCertificateService.findById(idResource);
    return HateoasSupportGiftCertificate.getModel(createdGift.get());
  }

  @PutMapping(value = "/{id}", consumes = "application/json")
  public EntityModel<GiftCertificate> updateGiftCertificate(
      @PathVariable Long id, @RequestBody @Valid GiftCertificate giftCertificate) {
    EntityModel<GiftCertificate> model;
    giftCertificate.setId(id);
    List<Tag> tags = tagService.createTags(giftCertificate.getTags());
    giftCertificate.setTags(tags);
    Optional<GiftCertificate> foundGiftCertificate = giftCertificateService.findById(id);
    if (foundGiftCertificate.isPresent()) {
      giftCertificateService.updateFull(giftCertificate);
      Optional<GiftCertificate> updatedGift = giftCertificateService.findById(id);
      model = HateoasSupportGiftCertificate.getModel(updatedGift.get());
    } else {
      throw new ResourceException(
          "certificate.not_found", HttpStatus.NOT_FOUND, GIFT_CERTIFICATE_ERROR_CODE, id);
    }
    return model;
  }

  @PatchMapping(value = "/{id}", consumes = "application/json")
  public EntityModel<GiftCertificate> partialUpdateGiftCertificate(
      @RequestBody Map<String, Object> updates, @PathVariable Long id) {
    EntityModel<GiftCertificate> model;
    Optional<GiftCertificate> foundGiftCertificate = giftCertificateService.findById(id);
    if (foundGiftCertificate.isPresent()) {
      giftCertificateService.updatePartial(updates, id);
      Optional<GiftCertificate> updatedGift = giftCertificateService.findById(id);
      model = HateoasSupportGiftCertificate.getModel(updatedGift.get());
    } else {
      throw new ResourceException(
          "certificate.not_found", HttpStatus.NOT_FOUND, GIFT_CERTIFICATE_ERROR_CODE, id);
    }
    return model;
  }

  @DeleteMapping(value = "/{id}", produces = "application/json")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteGiftCertificate(@PathVariable Long id) {
    giftCertificateService
        .findById(id)
        .orElseThrow(
            () ->
                new ResourceException(
                    "certificate.not_found",
                    HttpStatus.NOT_FOUND,
                    GIFT_CERTIFICATE_ERROR_CODE,
                    id));
    giftCertificateService.delete(id);
  }
}
