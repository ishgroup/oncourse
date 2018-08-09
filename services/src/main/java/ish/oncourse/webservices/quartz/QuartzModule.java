/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.quartz;

import com.google.inject.*;
import com.google.inject.multibindings.Multibinder;
import io.bootique.ConfigModule;
import io.bootique.jetty.MappedFilter;
import io.bootique.shutdown.ShutdownManager;
import ish.oncourse.configuration.Configuration;
import ish.oncourse.services.message.IMessagePersonService;
import ish.oncourse.services.payment.IPaymentService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.PreferenceControllerFactory;
import ish.oncourse.services.sms.ISMSService;
import ish.oncourse.solr.BuildSolrClient;
import ish.oncourse.tapestry.WillowTapestryFilter;
import ish.oncourse.webservices.jobs.PaymentInExpireJob;
import ish.oncourse.webservices.jobs.SMSJob;
import ish.oncourse.webservices.jobs.UpdateAmountOwingJob;
import ish.oncourse.webservices.quartz.job.solr.ReindexCoursesJob;
import ish.oncourse.webservices.quartz.job.solr.ReindexSuburbsJob;
import ish.oncourse.webservices.quartz.job.solr.ReindexTagsJob;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.solr.client.solrj.SolrClient;
import org.quartz.Scheduler;

import java.util.Arrays;
import java.util.Properties;

/**
 * User: akoiro
 * Date: 6/5/18
 */
public class QuartzModule extends ConfigModule {

	public static final String QUARTZ_CONTEXT_SERVER_RUNTIME_KEY = "ServerRuntime";
	public static final String QUARTZ_CONTEXT_SOLR_CLIENT_KEY = "SolrClient";

	public static final String QUARTZ_GROUP_SOLR = "solrGroup";
	public static final String QUARTZ_GROUP_DEFAULT = "defaultGroup";



	public static final String DEFAULT_CRON_REINDEX_COURSES = "0 53 3 ? * *";
	public static final String DEFAULT_CRON_REINDEX_TAGS = "0 */5 * ? * *";
	public static final String DEFAULT_CRON_REINDEX_SUBURBS = "0 34 4 ? * *";

	public static final String DEFAULT_CRON_SMS = "0 */1 * ? * * *";
	public static final String DEFAULT_CRON_PAYMENT = "0 5/20 * ? * * *";
	public static final String DEFAULT_CRON_INVOICE = "0 10/20 * ? * * *";

	@Override
	public void configure(Binder binder) {
		Multibinder.newSetBinder(binder, Key.get(Scheduler.class))
				.addBinding().to(Scheduler.class).asEagerSingleton();
	}

	@Provides
	@Singleton
	public Scheduler scheduler(ServerRuntime serverRuntime,
							   SolrClient solrClient,
							   ShutdownManager shutdownManager, MappedFilter<WillowTapestryFilter> filter
	) throws Exception {
		
		Scheduler scheduler = new BuildScheduler()
				.serverRuntime(serverRuntime)
				.solrClient(solrClient)
				.serviceProvider(new ServiceProvider(filter))
				.build();

		scheduler.start();

		registerJobs(scheduler);

		shutdownManager.addShutdownHook(scheduler::shutdown);

		return scheduler;
	}

	@Provides
	@Singleton
	public Properties applicationProperties() {
		return Configuration.loadProperties();
	}

	@Provides
	@Singleton()
	public SolrClient createSolrClient(Properties appProps, ShutdownManager shutdownManager) {
		SolrClient solrClient = BuildSolrClient.instance(appProps).build();
		shutdownManager.addShutdownHook(solrClient);
		return solrClient;
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
						.jobClass(ReindexTagsJob.class),
				new DeleteJob()
						.groupName(QUARTZ_GROUP_DEFAULT)
						.jobClass(SMSJob.class),
				new DeleteJob()
						.groupName(QUARTZ_GROUP_DEFAULT)
						.jobClass(PaymentInExpireJob.class),
				new DeleteJob()
						.groupName(QUARTZ_GROUP_DEFAULT)
						.jobClass(UpdateAmountOwingJob.class)
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
						.cronString(DEFAULT_CRON_REINDEX_TAGS),
				new BuildJob()
						.groupName(QUARTZ_GROUP_DEFAULT)
						.jobClass(PaymentInExpireJob.class)
						.cronString(DEFAULT_CRON_PAYMENT),
				new BuildJob()
						.groupName(QUARTZ_GROUP_DEFAULT)
						.jobClass(SMSJob.class)
						.cronString(DEFAULT_CRON_SMS),
				new BuildJob()
						.groupName(QUARTZ_GROUP_DEFAULT)
						.jobClass(UpdateAmountOwingJob.class)
						.cronString(DEFAULT_CRON_INVOICE)
				


		};
		Arrays.stream(builds).forEach((b) -> b.build(scheduler));
	}

	public static class ServiceProvider {
		
		WillowTapestryFilter tapestry;
				
		ServiceProvider(MappedFilter<WillowTapestryFilter> filter) {
			tapestry = filter.getFilter();
		}
		
		public <T> T get(Class<T> c) {
			return tapestry.getService(c);
		} 
		
	}

}
