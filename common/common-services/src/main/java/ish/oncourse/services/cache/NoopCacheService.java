package ish.oncourse.services.cache;

import org.apache.cayenne.cache.QueryCache;

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
