/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.solr;

import org.apache.solr.SolrTestCaseJ4;
import org.junit.AfterClass;
import org.junit.BeforeClass;


public class SolrTest extends SolrTestCaseJ4 {
	
	@BeforeClass
	public static void beforeClass() throws Exception {
		SearchContextUtils.setupDataSources();
	}
	
	public static void initSolr(String solrHome, String core) throws Exception {
		String config = String.format("%s/%s/conf/solrconfig.xml", solrHome, core);
		String schema = String.format("%s/%s/conf/schema.xml", solrHome, core);
		
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
	}
}
