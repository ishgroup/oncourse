package ish.oncourse.solr;

import org.apache.solr.SolrTestCaseJ4;

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
        return InitSolr.valueOf("conf/solrconfig.xml",
                "conf/schema.xml",
                "src/main/resources/solr",
                "courses");
    }

    public static InitSolr suburbsCore() {
        return InitSolr.valueOf("conf/solrconfig.xml",
                "conf/schema.xml",
                "src/main/resources/solr",
                "suburbs");
    }

    public static InitSolr tagsCore() {
        return InitSolr.valueOf("conf/solrconfig.xml",
                "conf/schema.xml",
                "src/main/resources/solr",
                "tags");
    }
}
