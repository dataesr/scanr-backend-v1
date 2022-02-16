/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.workflow.website.extractor;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import fr.gouv.recherche.scanr.companies.workflow.MessageQueue;
import fr.gouv.recherche.scanr.companies.workflow.service.QueueComponent;
import fr.gouv.recherche.scanr.companies.workflow.service.QueueListener;
import fr.gouv.recherche.scanr.db.model.*;
import fr.gouv.recherche.scanr.db.repository.OutlinkRepository;
import fr.gouv.recherche.scanr.db.repository.WebsiteRepository;
import fr.gouv.recherche.scanr.util.RepositoryLock;
import fr.gouv.recherche.scanr.workflow.website.WebsiteAnalysisService;
import fr.gouv.recherche.scanr.workflow.website.extractor.dto.CoreExtractorOut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 */
@Component
public class CoreExtractorPlugin extends QueueComponent implements QueueListener<CoreExtractorOut> {

    public static final MessageQueue<In> QUEUE_IN = MessageQueue.get("CORE_EXTRACTOR", In.class);
    public static final MessageQueue<CoreExtractorOut> QUEUE_OUT = MessageQueue.get("CORE_EXTRACTOR_OUT", CoreExtractorOut.class);

    private RepositoryLock<Website, String, WebsiteRepository> repository;

    @Autowired
    private WebsiteAnalysisService websiteAnalysisService;

    @Autowired
    public void setRepository(WebsiteRepository repository) {
        this.repository = RepositoryLock.get(repository);
    }

    @Autowired
    private OutlinkRepository outlinkRepository;

    public void execute(Website w) {
        queueService.push(new In(w), QUEUE_IN, QUEUE_OUT);
    }

    @Override
    public void receive(CoreExtractorOut out) {
        Website saved = repository.update(Website.idFromUrl(out.url), tx -> {
            Website w = tx.getNotNull();
            merge(w, out);
            tx.saveDeferred();
        }).getSaved();
        websiteAnalysisService.analysisEnd(saved.getId());
    }

    private Website merge(Website w, CoreExtractorOut out) {
        setSocial(w, out);
        setCommunication(w, out);
        setSummary(w, out);
        setTech(w, out);
        setECommerce(w, out);
        mergeOutlinks(out.domain, out.url, out.outlinks);
        computeScore(w);
        return w;
    }

    private void computeScore(Website w) {
        /**
         *
         */
        double score = 0;

        if (w.getMonitoring() != null && !w.getMonitoring().isEmpty()) {
            score += 0.2;
        }
        if (w.getCanonical() != null && w.getCanonical()) {
            score += 0.2;
        }
        if ((w.getTwitter() != null && !w.getTwitter().isEmpty()) ||
                (w.getFacebook() != null && !w.getFacebook().isEmpty()) ||
                (w.getLinkedIn() != null && !w.getLinkedIn().isEmpty()) ||
                (w.getViadeo() != null && !w.getViadeo().isEmpty()) ||
                (w.getGooglePlus() != null && !w.getGooglePlus().isEmpty())) {
            score += 0.3;
        }
        if ((w.getYoutube() != null && !w.getYoutube().isEmpty()) ||
                (w.getDailymotion() != null && !w.getDailymotion().isEmpty()) ||
                (w.getVimeo() != null && !w.getVimeo().isEmpty())) {
            score += 0.1;
        }
        if (w.getContactForms() != null && !w.getContactForms().isEmpty()) {
            score += 0.1;
        }
        if (w.getPlatforms() != null && !w.getPlatforms().isEmpty()) {
            score += 0.1;
        }
        w.setQuality(score);
    }

    private void setECommerce(Website w, CoreExtractorOut out) {
        /*
        Code ci-dessous récupéré du categorizer

        # eCommerce has bigger priority than digital
        # Rule Determined after a QA using a decision tree

        if ecommerce["payment"]["pagesWithBasket"] >= 3 and \
            ecommerce["pricing"]["pagesWithPrices"] >= 1 and \
            ecommerce["pricing"]["avgPricesPerPage"] > 1:
            classif = "ecommerce"
        */
        w.setEcommerce(out.ecommerce_meta.pages_with_basket >= 3 && out.ecommerce_meta.pages_with_prices >= 1 && out.ecommerce_meta.avg_prices_per_page > 1);
    }

