package ish.oncourse.solr;

import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.core.CoreContainer;
import org.apache.solr.core.CoreDescriptor;
import org.apache.solr.core.SolrConfig;
import org.apache.solr.core.SolrCore;
import org.apache.solr.core.SolrResourceLoader;
import org.apache.solr.logging.ListenerConfig;
import org.apache.solr.logging.LogWatcher;
import org.apache.solr.logging.jul.JulWatcher;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.util.AbstractSolrTestCase;
import org.apache.solr.util.TestHarness;
import org.junit.After;

public abstract class CustomizedAbstractSolrTestCase extends AbstractSolrTestCase {
	protected static final String SCHEMA_FILE_PATH = "/conf/schema.xml";
	protected static final String SOLRCONFIG_FILE_PATH = "/conf/solrconfig.xml";
	protected static final String SOLR_RESOURCES_PATH = "src/main/resources/solr/";
	protected static final String TEST_SOLR_HOME = "src/test/resources/";
	
	protected static String factoryProp;
	protected SolrServer server;
	
	@After
    public void destroy() {
    	clearIndex();
    	server.shutdown();
    }
	
	protected void prepareCore(final String coreName) throws Exception {
        testSolrHome = TEST_SOLR_HOME;
        System.setProperty("SOLR_DATA", testSolrHome);
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
    	log.info("####initCore");
    	ignoreException("ignore_exception");
    	factoryProp = System.getProperty("solr.directoryFactory");
    	if (factoryProp == null) {
    		System.setProperty("solr.directoryFactory","solr.RAMDirectoryFactory");
    	}
    	if (dataDir == null) {
    		createTempDir();
    	}
    	// other  methods like starting a jetty instance need these too
    	System.setProperty("solr.test.sys.prop1", "propone");
    	System.setProperty("solr.test.sys.prop2", "proptwo");
    	String configFile = getSolrConfigFile();
    	if (configFile != null) {
    		customCreateCore(coreName);
    	}
    	log.info("####initCore end");
    }
	
	protected static void customCreateCore(final String coreName) {
    	assertNotNull(testSolrHome);
    	SolrTestCaseJ4.solrConfig = TestHarness.createConfig(testSolrHome, coreName, getSolrConfigFile());
    	h = new TestHarness(coreName, new CustomInitializer(coreName, dataDir.getAbsolutePath(), SolrTestCaseJ4.solrConfig, 
    		new IndexSchema(SolrTestCaseJ4.solrConfig, getSchemaFile(), null)));
    	lrf = h.getRequestFactory("standard", 0, 20, CommonParams.VERSION, "2.2");
    }
	
	protected static class CustomInitializer extends CoreContainer.Initializer {
    	private String coreName;
    	private String dataDirectory;
    	private SolrConfig solrConfig;
    	private IndexSchema indexSchema;
    	
		protected CustomInitializer(String coreName, String dataDirectory, SolrConfig solrConfig, IndexSchema indexSchema) {
			if (coreName == null) {
				throw new IllegalArgumentException("No core name defined");
			}
			this.coreName = coreName;
			this.dataDirectory = dataDirectory;
			this.solrConfig = solrConfig;
			this.indexSchema = indexSchema;
		}
    	
		protected String getCoreName() {
			return coreName;
		}
		
		@Override
		public CoreContainer initialize() {
			CoreContainer container = new CoreContainer(new SolrResourceLoader(SolrResourceLoader.locateSolrHome())) {
				{hostPort = System.getProperty("hostPort");
				hostContext = "solr";
				defaultCoreName = getCoreName();//CoreContainer.DEFAULT_DEFAULT_CORE_NAME;
				initShardHandler(null);
				initZooKeeper(System.getProperty("zkHost"), 10000);
				}
			};
			LogWatcher<?> logging = new JulWatcher("test");
			logging.registerListener(new ListenerConfig(), container);
			container.setLogging(logging);
	      
			CoreDescriptor dcore = new CoreDescriptor(container, getCoreName(), solrConfig.getResourceLoader().getInstanceDir());
			dcore.setConfigName(solrConfig.getResourceName());
			dcore.setSchemaName(indexSchema.getResourceName());
			SolrCore core = new SolrCore(getCoreName(), dataDirectory, solrConfig, indexSchema, dcore);
			container.register(getCoreName(), core, false);

			// TODO: we should be exercising the *same* core container initialization code, not equivalent code!
			if (container.getZkController() == null && core.getUpdateHandler().getUpdateLog() != null) {
				// always kick off recovery if we are in standalone mode.
				core.getUpdateHandler().getUpdateLog().recoverFromLog();
			}
			return container;
		}
    }
}
