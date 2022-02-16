/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.crawl;

import java.util.Date;
import java.util.Map;

public class CrawlInfo {

    private String url;
    private int depth;
    private int maxPages;
    private Date endDate;
    private String id;
    private int pageCount;
    private Date startDate;
    private String status;
    private Map<String, Integer> histogram;
    private String mainLang;

    public CrawlInfo(String url, int depth, int maxPages, Date endDate, String id, int pageCount, Date startDate, String status, Map<String, Integer> histogram, String mainLang) {
        this.url = url;
        this.depth = depth;
        this.maxPages = maxPages;
        this.endDate = endDate;
        this.id = id;
        this.pageCount = pageCount;
        this.startDate = startDate;
        this.status = status;
        this.histogram = histogram;
        this.mainLang = mainLang;
    }
    
    public Map<String, Integer> getHistogram() {
        return histogram;
    }

    public String getUrl() {
        return url;
    }

    public int getDepth() {
        return depth;
    }

    public int getMaxPages() {
        return maxPages;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getId() {
        return id;
    }

    public int getPageCount() {
        return pageCount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public String getStatus() {
        return status;
    }

    public String getMainLang() { return mainLang; }
}
