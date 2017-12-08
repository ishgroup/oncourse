package ish.oncourse.services.cache;

import org.apache.cayenne.cache.QueryCache;
import org.apache.cayenne.cache.QueryCacheEntryFactory;
import org.apache.cayenne.query.QueryMetadata;

import java.util.List;

/**
 * {@link QueryCache} implementation which performs no caching.
 * 
 * @author dzmitry
 */
public class NoopQueryCache implements QueryCache {

	@Override
	public List get(QueryMetadata metadata) {
		return null;
	}

	@Override
	public List get(QueryMetadata metadata, QueryCacheEntryFactory factory) {
		return factory.createObject();
	}

	@Override
	public void put(QueryMetadata metadata, List results) {}

	@Override
	public void remove(String key) {}

	@Override
	public void removeGroup(String groupKey) {}

	@Override
	public void removeGroup(String groupKey, Class<?> keyType, Class<?> valueType) {}

	@Override
	public void clear() {}

	@Override
	public int size() {
		return 0;
	}

}
