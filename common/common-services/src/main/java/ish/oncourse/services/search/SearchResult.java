package ish.oncourse.services.search;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;

import java.net.URLDecoder;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class SearchResult {
    private static final Logger logger = LogManager.getLogger();

    private SolrQuery solrQuery;
    private QueryResponse queryResponse;


    public SolrQuery getSolrQuery() {
        return solrQuery;
    }

    public QueryResponse getQueryResponse() {
        return queryResponse;
    }


    public String getSolrQueryAsString() {
        try {
            return URLDecoder.decode(solrQuery.toString(), "UTF-8");
        } catch (Exception e) {
            logger.error(e);
            return solrQuery.toString();
        }
    }


    public static SearchResult valueOf(SolrQuery solrQuery, QueryResponse queryResponse) {
        SearchResult result = new SearchResult();
        result.solrQuery = solrQuery;
        result.queryResponse = queryResponse;
        return result;
    }

    public static SearchResult valueOf(SolrQuery solrQuery) {
        return SearchResult.valueOf(solrQuery, null);
    }

}
