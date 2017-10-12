/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.portal.services.application;

import ish.oncourse.services.search.SearchService;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;

import static ish.oncourse.configuration.Configuration.AppProperty.ZK_HOST;

public class PortalSearchService extends SearchService {

    protected SolrClient getSolrClient(SolrCore core) {
        SolrClient solrClient = null;
        if (solrClients.keySet().contains(core)) {
            solrClient = solrClients.get(core);
        }
        if (solrClient == null) {
            try {
                String solrURL = System.getProperty(ZK_HOST.getSystemProperty());
                if (solrURL == null) {
                    throw new IllegalStateException("Undefined property: " + ZK_HOST.getSystemProperty());
                }
                solrClient = new CloudSolrClient(solrURL);
                solrClients.put(core, solrClient);
            } catch (Exception e) {
                throw new RuntimeException("Unable to connect to solr server.", e);
            }

        }
        return solrClient;
    }
}
