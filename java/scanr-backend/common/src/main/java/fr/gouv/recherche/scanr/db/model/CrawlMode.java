/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.db.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;

/**
 * Crawl mode for the domain
 * <ul>
 *     <li>SINGLE_PAGE : website consisting in a single page</li>
 *     <li>SUBPATH : website consisting in a subpath of a url (all pages below)</li>
 *     <li>SINGLE_PAGE : website consisting in a whole domain</li>
 * </ul>
 */
@ApiModel("v2.CrawlMode")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public enum CrawlMode {
    SINGLE_PAGE, SUBPATH, FULL_DOMAIN
}
