package ish.oncourse.solr;

import com.google.gson.Gson;
import ish.oncourse.solr.query.*;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.request.LocalSolrQueryRequest;
import org.junit.Test;

import java.util.Collections;
import java.util.Map;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class FacetTest extends AbstractSolrTest {


    @Override
    protected String getDataSetResource() {
        return "ish/oncourse/solr/FacetTest.xml";
    }


    @Test
    public void test() throws Exception {
        assertEquals(2, fullImport());


        Suburb s1 = Suburb.valueOf("2000", -33.88232600, 151.20351100, 5d);
        Suburb s2 = Suburb.valueOf("3051", -37.80416850, 144.95419660, 5d);
        Suburb s3 = Suburb.valueOf("4006",  -27.45962950, 153.02985820, 5d);

        assertEquals(1.0, ((Map) searchBySuburb(s1).get("response")).get("numFound"));

        assertEquals(1.0, ((Map) searchBySuburb(s2).get("response")).get("numFound"));

        assertEquals(2.0, ((Map) searchBySuburb(s1, s2, s3).get("response")).get("numFound"));

        assertEquals(2.0, ((Map) searchBySuburb().get("response")).get("numFound"));

        testFacetQueries(s1, s1, 1);
        testFacetQueries(s1, s2, 1);
        testFacetQueries(s1, s3, 0);
    }

    private void testFacetQueries(Suburb mainSuburb, Suburb facetSuburb, double count) throws Exception {
        Map response;SearchParams searchParams = new SearchParams();
        searchParams.setDebugQuery(true);
        searchParams.addSuburb(mainSuburb);

        SolrQuery query = SolrQueryBuilder.valueOf(searchParams, "10", 0, 10).build();
        query.setFacet(true);

        LocationFacetQueryBuilder builder = LocationFacetQueryBuilder.valueOf(Collections.singletonList(facetSuburb),
                Collections.singletonList(new Count()), searchParams, 10l).build();
        SolrQuery solrQuery = builder.getSolrQuery();
        Map<String, Count> facetQueries = builder.getFacetQueries();
        String fq = facetQueries.keySet().toArray(new String[1])[0];

        Gson gson = new Gson();
        response = gson.fromJson(JQ(new LocalSolrQueryRequest(h.getCore(), solrQuery)), Map.class);

        double counter = (double) ((Map) ((Map) response.get("facet_counts")).get("facet_queries")).get(fq);
        assertEquals(count, counter, 0.0);
    }

    private Map searchBySuburb(Suburb... suburb) throws Exception {
        SearchParams searchParams = new SearchParams();
        for (Suburb s : suburb) {
            searchParams.addSuburb(s);
        }
        searchParams.setDebugQuery(true);
        SolrQuery query = SolrQueryBuilder.valueOf(searchParams, "10", 0, 10).build();
        Gson gson = new Gson();
        return gson.fromJson(JQ(new LocalSolrQueryRequest(h.getCore(), query)), Map.class);
    }
}