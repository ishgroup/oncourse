package ish.oncourse.services.persistence;

import ish.oncourse.services.cache.EHQueryCacheProvider;
import ish.oncourse.services.cache.NoopQueryCache;
import ish.oncourse.util.ContextUtil;
import net.sf.ehcache.CacheManager;
import org.apache.cayenne.cache.QueryCache;
import org.apache.cayenne.configuration.Constants;
import org.apache.cayenne.configuration.ObjectContextFactory;
import org.apache.cayenne.di.Binder;
import org.apache.cayenne.di.Key;
import org.apache.cayenne.di.Module;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ISHModule implements Module {
	private static final Logger logger = LogManager.getLogger();
	private CacheManager cacheManager;
	
	public ISHModule(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	@Override
	public void configure(Binder binder) {

		//needs for case insensitive queries
		binder.bindMap(Object.class, Constants.PROPERTIES_MAP).put(Constants.CI_PROPERTY, "true");

		binder.bind(ObjectContextFactory.class).toInstance(new ISHObjectContextFactory(ContextUtil.isObjectCacheEnabled()));

		if (ContextUtil.isQueryCacheEnabled()) {
			binder.bind(CacheManager.class).toInstance(cacheManager);
			binder.bind(QueryCache.class).toProvider(EHQueryCacheProvider.class);
			binder.bind(Key.get(QueryCache.class, ISHObjectContextFactory.QUERY_CACHE_INJECTION_KEY)).toProvider(EHQueryCacheProvider.class);
		} else {
			binder.bind(QueryCache.class).toInstance(new NoopQueryCache());
			binder.bind(Key.get(QueryCache.class, ISHObjectContextFactory.QUERY_CACHE_INJECTION_KEY)).toInstance(new NoopQueryCache());
		}
	}
}
