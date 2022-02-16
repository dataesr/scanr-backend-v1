/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.util;

import org.junit.Test;

import static fr.gouv.recherche.scanr.util.NormalizeURL.normalize;
import static fr.gouv.recherche.scanr.util.NormalizeURL.normalizeForIdentification;
import static org.junit.Assert.*;


public class NormalizeURLTest {

    @Test
    public void testNormalizeForIdentification() throws Exception {
        assertEquals("data-publica.com", normalizeForIdentification("http://www.data-publica.com/"));
        assertEquals("data-publica.com/stuff", normalizeForIdentification("http://www.data-publica.com/stuff/"));
        assertEquals("data-publica.com/stuff.php?=youpi&canard=%20test%20&id=1", normalizeForIdentification("http://www.data-publica.com/stuff.php?id=1&canard=%20test%20&=youpi"));
        assertEquals("data-publica.com/stuff.php?=youpi&canard=%20test%20&id=1", normalizeForIdentification("http://www.data-publica.com/stuff.php?id=1&canard=%20test%20&=youpi&utm_source=twitter"));
        assertEquals("data-publica.com:8080/stuff", normalizeForIdentification("https://www.data-publica.com:8080/stuff"));
        assertEquals("data-publica.com", normalizeForIdentification("http://www.data-publica.com/index.php"));
        assertEquals("data-publica.com/bidule", normalizeForIdentification("http://www.data-publica.com/bidule/index.php"));
        try {
            normalizeForIdentification("www");
            fail("Should have trigger IAE");
        } catch (IllegalArgumentException ignored) {

        }
    }

    @Test
    public void testNormalize() throws Exception {
        assertEquals("http://www.data-publica.com/index.php", normalize("http://www.data-publica.com/index.php"));
        assertEquals("http://www.data-publica.com/bidule/index.php", normalize("http://www.data-publica.com/bidule/index.php"));
    }
}