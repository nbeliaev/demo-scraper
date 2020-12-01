package dev.fr13.util;

import dev.fr13.domain.WebSite;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class WebSiteConverter implements AttributeConverter<WebSite, String> {

    @Override
    public String convertToDatabaseColumn(WebSite webSite) {
        if (webSite == null) {
            return null;
        } else {
            return webSite.getName();
        }
    }

    @Override
    public WebSite convertToEntityAttribute(String s) {
        if (s.isEmpty()) {
            return null;
        } else {
            return WebSite.valueByName(s);
        }
    }
}