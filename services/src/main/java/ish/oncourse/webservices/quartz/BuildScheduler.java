/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.quartz;

import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.DirectSchedulerFactory;
import org.quartz.impl.jdbcjobstore.InvalidConfigurationException;
import org.quartz.impl.jdbcjobstore.JobStoreTX;
import org.quartz.impl.jdbcjobstore.StdJDBCDelegate;
import org.quartz.simpl.SimpleThreadPool;
import org.quartz.utils.ConnectionProvider;
import org.quartz.utils.DBConnectionManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

/**
 * User: akoiro
 * Date: 5/6/18
 */
public class BuildScheduler {
	private static final Logger LOGGER = LogManager.getLogger();

	public static final String QUARTZ_SERVICES_SCHEDULER_NAME = "services";
	public static final String QUARTZ_CONTEXT_SERVER_RUNTIME_KEY = "ServerRuntime";
	public static final String QUARTZ_CONTEXT_SOLR_CLIENT_KEY = "SolrClient";

	private ServerRuntime serverRuntime;
	private SolrClient solrClient;

	public BuildScheduler serverRuntime(ServerRuntime serverRuntime) {
		this.serverRuntime = serverRuntime;
		return this;
	}

	public BuildScheduler solrClient(SolrClient solrClient) {
		this.solrClient = solrClient;
		return this;
	}

	public Scheduler build() {
		try {
			DBConnectionManager.getInstance().addConnectionProvider("willow", new QuartzConnectionProvider(serverRuntime.getDataSource()));
			JobStoreTX jobStore = new JobStoreTX();
			jobStore.setDataSource("willow");
			jobStore.setClusterCheckinInterval(20000);
			jobStore.setIsClustered(true);
			jobStore.setDriverDelegateClass(StdJDBCDelegate.class.getName());

			DirectSchedulerFactory factory = DirectSchedulerFactory.getInstance();

			factory.createScheduler(QUARTZ_SERVICES_SCHEDULER_NAME, UUID.randomUUID().toString(),
					new SimpleThreadPool(5, Thread.NORM_PRIORITY), jobStore);

			Scheduler scheduler = factory.getScheduler(QUARTZ_SERVICES_SCHEDULER_NAME);
			scheduler.getContext().put(QUARTZ_CONTEXT_SERVER_RUNTIME_KEY, serverRuntime);
			scheduler.getContext().put(QUARTZ_CONTEXT_SOLR_CLIENT_KEY, solrClient);
			return scheduler;
		} catch (InvalidConfigurationException | SchedulerException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static class QuartzConnectionProvider implements ConnectionProvider {
		private DataSource dataSource;

		public QuartzConnectionProvider(DataSource dataSource) {
			this.dataSource = dataSource;
		}

		@Override
		public Connection getConnection() throws SQLException {
			return dataSource.getConnection();
		}

		@Override
		public void shutdown() {
		}

		@Override
		public void initialize() {
		}
	}
}
