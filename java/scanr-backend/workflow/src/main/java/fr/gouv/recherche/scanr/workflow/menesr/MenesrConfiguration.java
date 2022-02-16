/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.workflow.menesr;

import fr.gouv.recherche.scanr.db.model.Person;
import fr.gouv.recherche.scanr.db.model.Project;
import fr.gouv.recherche.scanr.db.model.Structure;
import fr.gouv.recherche.scanr.db.model.publication.Publication;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpGet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;


@Configuration
public class MenesrConfiguration {
    @Value("${menesr.publication.dataset:https://data.enseignementsup-recherche.gouv.fr/explore/dataset/rnsr-publications-scientifiques}")
    private String publicationDataset;
    @Value("${menesr.publication.username:}")
    private String publicationUsername;
    @Value("${menesr.publication.password:}")
    private String publicationPassword;

    @Value("${menesr.resources.directory}")
    private String menesrResourcesDirectory;
    @Value("${menesr.resources.filename.person}")
    private String personsFilename;
    @Value("${menesr.resources.filename.project}")
    private String projectsFilename;
    @Value("${menesr.resources.filename.publication}")
    private String publicationsFilename;
    @Value("${menesr.resources.filename.structure}")
    private String structuresFilename;

    private String odsAuthorization;

    @PostConstruct
    private void init() throws UnsupportedEncodingException {
        odsAuthorization = "Basic "+Base64.encodeBase64String((this.publicationUsername + ":" + this.publicationPassword).getBytes(StandardCharsets.UTF_8));
    }

    public <E> String getResourceFilePath(Class<E> clazz) throws IllegalAccessException, InstantiationException {

        E testInstance =  clazz.newInstance();
        String $filename = StringUtils.EMPTY;

        if (testInstance instanceof Project) {
            $filename = projectsFilename;
        }
        else if (testInstance instanceof Person) {
            $filename = personsFilename;
        }
        else if (testInstance instanceof Publication) {
            $filename = publicationsFilename;
        }
        else if (testInstance instanceof Structure) {
            $filename = structuresFilename;
        }

        return menesrResourcesDirectory + "/" + $filename;
    }

    private HttpGet get(String url, String authorization) {
        HttpGet get = new HttpGet(url);
        get.setHeader(HttpHeaders.AUTHORIZATION, authorization);
        return get;
    }

    public HttpGet getPublicationDOIsAction() {
        return get(publicationDataset+"/download?format=json", odsAuthorization);
    }
}
