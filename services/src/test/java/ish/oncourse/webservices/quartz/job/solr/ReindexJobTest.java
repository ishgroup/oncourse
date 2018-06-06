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
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.junit.*;
import org.quartz.*;
import org.quartz.listeners.JobListenerSupport;

import java.io.IOException;
import java.util.Properties;

/**
 * This test can be run only on real environment
 */
public class ReindexJobTest {
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
	public void testSuburbs() throws Exception {
		testJob(ReindexSuburbsJob.class, SolrCollection.suburbs, 16079);
	}

	@Test
	@Ignore
	public void testTags() throws Exception {
		testJob(ReindexTagsJob.class, SolrCollection.tags, 100);
	}

	private void testJob(Class<? extends Job> jobClass, SolrCollection collection, long expectedResult) throws SchedulerException, InterruptedException, IOException, SolrServerException {
		new BuildJob()
				.groupName(QuartzModule.QUARTZ_GROUP_SOLR)
				.jobClass(jobClass)
				.cronString("0 0/1 * ? * *")
				.build(scheduler);


		scheduler.triggerJob(JobKey.jobKey(jobClass.getName(), QuartzModule.QUARTZ_GROUP_SOLR));

		boolean[] executed = new boolean[]{false};

		scheduler.getListenerManager().addJobListener(new JobListenerSupport() {
			@Override
			public String getName() {
				return ReindexTagsJob.class.getName();
			}

			@Override
			public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
				executed[0] = true;
			}
		});

		while (!executed[0] || !scheduler.getCurrentlyExecutingJobs().isEmpty())
			Thread.sleep(100);

		QueryResponse response = solrClient.query(collection.name(), new SolrQuery("*:*"));
		Assert.assertEquals(expectedResult, response.getResults().getNumFound());
	}


	@After
	public void after() throws SchedulerException, IOException {
		solrClient.close();
		solrTestData.getTestContext().close();
		scheduler.shutdown();
	}
}
