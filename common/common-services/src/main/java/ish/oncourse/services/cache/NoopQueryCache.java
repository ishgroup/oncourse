package ish.oncourse.services.cache;

import java.util.List;

import org.apache.cayenne.cache.QueryCache;
import org.apache.cayenne.cache.QueryCacheEntryFactory;
import org.apache.cayenne.query.QueryMetadata;

public class NoopQueryCache implements QueryCache {

	@Override
	public List get(QueryMetadata metadata) {
		return null;
	}

	@Override
	public List get(QueryMetadata metadata, QueryCacheEntryFactory factory) {
		return null;
	}

	@Override
	public void put(QueryMetadata metadata, List results) {}

	@Override
	public void remove(String key) {}

	@Override
	public void removeGroup(String groupKey) {}

	@Override
	public void clear() {}

	@Override
	public int size() {
		return 0;
	}

}
