/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.solr;

import com.google.gson.Gson;
import ish.oncourse.model.College;
import ish.oncourse.test.ContextUtils;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.common.params.MapSolrParams;
import org.apache.solr.handler.dataimport.DataImportHandler;
import org.apache.solr.handler.dataimport.DataImporter;
import org.apache.solr.request.LocalSolrQueryRequest;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public abstract class AbstractSolrTest extends SolrTestCaseJ4 {
    protected  ModelBuilder modelBuilder;

    @BeforeClass
    public static void beforeClass() throws Exception {
        SearchContextUtils.setupDataSources();
        initSolr("src/main/resources/solr", "courses");
    }


    @Before
    public void setup() throws Exception {
        InputStream st = CoursesSolrTest.class.getClassLoader().getResourceAsStream(getDataSetResource());
        FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);

        DataSource dataSource = ContextUtils.getDataSource("jdbc/oncourse");
        DatabaseConnection dbConnection = new DatabaseConnection(dataSource.getConnection(), null);
        dbConnection.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MySqlDataTypeFactory());
        dbConnection.getConfig().setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, false);

        ReplacementDataSet rDataSet = initReplacement(dataSet);

        SearchContextUtils.truncateAllTables(false);
        DatabaseOperation.CLEAN_INSERT.execute(dbConnection, rDataSet);


        ObjectContext context = SearchContextUtils.createObjectContext();
        College college = Cayenne.objectForPK(context, College.class, 10);
        modelBuilder = ModelBuilder.valueOf(context, college);


    }

    protected String getDataSetResource() {
        return getClass().getName() + ".xml";
    }

    protected ReplacementDataSet initReplacement(FlatXmlDataSet dataSet) {
        ReplacementDataSet rDataSet = new ReplacementDataSet(dataSet);
        rDataSet.addReplacementObject("[START_DATE]", DateUtils.addDays(new Date(), 1));
        rDataSet.addReplacementObject("[END_DATE]", DateUtils.addDays(new Date(), 2));
        return rDataSet;
    }


    public static void initSolr(String solrHome, String core) throws Exception {
        String config = "conf/solrconfig.xml";
        String schema = "conf/schema.xml";

        initSolr(config, schema, solrHome, core);
    }

    public static void initSolr(String config, String schema, String solrHome, String core) throws Exception {
        System.setProperty("SOLR_DATA", "");
        System.setProperty("solr.master.url", "http://localhost:8081/search-internal");
        System.setProperty("solr.core.name", core);
        System.setProperty("solr.master.enable", "true");
        System.setProperty("solr.slave.enable", "false");
        System.setProperty("solr.poll", "1");
        System.setProperty("solr.allow.unsafe.resourceloading", "true");
        initCore(config, schema, solrHome, core);
    }

    @AfterClass
    public static void afterClass() throws Exception {
        SearchContextUtils.shutdownDataSources();
        AbstractSolrTest.initCoreDataDir.deleteOnExit();
    }


    public Map fullImport() throws Exception {
        return solrImport("full-import");
    }


    public Map deltaImport() throws Exception {
        return solrImport("delta-import");
    }

    private Map solrImport(String command) throws Exception {
        Thread.sleep(2000);
        Gson gson = new Gson();

        Map<String, String> params = new HashMap<>();
        params.put("command", command);
        params.put("wt", "json");
        params.put("synchronous", Boolean.TRUE.toString());

        Map response = gson.fromJson(h.query("/dataimport", new LocalSolrQueryRequest(h.getCore(), new MapSolrParams(params))), Map.class);

        DataImporter dataImporter = ((DataImportHandler) h.getCore().getRequestHandlers().get("/dataimport")).getImporter();
        assertFalse(dataImporter.isBusy());
        Thread.sleep(2000);
        return response;
    }

    public void assertDeltaImportEmpty() throws Exception {
        Map response = deltaImport();
        assertEquals("0", ((Map) response.get("statusMessages")).get("Total Documents Processed"));
    }

    public void assertDeltaImport() throws Exception {
        Map response = deltaImport();
        assertEquals("1", ((Map) response.get("statusMessages")).get("Total Documents Processed"));

        assertDeltaImportEmpty();
    }
}
