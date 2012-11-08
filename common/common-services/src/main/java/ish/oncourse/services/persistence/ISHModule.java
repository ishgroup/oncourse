package ish.oncourse.services.persistence;

import ish.oncourse.services.cache.NoopQueryCache;
import ish.oncourse.util.ContextUtil;

import org.apache.cayenne.cache.OSQueryCache;
import org.apache.cayenne.cache.QueryCache;
import org.apache.cayenne.configuration.ObjectContextFactory;
import org.apache.cayenne.di.Binder;
import org.apache.cayenne.di.Key;
import org.apache.cayenne.di.Module;

public class ISHModule implements Module {

	@Override
	public void configure(Binder binder) {
		binder.bind(ObjectContextFactory.class).to(ISHObjectContextFactory.class);
		if (ContextUtil.isQueryCacheEnabled()) {
			binder.bind(QueryCache.class).to(OSQueryCache.class);
			binder.bind(Key.get(QueryCache.class, ISHObjectContextFactory.QUERY_CACHE_INJECTION_KEY)).to(OSQueryCache.class);
		} else {
			binder.bind(QueryCache.class).to(NoopQueryCache.class);
			binder.bind(Key.get(QueryCache.class, ISHObjectContextFactory.QUERY_CACHE_INJECTION_KEY)).to(NoopQueryCache.class);
		}
	}
}
