package ish.oncourse.services.cache;

import org.apache.cayenne.cache.QueryCache;

/**
 * {@link ICacheService} implementation doing no which returns cache instance 
 * which doing no caching. Used to force the app to perform no caching.
 * 
 * @author dzmitry
 */
public class NoopCacheService implements ICacheService {
	
	private NoopQueryCache queryCache;
	
	public NoopCacheService() {
		this.queryCache = new NoopQueryCache();
	}

	@Override
	public QueryCache cayenneCache() {
		return this.queryCache;
	}

	@Override
	public <T> T get(String key, CachedObjectProvider<T> objectProvider,
			CacheGroup... cacheGroups) {
		return objectProvider.create();
	}

}
