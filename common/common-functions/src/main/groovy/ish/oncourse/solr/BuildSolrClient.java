/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.solr;

import ish.oncourse.configuration.Configuration;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.LBHttpSolrClient;

import java.util.Properties;

/**
 * User: akoiro
 * Date: 5/12/17
 */
public class BuildSolrClient {
	private static final int ZK_TIMEOUT = 3000;

	private String url;

	private BuildSolrClient() {
	}

	public BuildSolrClient url(String url) {
		this.url = url;
		return this;
	}

	public SolrClient build() {
		if (url.startsWith("http://") || url.startsWith("https://")) {
			return new LBHttpSolrClient.Builder().withBaseSolrUrls(url.split(",")).build();
		} else {
			CloudSolrClient solrClient = new CloudSolrClient.Builder().withZkHost(url).build();
			solrClient.setZkClientTimeout(ZK_TIMEOUT);
			solrClient.setZkConnectTimeout(ZK_TIMEOUT);
			return solrClient;
		}
	}

	public static BuildSolrClient instance(String url) {
		BuildSolrClient result = new BuildSolrClient();
		result.url = url;
		return result;
	}

	public static BuildSolrClient instance(Properties properties) {
		BuildSolrClient result = new BuildSolrClient();
		//TEMPORARY SOLUTION FOR OD-11650. WE NEED IT TO EXCLUDE ZK FROM THE SOLR REQUEST CHAIN
		//WE CANNOT USE ZK_HOST PROPERTY BECAUSE OUR Configuration IMPLEMENTATION TRIES TO USE
		//THIS PROPERTY TO INITIALIZE ZK NODES.
		result.url = (String) properties.get("solr_url");

		if (result.url == null) {
			result.url = (String) properties.get(Configuration.AppProperty.ZK_HOST.getKey());
		}
		if (result.url == null) {
			throw new IllegalStateException("Undefined property: sorl/zk url");
		}
		return result;
	}
}
