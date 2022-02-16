/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.workflow.website.extractor.dto;

import fr.gouv.recherche.scanr.db.model.Address;
import fr.gouv.recherche.scanr.db.model.SocialAccount;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
*
*/
public class CoreExtractorOut {
    public String url;
    public String domain;
    public String country;

    public Set<SocialAccount> dailymotion;
    public Set<SocialAccount> youtube;
    public Set<SocialAccount> twitter;
    public Set<SocialAccount> vimeo;
    public Set<SocialAccount> facebook;
    public Set<SocialAccount> linkedin;
    public Set<SocialAccount> instagram;
    public Set<SocialAccount> viadeo;
    public Set<SocialAccount> googleplus;

    public List<Contact> contact;

    public List<Rss> rss;
    public List<Email> email;
    public List<String> phone;
    public List<String> fax;
    public List<String> contactform;

    public boolean legal;
    public boolean useterms;
    public List<Double> capital;
    public List<String> localId;

    public String description;
    public String metadescription;
    public List<Map<String, Integer>> summary;

    public boolean seo;
    public boolean mobile;
    public boolean responsive;
    public List<String> monitoring;
    public List<PlatformEntry> ecommerce;
    public List<PlatformEntry> cms;

    public Map<String, Integer> outlinks;
    public List<Address> addresses;

    public ECommerceMeta ecommerce_meta;
}
