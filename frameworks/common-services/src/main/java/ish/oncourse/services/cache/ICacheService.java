package ish.oncourse.services.cache;

import org.apache.cayenne.cache.QueryCache;

public interface ICacheService {

	/**
	 * Returns a Cayenne QueryCache adapter working on top of the same
	 * underlying cache as the ICacheService.
	 */
	QueryCache cayenneCache();

	/**
	 * Returns cached value.
	 */
	<T> T get(String key, CachedObjectProvider<T> objectProvider,
			CacheGroup... cacheGroups);
}
