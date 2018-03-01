/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.search;

import ish.oncourse.configuration.Configuration;
import ish.oncourse.services.property.Property;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;

import static ish.oncourse.configuration.Configuration.AppProperty.ZK_HOST;

/**
 * User: akoiro
 * Date: 5/12/17
 */
public class BuildSolrClient {
	private String url;

	public BuildSolrClient url(String url) {
		this.url = url;
		return this;
	}

	public SolrClient build() {
		if (url == null) {
			throw new IllegalStateException("Undefined property: " + Property.SolrServer);
		}
		if (url.startsWith("http://") || url.startsWith("https://")) {
			return new HttpSolrClient.Builder(url).build();
		} else {
			CloudSolrClient solrClient = new CloudSolrClient.Builder().withZkHost(url).build();
			solrClient.setZkClientTimeout(3000);
			solrClient.setZkConnectTimeout(1000);
			return solrClient;
		}
	}

	public static BuildSolrClient instance(String url) {
		BuildSolrClient result = new BuildSolrClient();
		result.url = url;
		return result;
	}

	public static BuildSolrClient instance() {
		BuildSolrClient result = new BuildSolrClient();
		result.url = Configuration.getValue(ZK_HOST);
		return result;
	}
}
