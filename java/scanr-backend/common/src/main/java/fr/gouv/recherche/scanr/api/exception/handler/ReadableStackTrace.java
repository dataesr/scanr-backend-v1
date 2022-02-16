/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.api.exception.handler;

import com.google.common.collect.Lists;

import java.util.List;

/**
 *
 */
public class ReadableStackTrace {
    private static final String ALLOWED_PREFIX = "fr.gouv.recherche.scanr";

    public String error;
    public String message;
    public List<String> stack = Lists.newArrayList();

    public ReadableStackTrace(Throwable e) {
        this.message = e.getMessage();
        this.error = e.getClass().getName();
        for (StackTraceElement line : e.getStackTrace()) {
            if (line.getClassName().startsWith(ALLOWED_PREFIX) && line.getLineNumber() >= 0)
                stack.add(line.toString());
        }
    }

    public static List<ReadableStackTrace> getTrace(Throwable e) {
        List<ReadableStackTrace> res = Lists.newArrayList();
        res.add(new ReadableStackTrace(e));
        Throwable cause = e.getCause();
        while (cause != null) {
            res.add(new ReadableStackTrace(cause));
            cause = cause.getCause();
        }
        return res;
    }
}
