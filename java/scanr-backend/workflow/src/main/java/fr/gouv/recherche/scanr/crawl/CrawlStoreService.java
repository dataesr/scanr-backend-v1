/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.crawl;

import java.util.List;

public interface CrawlStoreService {
    /**
     * Return the potential crawl info for a crawl url
     *
     * @param url the normalized url
     * @return A crawl info that matches
     */
    CrawlInfo searchCrawlInfo(String url);

    /**
     * Return the potential crawl id for a crawl url
     *
     * @param url the normalized url
     * @return The crawl_id (or null if absent)
     */
    String searchCrawlId(String url);


    /**
     * Return the pages crawl for a crawl id.
     *
     * @param crawlID the crawl id
     * @return the list of pages
     */
    List<CrawlData> getCrawlPages(String crawlID);

    /**
     * Does exactly what #getCrawlPages does but without a content
     *
     * @param crawlID the crawl id
     * @return the list of pages without content
     */
    List<CrawlData> getCrawlPagesMetaData(String crawlID);

    /**
     * Does exactly what #getCrawlPages does but get only the text data (used for indexation)
     *
     * @param crawlID the crawl id
     * @return the list of texts
     */
    List<CrawlText> getCrawlTexts(String crawlID);
}
