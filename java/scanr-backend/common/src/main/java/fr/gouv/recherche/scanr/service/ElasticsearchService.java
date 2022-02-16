/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.service;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.ElasticsearchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import fr.gouv.recherche.scanr.api.exception.ElasticException;
import fr.gouv.recherche.scanr.api.exception.ServiceException;
import fr.gouv.recherche.scanr.config.elasticsearch.EsClient;
import fr.gouv.recherche.scanr.config.elasticsearch.EsClusterEnum;

@Service
public class ElasticsearchService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchService.class);

    @Value("${elasticsearch.hosts:scanr-elasticsearch}")
    protected String esHost;

    @Value("${elasticsearch.cluster:scanr-cluster}")
    protected String esCluster;

    @Value("${elasticsearch.port:9200}")
    protected Integer esPort;
    
    @Value("${elasticsearch.user}")
    protected String esUser;
    
    @Value("${elasticsearch.password}")
    protected String esPassword;
    
    @Value("${elasticsearch.caPath}")
    protected String esCaPath;

    protected EsClient esClient;

    @PostConstruct
    protected void initEsClient() throws ServiceException, InterruptedException {

        // Récupération des paramètres de connexion
        if (StringUtils.isEmpty(esHost) || StringUtils.isEmpty(esCluster) || (esPort == 0)) {
            throw new ServiceException("Les valeurs d'intialisation du service Elastic ne sont pas correctes. esHost: [" + esHost + "], esCluster: [" + esCluster + "], esPort: [" + esPort + "]");
        }

        // On va essayer de se connecter plusieurs fois à ES qui peut parfois prendre du temps à démarrer
        // Surtout utile pour un run par Maven
        int remainingTries = 6;
        while ((remainingTries > 0) && (esClient == null)) {
            try {
                esClient = new EsClient(EsClusterEnum.SCANR, esCluster, esHost, esPort, esUser, esPassword, esCaPath);
            } catch (ElasticsearchException e) {
                remainingTries--;
                LOGGER.warn("ElasticSearch n'est pas encore disponible, nouvel essai dans 10s. Essais restants :" + remainingTries, e);
                TimeUnit.SECONDS.sleep(10);
            } catch (ElasticException e) {
                throw new ServiceException("Erreur lors de l'intialisation du client ElasticSearch", e);
            }
        }

        if (esClient == null) {
            throw new ElasticsearchException("Erreur lors de l'intialisation du client ElasticSearch");
        }
    }

    public EsClient getEsClient() {
        return esClient;
    }
}
