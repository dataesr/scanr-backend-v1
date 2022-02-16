/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.config.elasticsearch;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;

import com.sword.utils.elasticsearch.config.CertificateAuthoritiesTypeEnum;
import com.sword.utils.elasticsearch.config.EsClusterConfig;
import com.sword.utils.elasticsearch.config.IClusterKey;
import com.sword.utils.elasticsearch.config.IIndex;
import com.sword.utils.elasticsearch.contexts.ConfigurableEsContext;
import com.sword.utils.elasticsearch.contexts.DefaultEsContext;
import com.sword.utils.elasticsearch.exceptions.EsRequestException;
import com.sword.utils.elasticsearch.services.EsServiceAdmin;
import com.sword.utils.elasticsearch.services.EsServiceDedicated;
import com.sword.utils.elasticsearch.services.EsServiceGeneral;

import fr.gouv.recherche.scanr.api.exception.ElasticException;
import fr.gouv.recherche.scanr.db.model.full.FullPerson;
import fr.gouv.recherche.scanr.db.model.full.FullProject;
import fr.gouv.recherche.scanr.db.model.full.FullPublication;
import fr.gouv.recherche.scanr.db.model.full.FullStructure;

public class EsClient {

    protected IClusterKey clusterKey;
    protected String clusterName;
    protected String host;
    protected int port;
    protected String user;
    protected String password;
    protected String caPath;

    protected RestHighLevelClient esRestClient;
    protected EsServiceAdmin adminService;
    protected EsServiceGeneral serviceGeneral;
    protected EsServiceDedicated<FullStructure> serviceStructure;
    protected EsServiceDedicated<FullProject> serviceProject;
    protected EsServiceDedicated<FullPerson> servicePerson;
    protected EsServiceDedicated<FullPublication> servicePublication;

    public static final String KEYWORD_SUFFIXE = ".keyword";
    public static final String SORT_SUFFIXE = ".sort";

