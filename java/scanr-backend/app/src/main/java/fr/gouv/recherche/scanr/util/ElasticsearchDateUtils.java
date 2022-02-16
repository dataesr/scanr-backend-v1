/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.util;

import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ElasticsearchDateUtils {

    public static final String XML_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final String ES_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    /**
     * Retourne une date formatté pour Elasticsearch (convertie en UTC)
     * ==> Apparement, de puis la v5, fournir une Date dans les range query utilise le Date.toString() au lieu du format du mapper JSON ?????
     * @param date
     * @return
     */
    public static String getFormattedDateForES(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(XML_DATE_FORMAT);
        formatter.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
        return formatter.format(date);
    }

    /**
     * Retourne une date formatté pour Elasticsearch (convertie en UTC)
     * ==> Apparement, de puis la v5, fournir une Date dans les range query utilise le Date.toString() au lieu du format du mapper JSON ?????
     * @param dateString
     * @return
     * @throws ParseException
     */
    public static String getFormattedDateForESFromString(String dateString) throws ParseException {
        Date date = DateUtils.parseDate(dateString, "yyyy-MM-dd");
        return getFormattedDateForES(date);
    }

    /**
     * Retourne une date à partir d'un champ date time elasticsearch
     * @param date
     * @return
     * @throws ParseException
     */
    public static Date parseElasticDateTime(String date) throws ParseException {
        if(date.endsWith("Z")){
            //#bout de ruban adhesif, on précise le timezone de la date dans ES, donc UTC
            date = date.substring(0, date.length() -1 ) + "+0000";
        }

        return parseDate(date, ES_DATE_TIME_FORMAT);
    }

    /**
     * Retourne une date string en objet Date java
     * @param date
     * @param format
     * @return
     * @throws ParseException
     */
    public static Date parseDate(String date, String format) throws ParseException {
        SimpleDateFormat formater = new SimpleDateFormat(format);
        return formater.parse(date);
    }
}
