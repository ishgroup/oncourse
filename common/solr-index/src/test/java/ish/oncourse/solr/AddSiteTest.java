/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.solr;

import io.reactivex.schedulers.Schedulers;
import ish.oncourse.solr.functions.course.SCourseFunctions;
import ish.oncourse.solr.query.SearchParams;
import ish.oncourse.solr.query.SolrQueryBuilder;
import ish.oncourse.test.context.*;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;

public class AddSiteTest extends ASolrTest {

	static {
		InitSolr.INIT_STATIC_BLOCK();
	}

	@Test
	public void test() throws IOException, SolrServerException {

		FullImportTest.loadDataSet(testContext);

		SolrClient solrClient = new EmbeddedSolrServer(h.getCore());

		fullImport();

		DataContext dataContext = new DataContext()
				.withObjectContext(testContext.getServerRuntime().newContext()).load();

		CCollege college = dataContext.collegeById(10L);
		CSite site1 = college.newSite();
		CSite site2 = college.newSite();

		CCourse course = dataContext.collegeById(10L).newCourse("TEST_SITE");
		CCourseClass courseClass = course.newCourseClassWithSessionsAndSite("1", site1, 1);
		courseClass.getCourseClass().setRoom(site2.getRooms().get(0).getRoom());
		dataContext.getObjectContext().commitChanges();


		SCourseFunctions.SCourses(testContext.getServerRuntime().newContext(),
				new Date(), Schedulers.io(), () -> Collections.singletonList(course.getCourse()))
				.blockingSubscribe((c) -> {
							solrClient.addBean(c);
							Assert.assertTrue(
									c.getSiteId().contains(site1.getSite().getId()) &&
											c.getSiteId().contains(site2.getSite().getId())
							);
						}, Throwable::printStackTrace
						, solrClient::commit);


		SearchParams params = new SearchParams();
		params.setSiteId(site1.getSite().getId());
		Assert.assertEquals(1,
				solrClient.query(SolrQueryBuilder.valueOf(params, "10").build())
						.getResults().getNumFound());

		params.setSiteId(site2.getSite().getId());
		Assert.assertEquals(1,
				solrClient.query(SolrQueryBuilder.valueOf(params, "10").build())
						.getResults().getNumFound());
	}
}
