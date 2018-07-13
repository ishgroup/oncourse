/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.solr;

import ish.oncourse.solr.query.SearchParams;
import ish.oncourse.solr.query.SolrQueryBuilder;
import ish.oncourse.test.LoadDataSet;
import ish.oncourse.test.TestContext;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;

public class FullImportTest extends ASolrTest {

	static {
		InitSolr.INIT_STATIC_BLOCK();
	}


	@Test
	public void test() throws IOException, SolrServerException {
		FullImportTest.loadDataSet(testContext);

		Assert.assertEquals(5, fullImport());
		Assert.assertEquals(5, fullImport());


		QueryResponse response = solrClient.query(new SolrQueryBuilder().searchParams(new SearchParams()).collegeId("10").build());
		assertEquals(5, response.getResults().getNumFound());
	}

	public static void loadDataSet(TestContext testContext) {
		new LoadDataSet()
				.dataSetFile("ish/oncourse/solr/model/SolrCourseCoreTestDataSet.xml")
				.addReplacement("[START_DATE]", DateUtils.addDays(new Date(), 1))
				.addReplacement("[END_DATE]", DateUtils.addDays(new Date(), 2))
				.load(testContext.getDS());
	}


}
