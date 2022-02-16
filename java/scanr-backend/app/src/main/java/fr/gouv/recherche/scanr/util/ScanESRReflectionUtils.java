/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */

package fr.gouv.recherche.scanr.util;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import fr.gouv.recherche.scanr.config.elasticsearch.EsClient;
import fr.gouv.recherche.scanr.db.model.I18nValue;

@Service
public class ScanESRReflectionUtils {

    /**
     * Retourne TRUE si le champ est de type I18nValue, pour le modèle fourni
     * @param object
     * @param fieldPath
     * @return
     */
    public static boolean isFieldTranslatable(Object object, String fieldPath) {
        boolean translatable = Boolean.FALSE;

        List<String> fieldPathList = Arrays.asList(fieldPath.split("\\."));
        AtomicReference<Class<?>> clazz = new AtomicReference<>(object.getClass());
        AtomicReference<Field> field = new AtomicReference<>();
        try {
            fieldPathList.forEach(f->{
                field.set(ReflectionUtils.findField(clazz.get(), f));
                if (field.get().getType() == List.class) {
                    clazz.set((Class)((ParameterizedType) field.get().getGenericType()).getActualTypeArguments()[0]);
                }
                else {
                    clazz.set(field.get().getType());
                }
            });
            if (field.get().getType().equals(I18nValue.class)) {
                translatable = Boolean.TRUE;
            }
        } catch (Exception e) {
            // Sad but in case of error we say it's not translatable
        }

        return translatable;
    }

    /**
     * Retourne TRUE si le champ est de type Number ou Date, pour le modèle fourni
     *
     * @param object
     * @param fieldPath
     * @return
     */
    public static boolean isFieldNumberDateOrBoolean(Object object, String fieldPath) {
        boolean numberDateOrBoolean = Boolean.FALSE;

        List<String> fieldPathList = Arrays.asList(fieldPath.split("\\."));
        AtomicReference<Class<?>> clazz = new AtomicReference<>(object.getClass());
        AtomicReference<Field> field = new AtomicReference<>();
        try {
            fieldPathList.forEach(f->{
                field.set(ReflectionUtils.findField(clazz.get(), f));
                if (field.get().getType() == List.class) {
                    clazz.set((Class)((ParameterizedType) field.get().getGenericType()).getActualTypeArguments()[0]);
                }
                else {
                    clazz.set(field.get().getType());
                }
            });

            if (field.get().getType().getGenericSuperclass() != null) {
                if (field.get().getType().getGenericSuperclass().equals(Number.class) || field.get().getType().getGenericSuperclass().equals(Date.class)) {
                    numberDateOrBoolean = Boolean.TRUE;
                }
            }
            
            if (!numberDateOrBoolean) {
                if (field.get().getType().getTypeName().equalsIgnoreCase(Boolean.class.getSimpleName()) || field.get().getType().getTypeName().equalsIgnoreCase(Boolean.class.getTypeName())) {
                    numberDateOrBoolean = Boolean.TRUE;
                } else if (field.get().getType().getTypeName().equalsIgnoreCase(Date.class.getSimpleName()) || field.get().getType().getTypeName().equalsIgnoreCase(Date.class.getTypeName())) {
                    numberDateOrBoolean = Boolean.TRUE;
                } else if (field.get().getType().getTypeName().equalsIgnoreCase(Number.class.getSimpleName()) || field.get().getType().getTypeName().equalsIgnoreCase(Number.class.getTypeName())) {
                    numberDateOrBoolean = Boolean.TRUE;
                }
            }
        } catch (Exception e) {
            // Sad but in case of error we say it's not keywordType
        }

        return numberDateOrBoolean;
    }

    /**
     * Retourne l'identifiant d'un champ avec, si besoin, la bonne langue et/ou le sous-champ 'keyword'
     * @param object
     * @param fieldPath
     * @param lang
     * @return
     */
    public static String getESFieldIdentifier(Object object, String fieldPath, String lang) {
        if (ScanESRReflectionUtils.isFieldTranslatable(object, fieldPath)) {
            fieldPath += "." + lang;
        }
        if (!ScanESRReflectionUtils.isFieldNumberDateOrBoolean(object,fieldPath)) {
            fieldPath += EsClient.KEYWORD_SUFFIXE;
        }
        return fieldPath;
    }
}
