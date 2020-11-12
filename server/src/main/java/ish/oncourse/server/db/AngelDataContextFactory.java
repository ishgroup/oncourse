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
package ish.oncourse.server.db;

import ish.oncourse.server.ISHDataContext;
import org.apache.cayenne.DataChannel;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.access.DataDomain;
import org.apache.cayenne.access.DataRowStore;
import org.apache.cayenne.cache.NestedQueryCache;
import org.apache.cayenne.configuration.DefaultRuntimeProperties;
import org.apache.cayenne.configuration.RuntimeProperties;
import org.apache.cayenne.configuration.server.DataContextFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 */
public class AngelDataContextFactory extends DataContextFactory {

	private static final Logger logger = LogManager.getLogger();

	@Override
	protected ObjectContext createFromGenericChannel(DataChannel parent) {

		// for new dataRowStores use the same name for all stores
		// it makes it easier to track the event subject
		RuntimeProperties properties = new DefaultRuntimeProperties(this.dataDomain.getProperties());
		var snapshotCache = this.dataDomain.isSharedCacheEnabled() ? this.dataDomain.getSharedSnapshotCache() :
				new DataRowStore(this.dataDomain.getName(), properties, this.eventManager);

		var context = new ISHDataContext(parent, objectStoreFactory.createObjectStore(snapshotCache));
		context.setValidatingObjectsOnCommit(this.dataDomain.isValidatingObjectsOnCommit());
		context.setQueryCache(new NestedQueryCache(queryCache));
		context.setRecordQueueingEnabled(true);

		return context;
	}

	@Override
	protected ObjectContext createFromDataContext(DataContext parent) {
		// child ObjectStore should not have direct access to snapshot cache, so we'll create a null store
		var context = new ISHDataContext(parent, objectStoreFactory.createObjectStore(null));

		context.setValidatingObjectsOnCommit(parent.isValidatingObjectsOnCommit());
		context.setUsingSharedSnapshotCache(parent.isUsingSharedSnapshotCache());
		context.setQueryCache(new NestedQueryCache(queryCache));
		context.setRecordQueueingEnabled(true);

		return context;
	}

	@Override
	protected ObjectContext createdFromDataDomain(DataDomain parent) {

		// for new dataRowStores use the same name for all stores
		// it makes it easier to track the event subject
		RuntimeProperties properties = new DefaultRuntimeProperties(parent.getProperties());
		var snapshotCache = parent.isSharedCacheEnabled() ? parent.getSharedSnapshotCache() :
				new DataRowStore(parent.getName(), properties, this.eventManager);

		var context = new ISHDataContext(parent, objectStoreFactory.createObjectStore(snapshotCache));
		context.setValidatingObjectsOnCommit(parent.isValidatingObjectsOnCommit());
		context.setQueryCache(new NestedQueryCache(queryCache));
		context.setTransactionFactory(transactionFactory);
		context.setRecordQueueingEnabled(true);

		return context;
	}
}
