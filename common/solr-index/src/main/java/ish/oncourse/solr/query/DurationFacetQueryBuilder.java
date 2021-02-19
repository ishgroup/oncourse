package ish.oncourse.solr.query;

import org.apache.solr.client.solrj.SolrQuery;


public class DurationFacetQueryBuilder {
    
    private String collegeId;
    private SearchParams searchParams;

    private Duration duration;
    
    public SolrQuery build() {
        SolrQuery solrQuery = new SolrQueryBuilder()
                .searchParams(searchParams)
                .collegeId(collegeId).build();
        solrQuery.setFacet(true);
        String query = SolrQueryBuilder.getDurationQuery(duration);
        solrQuery.addFacetQuery(query);
        return solrQuery;
    }

    public static DurationFacetQueryBuilder valueOf(Duration duration, SearchParams searchParams, Long collegeId) {
        DurationFacetQueryBuilder result = new DurationFacetQueryBuilder();
        result.duration = duration;
        result.searchParams = SearchParams.valueOf(searchParams, true);
        result.collegeId = String.valueOf(collegeId);
        return result;
    }

}
