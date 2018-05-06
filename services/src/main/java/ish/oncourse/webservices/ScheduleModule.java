/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import io.bootique.ConfigModule;
import ish.oncourse.scheduler.ScheduleService;
import ish.oncourse.solr.reindex.ReindexCoursesJob;
import ish.oncourse.solr.reindex.ReindexSuburbsJob;
import ish.oncourse.solr.reindex.ReindexTagsJob;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryForever;
import org.apache.solr.client.solrj.SolrClient;

import java.util.Properties;

import static ish.oncourse.configuration.Configuration.AppProperty.ZK_HOST;

/**
 * User: akoiro
 * Date: 6/5/18
 */
public class ScheduleModule extends ConfigModule {
	public static final String ZNODE_MUTEX_PATH = "/willow/services/reindex";


	@Override
	public void configure(Binder binder) {
		Multibinder.newSetBinder(binder, Key.get(ScheduleService.class))
				.addBinding().to(ScheduleService.class).asEagerSingleton();
	}

	@Provides
	@Singleton
	public CuratorFramework curatorFramework(Properties applicationProperties) {
		CuratorFramework client = CuratorFrameworkFactory.newClient(applicationProperties.getProperty(ZK_HOST.getKey()), new RetryForever(1000));
		client.start();
		return client;
	}

	@Provides
	public ScheduleService createScheduledService(ServerRuntime runtime,
												  CuratorFramework curatorFramework,
												  SolrClient solrClient) {
		ObjectContext context = runtime.newContext();

		return new ScheduleService(curatorFramework, ZNODE_MUTEX_PATH)
				.addJob(new ReindexCoursesJob(context, solrClient))
				.addJob(new ReindexTagsJob(context, solrClient))
				.addJob(new ReindexSuburbsJob(context, solrClient)).start();
	}
}
