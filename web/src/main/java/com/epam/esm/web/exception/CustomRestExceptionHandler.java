package com.epam.esm.web.exception;

import static com.epam.esm.entity.ErrorCode.BAD_INPUT;
import static com.epam.esm.entity.ErrorCode.GIFT_CERTIFICATE_ERROR_CODE;
import static com.epam.esm.entity.ErrorCode.PARAM_ERROR;
import static com.epam.esm.entity.ErrorCode.SERVER_ERROR;
import static com.epam.esm.entity.ErrorCode.TAG_ERROR_CODE;

import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import com.epam.esm.service.exception.ValidationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomRestExceptionHandler extends ResponseEntityExceptionHandler {

  Logger logger = LoggerFactory.getLogger(CustomRestExceptionHandler.class);

  @Autowired private MessageSource messageSource;

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    List<String> errors = new ArrayList<>();
    for (FieldError error : ex.getBindingResult().getFieldErrors()) {
      errors.add(error.getField() + ": " + error.getDefaultMessage());
    }
    for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
      errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
    }
    String stringStatus = String.valueOf(status.value());
    int errorCode = 0;
    Object object = ex.getBindingResult().getTarget();
    if (object.getClass() == Tag.class) {
      errorCode = TAG_ERROR_CODE;
    } else if (object.getClass() == GiftCertificate.class) {
      errorCode = GIFT_CERTIFICATE_ERROR_CODE;
    }
    ApiError apiError = new ApiError(errors, stringStatus + errorCode);
    return handleExceptionInternal(ex, apiError, headers, status, request);
  }

  @ExceptionHandler({ConstraintViolationException.class})
  public ResponseEntity<Object> handleConstraintViolation(
      ConstraintViolationException ex, WebRequest request) {
    List<String> errors = new ArrayList<>();
    for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
      String paramName = null;
      for (Path.Node node : violation.getPropertyPath()) {
        paramName = node.getName();
      }
      errors.add(paramName + ": " + violation.getMessage());
    }
    String status = String.valueOf(HttpStatus.BAD_REQUEST.value());
    ApiError apiError = new ApiError(errors, status + PARAM_ERROR);
    return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler({ValidationException.class})
  public ResponseEntity<Object> handleValidationException(ValidationException ex) {
    List<String> errors = new ArrayList<>();
    String status;
    ApiError apiError;
    if (ex.getErrors() != null) {
      for (FieldError fieldError : ex.getErrors().getFieldErrors()) {
        String paramName = fieldError.getField();
        String message =
            messageSource.getMessage(fieldError.getCode(), null, LocaleContextHolder.getLocale());
        errors.add(paramName + ": " + message);
      }
      status = String.valueOf(HttpStatus.BAD_REQUEST.value());
      apiError = new ApiError(errors, status + PARAM_ERROR);
    } else {
      String message =
          messageSource.getMessage(ex.getMessage(), null, LocaleContextHolder.getLocale());
      errors.add(message);
      status = String.valueOf(HttpStatus.BAD_REQUEST.value());
      apiError = new ApiError(errors, status + BAD_INPUT);
    }
    return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ResourceException.class)
  public ResponseEntity<ApiError> resourceExceptionHandler(ResourceException e) {
    Long[] resourcesId = Arrays.stream(e.getResourceId()).boxed().toArray(Long[]::new);
    String message =
        messageSource.getMessage(e.getMessage(), resourcesId, LocaleContextHolder.getLocale());
    String status = String.valueOf(e.getStatus().value());
    ApiError apiError = new ApiError(List.of(message), status + e.getResourceType());
    return new ResponseEntity<>(apiError, e.getStatus());
  }

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(
      HttpMessageNotReadableException ex,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {

    String message = messageSource.getMessage("input.error", null, LocaleContextHolder.getLocale());
    String stringStatus = String.valueOf(status.value());
    ApiError apiError = new ApiError(List.of(message), stringStatus + BAD_INPUT);
    return handleExceptionInternal(ex, apiError, headers, status, request);
  }

  @ExceptionHandler({EnumConstantNotPresentException.class})
  public ResponseEntity<Object> handleEnum(EnumConstantNotPresentException ex) {
    String status = String.valueOf(HttpStatus.BAD_REQUEST.value());
    String noSuchConstantMessage =
        messageSource.getMessage(
            "error.enum.constant.not.present", null, LocaleContextHolder.getLocale());
    ApiError apiError =
        new ApiError(
            List.of(ex.constantName() + " : " + noSuchConstantMessage), status + PARAM_ERROR);
    return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler({Exception.class})
  public ResponseEntity<Object> handleAll(Exception ex) {
    logger.error("general exception handler", ex);
    String errorMessage;
    int errorCode;
    HttpStatus status;
    if (ex instanceof IllegalArgumentException) {
      status = HttpStatus.BAD_REQUEST;
      errorMessage = ex.getMessage();
      errorCode = PARAM_ERROR;
    } else {
      status = HttpStatus.INTERNAL_SERVER_ERROR;
      errorMessage =
          messageSource.getMessage(
              "error_500.error_details", null, LocaleContextHolder.getLocale());
      errorCode = SERVER_ERROR;
    }
    String strStatus = String.valueOf(status.value());
    ApiError apiError = new ApiError(List.of(errorMessage), strStatus + errorCode);
    return new ResponseEntity<>(apiError, new HttpHeaders(), status);
  }
}
