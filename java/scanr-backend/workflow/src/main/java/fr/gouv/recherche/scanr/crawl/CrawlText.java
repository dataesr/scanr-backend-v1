/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.crawl;

public class CrawlText {
    private String title;
    private String content;
    private String lang;

    public CrawlText(String title, String content, String lang) {
        this.title = title;
        this.content = content;
        this.lang = lang;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getLang() {
        return lang;
    }
}