    private void setSocial(Website w, CoreExtractorOut out) {
        w.setDailymotion(new ArrayList<>(out.dailymotion));
        w.setYoutube(new ArrayList<>(out.youtube));
        w.setTwitter(new ArrayList<>(out.twitter));
        w.setVimeo(new ArrayList<>(out.vimeo));
        w.setFacebook(new ArrayList<>(out.facebook));
        w.setLinkedIn(new ArrayList<>(out.linkedin));
        w.setInstagram(new ArrayList<>(out.instagram));
        w.setViadeo(new ArrayList<>(out.viadeo));
        w.setGooglePlus(new ArrayList<>(out.googleplus));
    }

    private void setCommunication(Website w, CoreExtractorOut out) {
        w.setRss(out.rss.stream().map(it -> new RssFeed(it.url, (float) it.frequency)).collect(Collectors.toList()));
        w.setContactForms(out.contactform);
    }

    private void setSummary(Website w, CoreExtractorOut out) {
        w.setDescription(out.description);
        w.setMetaDescription(out.metadescription);
    }

    private void setTech(Website w, CoreExtractorOut out) {
        w.setCanonical(out.seo);
        w.setMobile(out.mobile);
        w.setResponsive(out.responsive);
        w.setMonitoring(out.monitoring);
        w.setPlatforms(Stream.concat(out.cms.stream(), out.ecommerce.stream()).map(it -> it.type).collect(Collectors.toList()));
    }

    private void mergeOutlinks(String domain, String url, Map<String, Integer> outlinks) {
        Outlink outlink = outlinkRepository.findOne(domain);
        if (outlink == null) {
            outlink = new Outlink();
            outlink.setDomain(domain);
        }
        PerUrlOutDomain perUrl = new PerUrlOutDomain();
        perUrl.setUrl(url);
        List<OutDomain> outDomains = perUrl.getOutDomains();
        domainMapToOutDomains(outDomains, outlinks);

        Map<String, Integer> total = Maps.newHashMap(outlinks);
        List<PerUrlOutDomain> finalPerUrl = Lists.newArrayList(perUrl);
        // now perUrl is built we have to merge the data
        for (PerUrlOutDomain referer : outlink.getReferers()) {
            if (url.equals(referer.getUrl())) {
                // we already have this one
                continue;
            }
            // put old entry and add
            finalPerUrl.add(referer);
            for (OutDomain refOutDomain : referer.getOutDomains()) {
                Integer count = total.get(refOutDomain.getDomain());
                if (count == null)
                    total.put(refOutDomain.getDomain(), refOutDomain.getCount());
                else
                    total.put(refOutDomain.getDomain(), count + refOutDomain.getCount());
            }
        }
        outlink.setReferers(finalPerUrl);
        outlink.getOutDomains().clear();
        domainMapToOutDomains(outlink.getOutDomains(), total);
        outlinkRepository.save(outlink);
    }

    private void domainMapToOutDomains(List<OutDomain> outDomains, Map<String, Integer> outlinks) {
        for (Map.Entry<String, Integer> entry : outlinks.entrySet()) {
            OutDomain o = new OutDomain();
            o.setDomain(entry.getKey());
            o.setCount(entry.getValue());
            outDomains.add(o);
        }
    }

    @Override
    public MessageQueue<CoreExtractorOut> getQueue() {
        return QUEUE_OUT;
    }


    public static class In {
        public String url;
        public String country = "FR";
        public String domain;

        public In() {
        }

        public In(Website w) {
            this.url = w.getBaseURL();
            domain = extractDomain(w);
        }
    }

    private static String extractDomain(Website w) {
        try {
            return new URL(w.getBaseURL()).getHost();
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Can not have a malformed URL exception at that point " + w.getBaseURL());
        }
    }
}
