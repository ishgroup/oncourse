package ish.oncourse.solr.query;

import ish.oncourse.model.College;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class LocationFacetQueryBuilder {

    private static final Logger logger = LogManager.getLogger();

    private String collegeId;
    private SearchParams searchParams;

    private List<Count> counts;
    private List<Suburb> suburbs;

    private Map<String, Count> facetQueries = new HashMap<>();
    private SolrQuery solrQuery;

    public LocationFacetQueryBuilder build() {
        solrQuery = new SolrQueryBuilder()
                .searchParams(searchParams)
                .collegeId(collegeId).build();
        solrQuery.setFacet(true);

        facetQueries = new HashMap<>();
        for (int i = 0; i < suburbs.size(); i++) {
            Suburb suburb = suburbs.get(i);
            Count count = counts.get(i);
            if (suburb != null) {
                String query = SolrQueryBuilder.getSuburbQuery(suburb);
                facetQueries.put(query, count);
                solrQuery.addFacetQuery(query);
            } else {
                logger.debug(String.format("Cannot find suburb %s", count.getPath()));
            }
        }
        return this;
    }

    public Map<String, Count> getFacetQueries() {
        return facetQueries;
    }

    public SolrQuery getSolrQuery() {
        return solrQuery;
    }

    public static LocationFacetQueryBuilder valueOf(List<Suburb> suburbs, List<Count> counts, SearchParams searchParams, College college) {
        return valueOf(suburbs, counts, searchParams, college.getId());
    }

    public static LocationFacetQueryBuilder valueOf(List<Suburb> suburbs, List<Count> counts, SearchParams searchParams, Long collegeId) {
        LocationFacetQueryBuilder result = new LocationFacetQueryBuilder();
        result.suburbs = new ArrayList<>(suburbs);
        result.counts = new ArrayList<>(counts);
        result.searchParams = SearchParams.valueOf(searchParams, true);
        result.collegeId = String.valueOf(collegeId);
        return result;
    }

}
