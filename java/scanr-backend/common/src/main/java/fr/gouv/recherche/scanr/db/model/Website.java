/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.db.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import fr.gouv.recherche.scanr.db.model.CrawlMode;
import fr.gouv.recherche.scanr.db.model.RssFeed;
import fr.gouv.recherche.scanr.db.model.SocialAccount;
import fr.gouv.recherche.scanr.search.model.WebPage;
import fr.gouv.recherche.scanr.util.NormalizeURL;
import io.swagger.annotations.ApiModel;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@Document
@ApiModel(value="v2.Website", description="Same as v1")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Website {
    /**
     * Id of the website
     * Simplified url to avoid duplicates (www. for instance, or http/https)
     */
    @Id
    private String id;

    /**
     * Crawl entry point
     */
    private String baseURL;

    /**
     * Crawl mode
     */
    private CrawlMode crawlMode;

    /**
     * Monitoring platforms identified
     */
    private List<String> monitoring;

    /**
     * True if website is identified as canonical
     */
    private Boolean canonical;

    /**
     * True if website is identified as responsive
     */
    private Boolean responsive;
    /**
     * True if website is identified as mobile friendly
     */
    private Boolean mobile;

    /**
     * True if website is identified as an eCommerce website
     */
    private Boolean ecommerce;

    /**
     * CMS platforms identified
     */
    private List<String> platforms;
    /**
     * RSS feeds
     */
    private List<RssFeed> rss;

    /**
     * Ids of projects detected using named entity detection
     */
    private List<String> extractedProjects = new LinkedList<>();

    /**
     * Ids of publications (e.g. mosty patents) using named entity detection
     */
    private List<String> extractedPublications = new LinkedList<>();

    /**
     * Resolved publications from the publication extractor
     */
    private List<String> resolvedPublications = new LinkedList<>();

    /**
     * scocial account extracted
     */
    private List<SocialAccount> facebook = new ArrayList<>();
    /**
     * scocial account extracted
     */
    private List<SocialAccount> linkedIn = new ArrayList<>();
    /**
     * scocial account extracted
     */
    private List<SocialAccount> viadeo = new ArrayList<>();
    /**
     * scocial account extracted
     */
    private List<SocialAccount> youtube = new ArrayList<>();
    /**
     * scocial account extracted
     */
    private List<SocialAccount> twitter = new ArrayList<>();
    /**
     * scocial account extracted
     */
    private List<SocialAccount> googlePlus = new ArrayList<>();
    /**
     * scocial account extracted
     */
    private List<SocialAccount> dailymotion = new ArrayList<>();
    /**
     * scocial account extracted
     */
    private List<SocialAccount> vimeo = new ArrayList<>();
    /**
     * scocial account extracted
     */
    private List<SocialAccount> instagram = new ArrayList<>();

    /**
     * List of contact form urls
     */
    private List<String> contactForms;

    /**
     * description extracted from website or social networks
     */
    private String description;
    /**
     * html meta description
     */
    private String metaDescription;

    private Double quality;

    @CreatedDate
    private Date createdDate;

    @LastModifiedDate
    private Date lastUpdated;

    /**
     * last completion of the crawl
     */
    private Date lastCompletion;

    /**
     * Number of pages crawled
     */
    private int pageCount;

    private List<WebPage> webPages;

    public Website() {
    }

    public Website(String baseURL, CrawlMode crawlMode) {
        id = idFromUrl(baseURL);
        this.baseURL = baseURL;
        this.crawlMode = crawlMode;
    }

    public Website(String id, String baseURL, CrawlMode crawlMode) {
        this.id = id;
        this.baseURL = baseURL;
        this.crawlMode = crawlMode;
    }

    public String getBaseURL() {
        return baseURL;
    }

    public CrawlMode getCrawlMode() {
        return crawlMode;
    }

    public void setCrawlMode(CrawlMode crawlMode) {
        this.crawlMode = crawlMode;
    }

    public String getId() {
        return id;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public Date getLastCompletion() {
        return lastCompletion;
    }

    public void setLastCompletion(Date lastCompletion) {
        this.lastCompletion = lastCompletion;
    }

    public static String idFromUrl(String baseURL) {
        return NormalizeURL.normalizeForIdentification(baseURL);
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }

    public List<String> getMonitoring() {
        return monitoring;
    }

    public void setMonitoring(List<String> monitoring) {
        this.monitoring = monitoring;
    }

    public Boolean getCanonical() {
        return canonical;
    }

    public void setCanonical(Boolean canonical) {
        this.canonical = canonical;
    }

    public Boolean getResponsive() {
        return responsive;
    }

    public void setResponsive(Boolean responsive) {
        this.responsive = responsive;
    }

    public Boolean getMobile() {
        return mobile;
    }

    public void setMobile(Boolean mobile) {
        this.mobile = mobile;
    }

    public Boolean getEcommerce() {
        return ecommerce;
    }

    public void setEcommerce(Boolean ecommerce) {
        this.ecommerce = ecommerce;
    }

    public List<String> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(List<String> platforms) {
        this.platforms = platforms;
    }

    public List<RssFeed> getRss() {
        return rss;
    }

    public void setRss(List<RssFeed> rss) {
        this.rss = rss;
    }

    public List<SocialAccount> getFacebook() {
        return facebook;
    }

    public void setFacebook(List<SocialAccount> facebook) {
        this.facebook = facebook;
    }

    public List<SocialAccount> getLinkedIn() {
        return linkedIn;
    }

    public void setLinkedIn(List<SocialAccount> linkedIn) {
        this.linkedIn = linkedIn;
    }

    public List<SocialAccount> getViadeo() {
        return viadeo;
    }

    public void setViadeo(List<SocialAccount> viadeo) {
        this.viadeo = viadeo;
    }

    public List<SocialAccount> getYoutube() {
        return youtube;
    }

    public void setYoutube(List<SocialAccount> youtube) {
        this.youtube = youtube;
    }

    public List<SocialAccount> getTwitter() {
        return twitter;
    }

    public void setTwitter(List<SocialAccount> twitter) {
        this.twitter = twitter;
    }

    public List<SocialAccount> getGooglePlus() {
        return googlePlus;
    }

    public void setGooglePlus(List<SocialAccount> googlePlus) {
        this.googlePlus = googlePlus;
    }

    public List<SocialAccount> getDailymotion() {
        return dailymotion;
    }

    public void setDailymotion(List<SocialAccount> dailymotion) {
        this.dailymotion = dailymotion;
    }

    public List<SocialAccount> getVimeo() {
        return vimeo;
    }

    public void setVimeo(List<SocialAccount> vimeo) {
        this.vimeo = vimeo;
    }

    public List<SocialAccount> getInstagram() {
        return instagram;
    }

    public void setInstagram(List<SocialAccount> instagram) {
        this.instagram = instagram;
    }


    public List<String> getContactForms() {
        return contactForms;
    }

    public void setContactForms(List<String> contactForms) {
        this.contactForms = contactForms;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMetaDescription() {
        return metaDescription;
    }

    public void setMetaDescription(String metaDescription) {
        this.metaDescription = metaDescription;
    }

    public Double getQuality() {
        return quality;
    }

    public void setQuality(Double quality) {
        this.quality = quality;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public int getPageCount() {
        return pageCount;
    }

    public List<String> getExtractedPublications() {
        return extractedPublications;
    }

    public void setExtractedPublications(List<String> extractedPublications) {
        this.extractedPublications = extractedPublications;
    }

    public List<String> getExtractedProjects() {
        return extractedProjects;
    }

    public void setExtractedProjects(List<String> extractedProjects) {
        this.extractedProjects = extractedProjects;
    }

    public List<String> getResolvedPublications() {
        return resolvedPublications;
    }

    public void setResolvedPublications(List<String> resolvedPublications) {
        this.resolvedPublications = resolvedPublications;
    }

    public List<WebPage> getWebPages() {
        return webPages;
    }

    public void setWebPages(List<WebPage> webPages) {
        this.webPages = webPages;
    }
}
