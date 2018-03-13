/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.solr;

import ish.oncourse.configuration.Configuration;
import ish.oncourse.solr.query.SearchParams;
import ish.oncourse.solr.query.SolrQueryBuilder;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;

import java.io.IOException;

/**
 * User: akoiro
 * Date: 6/3/18
 */
public class BuildSolrClientTest {
	public static void main(String[] args) throws IOException, SolrServerException {
		SolrClient client = BuildSolrClient.instance(Configuration.loadProperties()).build();
		QueryResponse response = client.query("courses", SolrQueryBuilder.valueOf(new SearchParams(), "299", 0, 10).build());
		System.out.println(response.getResults());
	}
}
