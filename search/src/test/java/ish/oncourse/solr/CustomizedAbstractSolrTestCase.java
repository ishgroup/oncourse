package ish.oncourse.solr;

import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.client.solrj.SolrServer;
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

public abstract class CustomizedAbstractSolrTestCase extends AbstractSolrTestCase {
	protected static String factoryProp;
	protected SolrServer server;
	
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
