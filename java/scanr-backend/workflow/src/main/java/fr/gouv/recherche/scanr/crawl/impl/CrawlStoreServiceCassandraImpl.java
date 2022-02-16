/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
 
package fr.gouv.recherche.scanr.crawl.impl;

import fr.gouv.recherche.scanr.config.CassandraConfiguration;
import fr.gouv.recherche.scanr.crawl.CrawlData;
import fr.gouv.recherche.scanr.crawl.CrawlInfo;
import fr.gouv.recherche.scanr.crawl.CrawlStoreService;
import fr.gouv.recherche.scanr.crawl.CrawlText;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.KeyspaceMetadata;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.google.common.base.Charsets;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Main service for indexation. This service is manually registred in order to set it enabled (for prod) or not (for
 * tests) Not declared as a service to desactivate for tests
 */
public class CrawlStoreServiceCassandraImpl implements CrawlStoreService {

    private static final Logger log = LoggerFactory.getLogger(CrawlStoreServiceCassandraImpl.class);
    public static final String CRAWL_STORE_TYPE_INFO = "crawl_info";
    public static final String CRAWL_STORE_TYPE_DATA = "crawl_data";
    public static final String CRAWL_STORE_INDEX_INFO_ID = "crawl_info_id";

    @Autowired
    private CassandraConfiguration cassandraConfiguration;
    @Autowired
    private Cluster cassandraCluster;

    private Session cassandraSession;
    private PreparedStatement selectStarFromData;
    private PreparedStatement selectStarFromInfo;
    private PreparedStatement selectMetaFromData;
    private PreparedStatement selectIdFromInfo;
    private PreparedStatement selectTextFromData;

    @PostConstruct
    public void init() throws IOException {
        if (cassandraCluster != null)
            ensureTablesCreated(false);
    }

    /**
     * Insure that the crawl index is correctly created.
     *
     * @param delete force recreation of the index if true
     * @throws IOException
     */
    public void ensureTablesCreated(boolean delete) throws IOException {
        ensureKeyspaceCreated(cassandraConfiguration.getKeyspace(), delete);
        // put the mappings anyway
        createTable(CRAWL_STORE_TYPE_DATA);
        createTable(CRAWL_STORE_TYPE_INFO);
        createIndex();
        prepareQueries();
    }

    /**
     * Insure that the index is correctly created.
     *
     * @param index  index name
     * @param delete force recreation of the index if true
     * @throws IOException
     */
    private void ensureKeyspaceCreated(String index, boolean delete) throws IOException {
        boolean keyspaceExists = false;
        for (KeyspaceMetadata meta : cassandraCluster.getMetadata().getKeyspaces()) {
            if (meta.getName().equals(cassandraConfiguration.getKeyspace())) {
                keyspaceExists = true;
                break;
            }
        }

        Session localCassandraSession = cassandraCluster.connect();
        if (keyspaceExists && delete) {
            localCassandraSession.execute("DROP KEYSPACE " + cassandraConfiguration.getKeyspace() + ";");
        }
        if (!keyspaceExists || delete) {
            String create = IOUtils.toString(
                    CrawlStoreServiceCassandraImpl.class.getResourceAsStream("/fr/gouv/recherche/scanr/crawl_store/create_keyspace.cql"),
                    Charsets.UTF_8);
            localCassandraSession.execute(create.replace("%s", cassandraConfiguration.getKeyspace()));
        }

        cassandraSession = cassandraCluster.connect(cassandraConfiguration.getKeyspace());
    }

    private void createTable(String table) throws IOException {
        log.info("Create table for keyspace " + cassandraConfiguration.getKeyspace() + " table:" + table);

        String source = IOUtils.toString(
                CrawlStoreServiceCassandraImpl.class.getResourceAsStream("/fr/gouv/recherche/scanr/crawl_store/create_table_" + table + ".cql"),
                Charsets.UTF_8);

        cassandraSession.execute(source);
    }

    private void createIndex() throws IOException {
        cassandraSession.execute("CREATE INDEX IF NOT EXISTS " + CRAWL_STORE_INDEX_INFO_ID + " ON "
                + CRAWL_STORE_TYPE_INFO + " (id)");
    }