    public EsClient(IClusterKey clusterKey, String clusterName, String host, int port, String user, String password, String caPath) throws ElasticException {
        this.clusterKey = clusterKey;
        this.clusterName = clusterName;
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        this.caPath = caPath;
        
        ConfigurableEsContext context;
        EsClusterConfig config = new EsClusterConfig(clusterKey, clusterName, port, false, user, password, host);
        config.useTls(caPath, CertificateAuthoritiesTypeEnum.PEM, null);
        context = DefaultEsContext.instance();
        context.addCluster(config);
        context.setMapperConfigurator(EsMapperConfigurator.getInstance());
        EsMapperConfigurator.getInstance().configureMapper(context.getObjectMapper());
        
        esRestClient = context.getRestClient(clusterKey);
        try {
            // Pour éviter de se retrouver avec des erreurs sur les shards si ES a été démarré en même temps et n'a pas fini son init
            esRestClient.cluster().health(new ClusterHealthRequest().waitForGreenStatus().timeout(TimeValue.timeValueSeconds(60)), RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new ElasticException("Le cluster ES n'est pas GREEN", e);
        }

        serviceGeneral = new EsServiceGeneral(context, clusterKey);
        adminService = new EsServiceAdmin(context, clusterKey);

        initIndices();
    }

    protected synchronized void initServiceStructure() {
        if (serviceStructure == null) {
            serviceStructure = new EsServiceDedicated<>(DefaultEsContext.instance(), clusterKey, EsIndexEnum.STRUCTURE, FullStructure.class);
        }
    }

    protected synchronized void initServiceProject() {
        if (serviceProject == null) {
            serviceProject = new EsServiceDedicated<>(DefaultEsContext.instance(), clusterKey, EsIndexEnum.PROJECT, FullProject.class);
        }
    }

    protected synchronized void initServicePerson() {
        if (servicePerson == null) {
            servicePerson = new EsServiceDedicated<>(DefaultEsContext.instance(), clusterKey, EsIndexEnum.PERSON, FullPerson.class);
        }
    }

    protected synchronized void initServicePublication() {
        if (servicePublication == null) {
            servicePublication = new EsServiceDedicated<>(DefaultEsContext.instance(), clusterKey, EsIndexEnum.PUBLICATION, FullPublication.class);
        }
    }

    public EsServiceGeneral getServiceGeneral() {
        return serviceGeneral;
    }

    public RestHighLevelClient getEsRestClient() {
        return this.esRestClient;
    }

    public EsServiceDedicated<FullStructure> getServiceStructure() {
    	if (serviceStructure == null) {
    		initServiceStructure();
    	}
    	
        return serviceStructure;
    }

    public EsServiceDedicated<FullProject> getServiceProject() {
    	if (serviceProject == null) {
    		initServiceProject();
    	}
    	
        return serviceProject;
    }

    public EsServiceDedicated<FullPerson> getServicePerson() {
    	if (servicePerson == null) {
    		initServicePerson();
    	}
    	
        return servicePerson;
    }

    public EsServiceDedicated<FullPublication> getServicePublication() {
    	if (servicePublication == null) {
    		initServicePublication();
    	} 
    	
        return servicePublication;
    }

    protected void initIndices() throws ElasticException {
        // Fichier avec les paramètres pour les indexes
        InputStream settingsDefaultInputStream;
        String settingsDefault;

        try {
            settingsDefaultInputStream = getClass().getResourceAsStream(EsFilePath.INDEX_SETTINGS_DEFAULT.getFileName());
            settingsDefault = IOUtils.toString(settingsDefaultInputStream, Charsets.UTF_8);
        } catch (IOException ex) {
            throw new ElasticsearchException("Impossible de lire le fichier de paramétrage des propriétés de l'index Elasticsearch", ex);
        }

        try {
            // Création des indexes
            createIndexIfNotExist(EsIndexEnum.CREATE_NEW_INDEX_STRUCTURE, EsIndexEnum.STRUCTURE, EsFilePath.MAPPING_STRUCTURE, settingsDefault);
            createIndexIfNotExist(EsIndexEnum.CREATE_NEW_INDEX_PUBLICATION, EsIndexEnum.PUBLICATION, EsFilePath.MAPPING_PUBLICATION, settingsDefault);
            createIndexIfNotExist(EsIndexEnum.CREATE_NEW_INDEX_PERSON, EsIndexEnum.PERSON, EsFilePath.MAPPING_PERSONS, settingsDefault);
            createIndexIfNotExist(EsIndexEnum.CREATE_NEW_INDEX_PROJECT, EsIndexEnum.PROJECT, EsFilePath.MAPPING_PROJECT, settingsDefault);
        } catch (IOException | EsRequestException ex) {
            throw new ElasticException("Imposible de lire un ou plusieurs fichiers de définition des mappings Elasticsearch", ex);
        }
    }

    protected void createIndexIfNotExist(EsIndexEnum index, EsIndexEnum alias, EsFilePath mapping, String settings) throws EsRequestException, IOException {
        if (!adminService.indexExists(alias)) {
            // On crée le nouvel index + l'alias staging
            adminService.createIndexWithSetting(index, settings);
            adminService.addIndexAlias(index, alias.getName());

            putMapping(index, mapping);
        }
    }

    protected void createIndexIfNotExist(EsIndexEnum index, String settings) throws EsRequestException {
        if (!adminService.indexExists(index)) {
            adminService.createIndexWithSetting(index, settings);
        }
    }
    
    protected void putMapping(IIndex index, EsFilePath filePath) throws EsRequestException, IOException {
        InputStream mappingInputStream = getClass().getResourceAsStream(filePath.getFileName());
        String mapping = IOUtils.toString(mappingInputStream, StandardCharsets.UTF_8.name());

        adminService.putMapping(index, mapping);
    }

//    public void index(Object objToIndex, IIndex index, String id, boolean refresh) throws ElasticException {
//        String json;
//
//        try {
//            json = EsMapperManager.getMapper().writeValueAsString(objToIndex);
//        } catch (JsonProcessingException ex) {
//            throw new ElasticException("Erreur lors du parsing de l'objet JSON", ex);
//        }
//
//        IndexRequestBuilder index = esRestClient
//                .prepareIndex(indexEnum.getIndex().getName(), indexEnum.getType())
//                .setSource(json, XContentType.JSON);
//
//        if (StringUtils.isNotBlank(id)) {
//            index = index.setId(id);
//        }
//
//        if (refresh) {
//            index.setRefreshPolicy(RefreshPolicy.WAIT_UNTIL);
//        } else {
//            index.setRefreshPolicy(RefreshPolicy.NONE);
//        }
//
//        index.execute().actionGet(30, TimeUnit.SECONDS);
//    }
//
//    public void index(Object objToIndex, IIndexType indexEnum, boolean refresh) throws ElasticException {
//        if (objToIndex instanceof IIdentifiable) {
//            IIdentifiable obj = (IIdentifiable) objToIndex;
//            index(objToIndex, indexEnum, obj.getId(), refresh);
//        } else {
//            index(objToIndex, indexEnum, StringUtils.EMPTY, refresh);
//        }
//    }
//
//    public void indexBulk(List<Object> objs, IIndexType indexEnum) throws ElasticException {
//        BulkRequestBuilder bulk = esTransportClient.prepareBulk();
//
//        for (Object obj : objs) {
//            String id = null;
//            if (obj instanceof IIdentifiable) {
//                id = ((IIdentifiable) obj).getId();
//            }
//
//            String json;
//            try {
//                json = EsMapperManager.getMapper().writeValueAsString(obj);
//            } catch (JsonProcessingException ex) {
//                throw new ElasticException("Erreur lors du parsing de l'objet JSON", ex);
//            }
//
//            IndexRequestBuilder index = esTransportClient
//                    .prepareIndex(indexEnum.getIndex().getName(), indexEnum.getType())
//                    .setSource(json, XContentType.JSON);
//
//            if (StringUtils.isNotBlank(id)) {
//                index = index.setId(id);
//            }
//
//            bulk.add(index);
//        }
//
//        if (bulk.numberOfActions() > 0) {
//            BulkResponse bulkResponse = bulk.execute().actionGet(30, TimeUnit.SECONDS);
//
//            if (bulkResponse.hasFailures()) {
//                throw new ElasticsearchException("Erreur lors de l'indexation bulk : " + bulkResponse.buildFailureMessage());
//            }
//        }
//    }
}
