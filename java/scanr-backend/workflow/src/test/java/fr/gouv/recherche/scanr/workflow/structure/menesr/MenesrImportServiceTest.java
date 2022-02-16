/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.workflow.structure.menesr;

import fr.gouv.recherche.scanr.db.model.CrawlMode;
import org.junit.Test;

import static fr.gouv.recherche.scanr.workflow.menesr.MenesrImportService.inferCrawlMode;
import static org.junit.Assert.*;


public class MenesrImportServiceTest {

    @Test
    public void testInferCrawlMode() throws Exception {
        assertEquals(CrawlMode.FULL_DOMAIN, inferCrawlMode("data-publica.com"));
        assertEquals(CrawlMode.SUBPATH, inferCrawlMode("data-publica.com/stuff"));
        assertEquals(CrawlMode.SINGLE_PAGE, inferCrawlMode("data-publica.com/stuff.php"));
        assertEquals(CrawlMode.SINGLE_PAGE, inferCrawlMode("data-publica.com?id=1"));
        assertEquals(CrawlMode.SINGLE_PAGE, inferCrawlMode("data-publica.com/stuff?id=1"));
        assertEquals(CrawlMode.SINGLE_PAGE, inferCrawlMode("data-publica.com/youpi/trala/stuff?id=1"));
        assertEquals(CrawlMode.SINGLE_PAGE, inferCrawlMode("data-publica.com/youpi/trala/stuff.php"));
        assertEquals(CrawlMode.SUBPATH, inferCrawlMode("data-publica.com/youpi/tra.la/stuff"));
    }
}