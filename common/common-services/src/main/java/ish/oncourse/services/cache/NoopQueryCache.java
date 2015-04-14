package ish.oncourse.services.cache;

import org.apache.cayenne.CayenneRuntimeException;
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
		Object newObject = factory.createObject();

        if (!(newObject instanceof List)) {
            if (newObject == null) {
                throw new CayenneRuntimeException("Null on cache rebuilding: "
                        + metadata.getCacheKey());
            }
            else {
                throw new CayenneRuntimeException(
                        "Invalid query result, expected List, got "
                                + newObject.getClass().getName());
            }
        }
        
        return (List) newObject;
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
