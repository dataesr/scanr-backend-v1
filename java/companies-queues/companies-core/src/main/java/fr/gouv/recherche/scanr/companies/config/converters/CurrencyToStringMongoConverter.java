/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.companies.config.converters;

import java.util.Currency;

/**
 *
 */
public class CurrencyToStringMongoConverter implements org.springframework.core.convert.converter.Converter<Currency, String> {
    @Override
    public String convert(Currency source) {
        return source.getCurrencyCode();
    }
}
