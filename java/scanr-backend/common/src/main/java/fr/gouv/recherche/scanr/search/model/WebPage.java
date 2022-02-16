/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.search.model;

/**
 * Represents a crawled webpage and its content
 *
 * @author glebourg
 */
public class WebPage {

    private String title;
    private String content;

    public WebPage() {
    }

    public WebPage(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

}
