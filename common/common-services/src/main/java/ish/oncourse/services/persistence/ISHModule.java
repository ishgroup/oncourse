package ish.oncourse.services.persistence;

import ish.oncourse.services.cache.OSQueryCacheProvider;
import org.apache.cayenne.cache.QueryCache;
import org.apache.cayenne.configuration.ObjectContextFactory;
import org.apache.cayenne.di.Binder;
import org.apache.cayenne.di.Key;
import org.apache.cayenne.di.Module;

public class ISHModule implements Module {

	@Override
	public void configure(Binder binder) {
		binder.bind(ObjectContextFactory.class).to(ISHObjectContextFactory.class);
		binder.bind(QueryCache.class).toProvider(OSQueryCacheProvider.class);
		binder.bind(Key.get(QueryCache.class, ISHObjectContextFactory.QUERY_CACHE_INJECTION_KEY)).toProvider(OSQueryCacheProvider.class);
	}
}
