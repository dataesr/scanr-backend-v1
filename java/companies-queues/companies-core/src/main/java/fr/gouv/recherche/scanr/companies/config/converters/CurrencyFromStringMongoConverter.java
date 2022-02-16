/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.companies.config.converters;

import java.util.Currency;

/**
 *
 */
public class CurrencyFromStringMongoConverter implements org.springframework.core.convert.converter.Converter<String, Currency> {
    @Override
    public Currency convert(String source) {
        return Currency.getInstance(source);
    }
}
