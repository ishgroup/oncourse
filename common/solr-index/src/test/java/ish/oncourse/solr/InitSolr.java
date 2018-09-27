package ish.oncourse.solr;

import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.schema.PointField;

import java.io.File;

public class InitSolr {

    private String url = "http://localhost:8081/search-internal";
    private String config;
    private String schema;
    private String solrHome;
    private String core;


    public void init() throws Exception {
        System.setProperty("SOLR_DATA", "");
        System.setProperty("solr.master.url", url);
        System.setProperty("solr.core.name", core);
        System.setProperty("solr.master.enable", "true");
        System.setProperty("solr.slave.enable", "false");
        System.setProperty("solr.poll", "1");
        System.setProperty("solr.allow.unsafe.resourceloading", "true");
        System.setProperty("test.solr.allowed.securerandom", "NativePRNG");

        SolrTestCaseJ4.initCore(config, schema, solrHome, core);
    }

    public String getUrl() {
        return url;
    }

    public static InitSolr valueOf(String config, String schema, String solrHome, String core) {
        InitSolr result = new InitSolr();
        result.config = config;
        result.schema = schema;
        result.solrHome = solrHome;
        result.core = core;
        return result;
    }

    public static InitSolr coursesCore() {
        File solr = new File(InitSolr.class.getClassLoader().getResource("solr").getFile());

        return InitSolr.valueOf("conf/solrconfig.xml",
                "conf/schema.xml",
                solr.getAbsolutePath(),
                "courses");
    }

    public static InitSolr suburbsCore() {
        File solr = new File(InitSolr.class.getClassLoader().getResource("solr").getFile());
        
        return InitSolr.valueOf("conf/solrconfig.xml",
                "conf/schema.xml",
                solr.getAbsolutePath(),
                "suburbs");
    }

    public static InitSolr tagsCore() {
        File solr = new File(InitSolr.class.getClassLoader().getResource("solr").getFile());
        
        return InitSolr.valueOf("conf/solrconfig.xml",
                "conf/schema.xml",
                solr.getAbsolutePath(),
                "tags");
    }

    public static InitSolr classesCore() {
        File solr = new File(InitSolr.class.getClassLoader().getResource("solr").getFile());

        return InitSolr.valueOf("conf/solrconfig.xml",
                "conf/schema.xml",
                solr.getAbsolutePath(),
                "classes");
    }

    /**
     * This init static block should be add  to any junit which use this InitSolr implementation
     */
    public static void INIT_STATIC_BLOCK() {
            System.setProperty("test.solr.allowed.securerandom", "NativePRNG");
            System.setProperty("tests.timezone", "Australia/Sydney");
            System.setProperty("tests.locale", "en-AU");
            PointField.TEST_HACK_IGNORE_USELESS_TRIEFIELD_ARGS = false;
    }
}