    private void prepareQueries() {
        selectStarFromData = cassandraSession.prepare("SELECT * FROM " + CRAWL_STORE_TYPE_DATA + " WHERE crawl_id = ?");
        selectStarFromData.setConsistencyLevel(ConsistencyLevel.ONE);
        selectMetaFromData = cassandraSession.prepare("SELECT crawl_id, url, charset, content_type, crawl_date, depth, domain, headers, http_status, title, relevant_txt, lang FROM " + CRAWL_STORE_TYPE_DATA + " WHERE crawl_id = ?");
        selectMetaFromData.setConsistencyLevel(ConsistencyLevel.ONE);
        selectStarFromInfo = this.cassandraSession.prepare("SELECT * FROM " + CRAWL_STORE_TYPE_INFO + " WHERE url = ?");
        selectStarFromInfo.setConsistencyLevel(ConsistencyLevel.ONE);
        selectIdFromInfo = this.cassandraSession.prepare("SELECT id FROM " + CRAWL_STORE_TYPE_INFO + " WHERE url = ?");
        selectIdFromInfo.setConsistencyLevel(ConsistencyLevel.ONE);
        selectTextFromData = this.cassandraSession.prepare("SELECT title, relevant_txt, lang FROM " + CRAWL_STORE_TYPE_DATA + " WHERE crawl_id = ?");
        selectTextFromData.setConsistencyLevel(ConsistencyLevel.ONE);
    }

    protected static CrawlInfo buildCrawlInfoFromRow(Row row) {
        /*
         * url text, depth int, max_pages int, end_date timestamp, id uuid, page_count int, start_date timestamp, status
         * text, main_lang text
         */
        return new CrawlInfo(row.getString("url"), row.getInt("depth"), row.getInt("max_pages"),
                row.getDate("end_date"), row.getUUID("id").toString(), row.getInt("page_count"),
                row.getDate("start_date"), row.getString("status"),
                row.getMap("histogram", String.class, Integer.class),
                row.getString("main_lang"));
    }

    protected static CrawlData buildCrawlDataFromRow(Row row) {
        boolean hasContent = row.getColumnDefinitions().contains("content");
        return new CrawlData(row.getUUID("crawl_id").toString(), row.getString("url"), row.getString("charset"),
                hasContent ? row.getString("content") : null, row.getString("content_type"),
                row.getDate("crawl_date"), row.getInt("depth"), row.getString("domain"),
                row.getMap("headers", String.class, String.class), row.getInt("http_status"),
                row.getString("title"), row.getString("relevant_txt"), row.getString("lang"));
    }

    protected static CrawlText buildCrawlTextFromRow(Row row) {
        return new CrawlText(row.getString("title"), row.getString("relevant_txt"), row.getString("lang"));
    }

    @Override
    public CrawlInfo searchCrawlInfo(String url) {
        final List<Row> rows = queryCrawlInfo(url, selectStarFromInfo);
        return rows.size() == 0 ? null : buildCrawlInfoFromRow(rows.get(0));
    }

    private List<Row> queryCrawlInfo(String url, PreparedStatement query) {
        log.trace("Querying: " + query.getQueryString() + " / " + url);
        selectStarFromInfo.enableTracing();
        ResultSet results = this.cassandraSession.execute(selectStarFromInfo.bind(url));

        final List<Row> rows = results.all();
        if (rows.size() > 1) {
            // TODO: better exception management
            throw new IllegalStateException("Got " + rows.size() + " crawls for input url [" + url + "]");
        }
        return rows;
    }

    @Override
    public String searchCrawlId(String url) {
        final List<Row> rows = queryCrawlInfo(url, selectStarFromInfo);
        return rows.size() == 0 ? null : rows.get(0).getUUID("id").toString();
    }

    @Override
    public List<CrawlData> getCrawlPages(String crawlID) {
        return getCrawlData(crawlID, selectStarFromData, CrawlStoreServiceCassandraImpl::buildCrawlDataFromRow);
    }

    @Override
    public List<CrawlData> getCrawlPagesMetaData(String crawlID) {
        return getCrawlData(crawlID, selectMetaFromData, CrawlStoreServiceCassandraImpl::buildCrawlDataFromRow);
    }

    @Override
    public List<CrawlText> getCrawlTexts(String crawlID) {
        return getCrawlData(crawlID, selectTextFromData, CrawlStoreServiceCassandraImpl::buildCrawlTextFromRow);
    }

    private <T> List<T> getCrawlData(String crawlID, PreparedStatement query, Function<Row, T> mapping) {
        ResultSet results = this.cassandraSession.execute(query.bind(UUID.fromString(crawlID)));

        return results.all().stream().map(mapping::apply).collect(Collectors.toList());
    }

}
