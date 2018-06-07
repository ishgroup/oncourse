/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.quartz;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import io.bootique.ConfigModule;
import io.bootique.shutdown.ShutdownManager;
import ish.oncourse.webservices.quartz.job.solr.ReindexCoursesJob;
import ish.oncourse.webservices.quartz.job.solr.ReindexSuburbsJob;
import ish.oncourse.webservices.quartz.job.solr.ReindexTagsJob;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.solr.client.solrj.SolrClient;
import org.quartz.Scheduler;

import java.util.Arrays;

/**
 * User: akoiro
 * Date: 6/5/18
 */
public class QuartzModule extends ConfigModule {

	public static final String QUARTZ_CONTEXT_SERVER_RUNTIME_KEY = "ServerRuntime";
	public static final String QUARTZ_CONTEXT_SOLR_CLIENT_KEY = "SolrClient";

	public static final String QUARTZ_GROUP_SOLR = "solrGroup";


	public static final String DEFAULT_CRON_REINDEX_COURSES = "0 53 3 ? * *";
	public static final String DEFAULT_CRON_REINDEX_TAGS = "0 9 4 ? * *";
	public static final String DEFAULT_CRON_REINDEX_SUBURBS = "0 34 4 ? * *";

	@Override
	public void configure(Binder binder) {
		Multibinder.newSetBinder(binder, Key.get(Scheduler.class))
				.addBinding().to(Scheduler.class).asEagerSingleton();
	}

	@Provides
	@Singleton
	public Scheduler scheduler(ServerRuntime serverRuntime, SolrClient solrClient, ShutdownManager shutdownManager) throws Exception {
		Scheduler scheduler = new BuildScheduler()
				.serverRuntime(serverRuntime)
				.solrClient(solrClient).build();

		scheduler.start();

		registerJobs(scheduler);

		shutdownManager.addShutdownHook(scheduler::shutdown);

		return scheduler;
	}

	private void unregisterJobs(Scheduler scheduler) {
		DeleteJob[] list = new DeleteJob[]{
				new DeleteJob()
						.groupName(QUARTZ_GROUP_SOLR)
						.jobClass(ReindexCoursesJob.class),
				new DeleteJob()
						.groupName(QUARTZ_GROUP_SOLR)
						.jobClass(ReindexSuburbsJob.class),
				new DeleteJob()
						.groupName(QUARTZ_GROUP_SOLR)
						.jobClass(ReindexTagsJob.class)
		};
		Arrays.stream(list).forEach((j) -> j.delete(scheduler));
	}

	private void registerJobs(Scheduler scheduler) {
		BuildJob[] builds = new BuildJob[]{
//				new BuildJob()
//						.groupName(QUARTZ_GROUP_SOLR)
//						.jobClass(ReindexCoursesJob.class)
//						.cronString(DEFAULT_CRON_REINDEX_COURSES),
				new BuildJob()
						.groupName(QUARTZ_GROUP_SOLR)
						.jobClass(ReindexSuburbsJob.class)
						.cronString(DEFAULT_CRON_REINDEX_SUBURBS),
				new BuildJob()
						.groupName(QUARTZ_GROUP_SOLR)
						.jobClass(ReindexTagsJob.class)
						.cronString(DEFAULT_CRON_REINDEX_TAGS)


		};
		Arrays.stream(builds).forEach((b) -> b.build(scheduler));
	}

}
