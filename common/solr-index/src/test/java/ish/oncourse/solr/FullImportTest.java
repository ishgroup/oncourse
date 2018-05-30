/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.solr;

import io.reactivex.schedulers.Schedulers;
import ish.oncourse.solr.functions.course.SCourseFunctions;
import ish.oncourse.solr.query.SearchParams;
import ish.oncourse.solr.query.SolrQueryBuilder;
import ish.oncourse.test.LoadDataSet;
import ish.oncourse.test.TestContext;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
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
		new LoadDataSet()
				.dataSetFile("ish/oncourse/solr/model/SolrCourseCoreTestDataSet.xml")
				.addReplacement("[START_DATE]", DateUtils.addDays(new Date(), 1))
				.addReplacement("[END_DATE]", DateUtils.addDays(new Date(), 2))
				.load(testContext.getDS());

		SolrClient solrClient = new EmbeddedSolrServer(h.getCore());

		fullImport(testContext.getServerRuntime().newContext(), solrClient);
		fullImport(testContext.getServerRuntime().newContext(), solrClient);


		QueryResponse response = solrClient.query(SolrQueryBuilder.valueOf(new SearchParams(), "10").build());
		assertEquals(5, response.getResults().getNumFound());
	}

	public static void fullImport(ObjectContext context, SolrClient solrClient) {
		final int[] imported = new int[1];
		final Throwable[] error = new Throwable[1];
		SCourseFunctions.SCourses(context,
				new Date(), Schedulers.io()).blockingSubscribe(
				(c) -> {
					solrClient.addBean(c);
					imported[0]++;
				},
				(e) -> error[0] = e,
				() -> solrClient.commit()
		);
		if (error[0] != null) throw new RuntimeException(error[0]);
		Assert.assertEquals(5, imported[0]);
	}

	public static void loadDataSet(TestContext testContext) {
		new LoadDataSet()
				.dataSetFile("ish/oncourse/solr/model/SolrCourseCoreTestDataSet.xml")
				.addReplacement("[START_DATE]", DateUtils.addDays(new Date(), 1))
				.addReplacement("[END_DATE]", DateUtils.addDays(new Date(), 2))
				.load(testContext.getDS());
	}


}
