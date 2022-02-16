/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.api.util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class ExportUtil {

    public static final int EXPORT_LIMIT = 1000;
    public static String BASE_URL;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * @param query
     * @return
     * @throws UnsupportedEncodingException
     * @throws IOException
     * @throws JsonParseException
     * @throws JsonMappingException
     */
    public static <T> T resolveFrontString(String query, Class<T> clazz) throws UnsupportedEncodingException, IOException, JsonParseException, JsonMappingException {
        return objectMapper.readValue(decode(query), clazz);
    }

    public static String decode(String query) throws UnsupportedEncodingException {
        if (StringUtils.isEmpty(query)) {
            return "";
        }

        String queryUtf8 = new String(Base64.getDecoder().decode(query), StandardCharsets.UTF_8);
        return URLDecoder.decode(queryUtf8, StandardCharsets.UTF_8.name());

    }

    @Value("${site.url}")
    public void setBaseUrl(String baseUrl) {
        BASE_URL = baseUrl;
    }
}
