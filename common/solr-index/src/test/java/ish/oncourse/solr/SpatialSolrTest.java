/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.solr;

import com.google.gson.Gson;
import ish.oncourse.solr.query.SearchParams;
import ish.oncourse.solr.query.SolrQueryBuilder;
import ish.oncourse.solr.query.Suburb;
import ish.oncourse.test.LoadDataSet;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.request.LocalSolrQueryRequest;
import org.junit.Test;

import java.util.Date;
import java.util.Map;

public class SpatialSolrTest extends ASolrTest {

	static {
		InitSolr.INIT_STATIC_BLOCK();
	}

	@Test
	public void test() throws Exception {

		new LoadDataSet()
				.dataSetFile("ish/oncourse/solr/SpatialSolrTest.xml")
				.addReplacement("[START_DATE]", DateUtils.addDays(new Date(), 1))
				.addReplacement("[END_DATE]", DateUtils.addDays(new Date(), 2))
				.load(testContext.getDS());

		fullImport();

		assertEquals(1.0, ((Map) searchByCoordinate(1d).get("response")).get("numFound"));
		assertEquals(2.0, ((Map) searchByCoordinate(730d).get("response")).get("numFound"));
		assertEquals(3.0, ((Map) searchByCoordinate(1000d).get("response")).get("numFound"));
	}

	private Map searchByCoordinate(Double distance) throws Exception {
		Suburb suburb = Suburb.valueOf("2000", -33.88232600, 151.20351100, distance);
		SearchParams searchParams = new SearchParams();
		searchParams.addSuburb(suburb);

		searchParams.setDebugQuery(true);
		SolrQuery query = SolrQueryBuilder.valueOf(searchParams, "10", 0, 10).build();
		Gson gson = new Gson();
		return gson.fromJson(JQ(new LocalSolrQueryRequest(h.getCore(), query)), Map.class);
	}
}
