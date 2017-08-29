/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
*/

package ish.oncourse.services.persistence;

import org.apache.cayenne.DataChannel;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.access.DataDomain;
import org.apache.cayenne.access.DataRowStore;
import org.apache.cayenne.access.ObjectStore;
import org.apache.cayenne.cache.NestedQueryCache;
import org.apache.cayenne.configuration.DefaultRuntimeProperties;
import org.apache.cayenne.configuration.RuntimeProperties;
import org.apache.cayenne.configuration.server.DataContextFactory;

public class ISHObjectContextFactory extends DataContextFactory {

	public static final String QUERY_CACHE_INJECTION_KEY = "local";
	private boolean objectCacheEnabled = false;

	public ISHObjectContextFactory(boolean objectCacheEnabled) {
		this.objectCacheEnabled = objectCacheEnabled;
	}


	protected ObjectContext createFromGenericChannel(DataChannel parent) {

		DataRowStore snapshotCache = createDataRowStore(dataDomain);
		ISHObjectContext context = new ISHObjectContext(parent, objectStoreFactory.createObjectStore(snapshotCache));

		return initContext(context, dataDomain.isValidatingObjectsOnCommit(), objectCacheEnabled
				&& dataDomain.isSharedCacheEnabled());
	}

	protected ObjectContext createFromDataContext(DataContext parent) {
		ObjectStore objectStore = objectStoreFactory.createObjectStore(null);
		ISHObjectContext context = new ISHObjectContext(parent, objectStore);
		return initContext(context, parent.isValidatingObjectsOnCommit(),
				parent.isUsingSharedSnapshotCache());
	}

	protected ObjectContext createdFromDataDomain(DataDomain parent) {
		DataRowStore snapshotCache = createDataRowStore(parent);
		ISHObjectContext context = new ISHObjectContext(parent, objectStoreFactory.createObjectStore(snapshotCache));

		initContext(context, parent.isValidatingObjectsOnCommit(),
				objectCacheEnabled
				&& parent.isSharedCacheEnabled());
		return context;
	}

	private ISHObjectContext initContext(ISHObjectContext context, boolean validatingObjectsOnCommit, boolean usingSharedSnapshotCache) {
		context.setUsingSharedSnapshotCache(usingSharedSnapshotCache);
		context.setValidatingObjectsOnCommit(validatingObjectsOnCommit);
		context.setTransactionFactory(transactionFactory);
		context.setQueryCache(new NestedQueryCache(queryCache));
		context.setRecordQueueingEnabled(true);
		return context;
	}

	private DataRowStore createDataRowStore(DataDomain parent) {
		if (parent.isSharedCacheEnabled()) {
			return parent.getSharedSnapshotCache();
		}

		RuntimeProperties properties = new DefaultRuntimeProperties(parent.getProperties());
		return new DataRowStore(parent.getName(), properties, eventManager);
	}

}

