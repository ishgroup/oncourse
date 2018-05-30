package ish.oncourse.solr;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.util.AbstractSolrTestCase;
import org.apache.solr.util.TestHarness;
import org.junit.After;

import java.io.File;
import java.io.IOException;

public abstract class CustomizedAbstractSolrTestCase extends AbstractSolrTestCase {
	private static final Logger logger = LogManager.getLogger();

	protected static final String SCHEMA_FILE_PATH = "/conf/schema.xml";
	protected static final String SOLRCONFIG_FILE_PATH = "/conf/solrconfig.xml";
	protected static final String SOLR_RESOURCES_PATH = "src/main/resources/solr/";
	protected static final String TEST_SOLR_HOME = "src/test/resources/";
	protected static final String ID_FIELD_NAME = "id";
	protected static final String COLLEGE_ID_FIELD_NAME = "collegeId";
	protected static final String NAME_FIELD_NAME = "name";
	protected static final String DOCTYPE_FIELD_NAME = "doctype";
	protected static final String TEST_LOCATION_3 = "-40.94777400,110.82577800";
	protected static final String TEST_LOCATION_2 = "-30.94777400,110.82577800";
	protected static final String TEST_LOCATION_1 = "-31.94777400,115.82577800";
	
	protected static String factoryProp;
	protected SolrClient server;
	
	@After
    public void destroy() {
		try {
			clearIndex();
			server.close();
		} catch (IOException e) {
			logger.error(e);
		}
	}
	
	protected void prepareCore(final String coreName) throws Exception {
        testSolrHome = new File(TEST_SOLR_HOME).toPath();
        System.setProperty("SOLR_DATA", TEST_SOLR_HOME);
		System.setProperty("solr.master.url", "http://localhost:8081/search-internal");
		System.setProperty("solr.master.enable", "true");
		System.setProperty("solr.slave.enable", "false");
		System.setProperty("solr.core.name", coreName);
		System.setProperty("solr.poll", "1");
		configString = getConfigPath(coreName);
		schemaString = getSchemaPath(coreName);
        customInitCore(coreName);
        server = new EmbeddedSolrServer(h.getCoreContainer(), h.getCore().getName());
	}
	
	protected String getConfigPath(final String coreName) {
    	return String.format("%s%s%s", SOLR_RESOURCES_PATH, coreName, SOLRCONFIG_FILE_PATH);
    }
    
    protected String getSchemaPath(final String coreName) {
    	return String.format("%s%s%s", SOLR_RESOURCES_PATH, coreName, SCHEMA_FILE_PATH);
    }
	
	protected static void customInitCore(final String coreName) throws Exception {
		logger.info("####initCore");
    	ignoreException("ignore_exception");
    	factoryProp = System.getProperty("solr.directoryFactory");
    	if (factoryProp == null) {
    		System.setProperty("solr.directoryFactory", "solr.RAMDirectoryFactory");
    	}
    	// other  methods like starting a jetty instance need these too
    	System.setProperty("solr.test.sys.prop1", "propone");
    	System.setProperty("solr.test.sys.prop2", "proptwo");
    	String configFile = getSolrConfigFile();
    	if (configFile != null) {
    		customCreateCore(coreName);
    	}
		logger.info("####initCore end");
    }
	
	protected static void customCreateCore(final String coreName) {
    	assertNotNull(testSolrHome);
    	SolrTestCaseJ4.solrConfig = TestHarness.createConfig(testSolrHome, coreName, getSolrConfigFile());
		h = new TestHarness(coreName, SolrTestCaseJ4.solrConfig.getDataDir(), SolrTestCaseJ4.solrConfig, getSchemaFile());
    	lrf = h.getRequestFactory("standard", 0, 20, CommonParams.VERSION, "2.2");
    }
}
