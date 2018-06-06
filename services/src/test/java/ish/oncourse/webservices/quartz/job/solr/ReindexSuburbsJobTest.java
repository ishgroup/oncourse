/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.quartz.job.solr;

import ish.oncourse.configuration.Configuration;
import ish.oncourse.solr.BuildSolrClient;
import ish.oncourse.solr.SolrCollection;
import ish.oncourse.webservices.quartz.BuildJob;
import ish.oncourse.webservices.quartz.BuildScheduler;
import ish.oncourse.webservices.quartz.QuartzModule;
import ish.oncourse.webservices.quartz.job.SolrTestData;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.junit.*;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import java.io.IOException;
import java.util.Properties;

/**
 * This test can be run only on real environment
 */
public class ReindexSuburbsJobTest {
	private SolrTestData solrTestData;
	private SolrClient solrClient;
	private Scheduler scheduler;

	private String zkHost = "127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183/solr";
	private String schema = "OD9910";


	@Before
	public void before() throws SchedulerException {
		solrTestData = new SolrTestData().schema(schema).create();
		Properties properties = new Properties();
		properties.setProperty(Configuration.AppProperty.ZK_HOST.getKey(), zkHost);
		solrClient = BuildSolrClient.instance(properties).build();
		scheduler = new BuildScheduler()
				.serverRuntime(solrTestData.getTestContext().getServerRuntime())
				.solrClient(solrClient).build();
		scheduler.start();
	}

	@Test
	@Ignore
	public void test() throws Exception {
		new BuildJob()
				.groupName(QuartzModule.QUARTZ_GROUP_SOLR)
				.jobClass(ReindexSuburbsJob.class)
				.cronString("0 0/1 * ? * *")
				.build(scheduler);


		scheduler.triggerJob(JobKey.jobKey(ReindexSuburbsJob.class.getName(), QuartzModule.QUARTZ_GROUP_SOLR));

		while (!scheduler.getCurrentlyExecutingJobs().isEmpty()) Thread.sleep(100);

		QueryResponse response = solrClient.query(SolrCollection.suburbs.name(), new SolrQuery("*:*"));
		Assert.assertEquals(16079, response.getResults().getNumFound());
	}

	@After
	public void after() throws SchedulerException, IOException {
		solrClient.close();
		solrTestData.getTestContext().close();
		scheduler.shutdown();
	}
}
