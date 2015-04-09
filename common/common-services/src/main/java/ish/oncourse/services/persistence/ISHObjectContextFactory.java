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
import org.apache.cayenne.cache.QueryCache;
import org.apache.cayenne.configuration.server.DataContextFactory;
import org.apache.cayenne.di.Key;

import java.util.HashMap;

/**
 *
 * @author marek
 */
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

		QueryCache queryCache = injector.getInstance(Key.get(QueryCache.class, QUERY_CACHE_INJECTION_KEY));
		context.setQueryCache(queryCache);

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

		QueryCache queryCache = injector.getInstance(Key.get(QueryCache.class, QUERY_CACHE_INJECTION_KEY));
		context.setQueryCache(queryCache);

		context.setRecordQueueingEnabled(true);
	}

	protected DataRowStore createDataRowStore(DataDomain parent)
	{
		DataRowStore snapshotCache = null;
		if (ContextUtil.isObjectCacheEnabled())
		{
			snapshotCache = parent.isSharedCacheEnabled()?
					parent.getSharedSnapshotCache() :
					new DataRowStore(parent.getName(), parent.getProperties(), eventManager);
		}
		else
		{
			/**
			 * When cache.enabled property is set to false we disable object shared cache.
			 */
			HashMap<String,String> properties = new HashMap<>(parent.getProperties());
			properties.put(DataRowStore.SNAPSHOT_CACHE_SIZE_PROPERTY, "1");
			properties.put(DataRowStore.SNAPSHOT_EXPIRATION_PROPERTY, "0");
			snapshotCache = new DataRowStore(parent.getName(),properties, eventManager);
		}
		return snapshotCache;
	}

}

