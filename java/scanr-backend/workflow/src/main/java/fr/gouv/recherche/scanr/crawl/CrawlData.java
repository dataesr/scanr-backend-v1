/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.crawl;

import java.util.Date;
import java.util.Map;

/**
 *
 */
public class CrawlData {

    private String crawlId;
    private String url;
    private String charset;
    private String content;
    private String contentType;
    private Date crawlDate;
    private int depth;
    private String domain;
    private Map<String, String> headers;
    private int httpStatus;
    private String title;
    private String relevantTxt;
    private String lang;

    public CrawlData(String crawlId, String url, String charset, String content, String contentType, 
                     Date crawlDate, int depth, String domain, Map<String, String> headers, 
                     int httpStatus, String title, String relevantTxt, String lang) {
        this.crawlId = crawlId;
        this.url = url;
        this.charset = charset;
        this.content = content;
        this.contentType = contentType;
        this.crawlDate = crawlDate;
        this.depth = depth;
        this.domain = domain;
        this.headers = headers;
        this.httpStatus = httpStatus;
        this.title = title;
        this.relevantTxt = relevantTxt;
        this.lang = lang;
    }

    public String getRelevantTxt() {
        return relevantTxt;
    }

    public String getCrawlId() {
        return crawlId;
    }

    public String getUrl() {
        return url;
    }

    public String getCharset() {
        return charset;
    }

    public String getContent() {
        return content;
    }

    public String getContentType() {
        return contentType;
    }

    public Date getCrawlDate() {
        return crawlDate;
    }

    public int getDepth() {
        return depth;
    }

    public String getDomain() {
        return domain;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public String getTitle() {
        return title;
    }

    public String getLang() { return lang; }
}
