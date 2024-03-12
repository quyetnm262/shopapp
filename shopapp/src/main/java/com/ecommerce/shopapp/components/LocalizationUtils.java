package com.ecommerce.shopapp.components;


import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;

@RequiredArgsConstructor
@Component
public class LocalizationUtils {

    private final MessageSource messageSource;

    private final LocaleResolver localeResolver;

    public String getLocalizationMessage(String messageKey, Object... params){

        Locale locale = localeResolver.resolveLocale(WebUtils.getCurrentRequest());

        return messageSource.getMessage(messageKey, params, locale);

    }

}
