/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */
package ish.oncourse.server;

import javax.inject.Inject;
import javax.inject.Singleton;
import ish.math.MoneyType;
import ish.oncourse.server.cayenne.glue.CayenneDataObject;
import ish.util.Maps;
import org.apache.cayenne.ConfigurationException;
import org.apache.cayenne.DataChannelQueryFilter;
import org.apache.cayenne.DataChannelSyncFilter;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.access.DataNode;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.map.LifecycleEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import java.util.Map;

import static ish.oncourse.server.api.servlet.ApiFilter.validateOnly;

/**
 * Responsible for setting up database connection.
 */
@Singleton
public class CayenneService implements ICayenneService {

	private static Logger logger = LogManager.getLogger();

	private static final Map<LifecycleEvent, String> LIFECYCLE_CALLBACKS = Maps.asMap(new LifecycleEvent[] {
			LifecycleEvent.POST_ADD,
			LifecycleEvent.PRE_PERSIST,
			LifecycleEvent.POST_PERSIST,
			LifecycleEvent.POST_REMOVE,
			LifecycleEvent.POST_UPDATE,
			LifecycleEvent.POST_LOAD,
			LifecycleEvent.PRE_REMOVE,
			LifecycleEvent.PRE_UPDATE }, new String[] {
			"postAdd",
			"prePersist",
			"postPersist",
			"postRemove",
			"postUpdate",
			"postLoad",
			"preRemove",
			"preUpdate" });

	private final ServerRuntime runtime;
	private DataContext sharedContext;

	@Inject
	public CayenneService(ServerRuntime runtime) {
		this.runtime = runtime;

		try {
			for (var node : runtime.getDataDomain().getDataNodes()) {
				// install ExtendedTypes
				installExtendedTypes(node);
				if (runtime.getDataDomain().getDefaultNode() == null) {
					runtime.getDataDomain().setDefaultNode(node);
				}
			}
			setupListeners();

			logger.info("creating shared data context...");

			this.sharedContext = getNewContext();

			logger.info("Database connection established...");


		} catch (ConfigurationException e) {
			logger.error("Configuration exception occured while connecting to database", e);
			throw new RuntimeException("Could not connect to database server. Please check the configuration.", e);
		} catch (Exception e) {
			logger.error("Exception occured while connecting to database", e);
			throw new RuntimeException("Could not connect to database server. Please check the logs.", e);
		}
	}

	@Override
	public DataNode getDataNode() {
		return null;
	}


	private void installExtendedTypes(DataNode node) {
		node.getAdapter().getExtendedTypes().registerType(new MoneyType());
	}

	/**
	 * Configures Cayenne lifecycle listeners.
	 */
	private void setupListeners() {
		for (final var anEvent : LIFECYCLE_CALLBACKS.keySet()) {
			final var methodName = LIFECYCLE_CALLBACKS.get(anEvent);
			this.runtime.getChannel().getEntityResolver().getCallbackRegistry().addCallback(anEvent, CayenneDataObject.class, methodName);
		}
	}

	/**
	 * Registers an annotated persistent object event listener.
	 */
	@Override
	public void addListener(Object listener) {
		logger.info("Adding listener: {}", listener.getClass().getName());
		this.runtime.getChannel().getEntityResolver().getCallbackRegistry().addListener(listener);
	}

	@Override
	public void addSyncFilter(DataChannelSyncFilter filter) {
		this.runtime.getDataDomain().addSyncFilter(filter);
	}

	@Override
	public void addQueryFilter(DataChannelQueryFilter filter) {
		this.runtime.getDataDomain().addQueryFilter(filter);
	}

	/**
	 * Instantiates a new context with lifecycle events.
	 *
	 * @return a context
	 */
	@Override
	public DataContext getNewContext() {
		if (validateOnly.get() != null && validateOnly.get()) {
			return getNewNonReplicatingContext();
		}
		return (DataContext) this.runtime.newContext();
	}

	/**
	 * @return shared context
	 */
	@Override
	public DataContext getSharedContext() {
		return this.sharedContext;
	}

	@Override
	public DataContext getNewNonReplicatingContext() {
		var dc = (ISHDataContext) this.runtime.newContext();
		dc.setRecordQueueingEnabled(false);
		return dc;
	}

	@Override
	public DataContext getNewReadonlyContext() {
		var dc = (ISHDataContext) this.runtime.newContext();
		dc.setRecordQueueingEnabled(false);
		dc.setReadOnly(true);
		return dc;
	}

	@Override
	public ServerRuntime getServerRuntime() {
		return runtime;
	}

	@Override
	public DataSource getDataSource() {
		return getServerRuntime().getDataSource("AngelNode");
	}
}
