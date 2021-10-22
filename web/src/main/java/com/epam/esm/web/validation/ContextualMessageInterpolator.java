package com.epam.esm.web.validation;

import java.util.Locale;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.hibernate.validator.resourceloading.PlatformResourceBundleLocator;
import org.springframework.context.i18n.LocaleContextHolder;

public class ContextualMessageInterpolator extends ResourceBundleMessageInterpolator {

  private static final String BUNDLE_NAME = "messages/exceptions";

  public ContextualMessageInterpolator() {
    super(new PlatformResourceBundleLocator(BUNDLE_NAME));
  }

  @Override
  public String interpolate(String template, Context context) {
    return super.interpolate(template, context, LocaleContextHolder.getLocale());
  }

  @Override
  public String interpolate(String template, Context context, Locale locale) {
    return super.interpolate(template, context, LocaleContextHolder.getLocale());
  }
}
