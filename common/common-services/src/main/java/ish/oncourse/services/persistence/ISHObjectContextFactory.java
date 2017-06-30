/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
*/

package ish.oncourse.services.persistence;

import ish.oncourse.util.ContextUtil;
import org.apache.cayenne.DataChannel;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.access.DataDomain;
import org.apache.cayenne.access.DataRowStore;
import org.apache.cayenne.access.ObjectStore;
import org.apache.cayenne.configuration.DefaultRuntimeProperties;
import org.apache.cayenne.configuration.RuntimeProperties;
import org.apache.cayenne.configuration.server.DataContextFactory;

public class ISHObjectContextFactory extends DataContextFactory {

	public static final String QUERY_CACHE_INJECTION_KEY = "local";

	protected ObjectContext createFromGenericChannel(DataChannel parent) {

		DataRowStore snapshotCache = createDataRowStore(dataDomain);
		ISHObjectContext context = new ISHObjectContext(parent, objectStoreFactory.createObjectStore(snapshotCache));

		fillContext(context, dataDomain);
		return context;
	}

	protected ObjectContext createFromDataContext(DataContext parent) {
		ObjectStore objectStore = objectStoreFactory.createObjectStore(null);
		ISHObjectContext context = new ISHObjectContext(parent, objectStore);

		context.setValidatingObjectsOnCommit(parent.isValidatingObjectsOnCommit());
		context.setUsingSharedSnapshotCache(parent.isUsingSharedSnapshotCache());
		context.setTransactionFactory(transactionFactory);

//		QueryCache queryCache = injector.getInstance(Key.get(QueryCache.class, QUERY_CACHE_INJECTION_KEY));
//		context.setQueryCache(queryCache);

		context.setRecordQueueingEnabled(true);
		return context;
	}

	protected ObjectContext createdFromDataDomain(DataDomain parent) {
		DataRowStore snapshotCache = createDataRowStore(parent);
		ISHObjectContext context = new ISHObjectContext(parent, objectStoreFactory.createObjectStore(snapshotCache));

		fillContext(context, parent);
		return context;
	}

	private void fillContext(ISHObjectContext context, DataDomain parent) {
		context.setUsingSharedSnapshotCache(ContextUtil.isObjectCacheEnabled() && parent.isSharedCacheEnabled());
		context.setValidatingObjectsOnCommit(parent.isValidatingObjectsOnCommit());
		context.setTransactionFactory(transactionFactory);

//		QueryCache queryCache = injector.getInstance(Key.get(QueryCache.class, QUERY_CACHE_INJECTION_KEY));
//		context.setQueryCache(queryCache);

		context.setRecordQueueingEnabled(true);
	}

	protected DataRowStore createDataRowStore(DataDomain parent) {
		if (parent.isSharedCacheEnabled()) {
			return parent.getSharedSnapshotCache();
		}

		RuntimeProperties properties = new DefaultRuntimeProperties(parent.getProperties());
		return new DataRowStore(parent.getName(), properties, eventManager);
	}

}

