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
import org.apache.cayenne.cache.QueryCache;
import org.apache.cayenne.configuration.server.DataContextFactory;
import org.apache.cayenne.di.Key;

/**
 * 
 * @author marek
 */
public class ISHObjectContextFactory extends DataContextFactory {
	public static final String QUERY_CACHE_INJECTION_KEY = "local";

	protected ObjectContext createFromGenericChannel(DataChannel parent) {

		DataRowStore snapshotCache = (dataDomain.isSharedCacheEnabled()) ? dataDomain.getSharedSnapshotCache()
				: new DataRowStore(dataDomain.getName(), dataDomain.getProperties(), eventManager);
		@SuppressWarnings("deprecation")
		ISHObjectContext context = new ISHObjectContext(parent, new ObjectStore(snapshotCache));
		context.setValidatingObjectsOnCommit(dataDomain.isValidatingObjectsOnCommit());
		QueryCache queryCache = injector.getInstance(Key.get(QueryCache.class, QUERY_CACHE_INJECTION_KEY));
		context.setQueryCache(queryCache);
		context.setRecordQueueingEnabled(true);
		return context;
	}

	protected ObjectContext createFromDataContext(DataContext parent) {
		@SuppressWarnings("deprecation")
		ObjectStore objectStore = new ObjectStore();
		ISHObjectContext context = new ISHObjectContext(parent, objectStore);
		context.setValidatingObjectsOnCommit(parent.isValidatingObjectsOnCommit());
		context.setUsingSharedSnapshotCache(parent.isUsingSharedSnapshotCache());
		QueryCache queryCache = injector.getInstance(Key.get(QueryCache.class, QUERY_CACHE_INJECTION_KEY));
		context.setQueryCache(queryCache);
		context.setRecordQueueingEnabled(true);
		return context;
	}

	protected ObjectContext createdFromDataDomain(DataDomain parent) {
		DataRowStore snapshotCache = (parent.isSharedCacheEnabled()) ? parent.getSharedSnapshotCache() : new DataRowStore(
				parent.getName(), parent.getProperties(), eventManager);
		@SuppressWarnings("deprecation")
		ISHObjectContext context = new ISHObjectContext(parent, new ObjectStore(snapshotCache));
		context.setValidatingObjectsOnCommit(parent.isValidatingObjectsOnCommit());
		QueryCache queryCache = injector.getInstance(Key.get(QueryCache.class, QUERY_CACHE_INJECTION_KEY));
		context.setQueryCache(queryCache);
		context.setRecordQueueingEnabled(true);
		return context;
	}

}
