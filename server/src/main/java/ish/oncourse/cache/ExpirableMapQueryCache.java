/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.cache;

import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.cache.QueryCache;
import org.apache.cayenne.cache.QueryCacheEntryFactory;
import org.apache.cayenne.query.QueryMetadata;
import org.apache.commons.collections4.map.LRUMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * {@link QueryCache} implementation based on Cayenne's default
 * {@link org.apache.cayenne.cache.MapQueryCache}. It is exact copy of MapQueryCache with ability to expire
 * cache entries by timeout.
 */
public class ExpirableMapQueryCache implements QueryCache, Serializable {

	private static final Logger logger = LogManager.getLogger();

	public static final long DEFAULT_REFRESH_TIMEOUT = 900000L; // 15 min

	private long refreshTimeout;

	protected Map<String, ExpirableCacheEntry> map;

	/**
	 * Create {@link ExpirableMapQueryCache} instance with specific maximum size and refresh
	 * timeout.
	 *
	 * @param maxSize
	 * @param refreshTimeout
	 */
	public ExpirableMapQueryCache(int maxSize, long refreshTimeout) {
		this.map = new LRUMap(maxSize);

		this.refreshTimeout = refreshTimeout;
	}

	@SuppressWarnings("unchecked")
	public List get(QueryMetadata metadata) {
		String key = metadata.getCacheKey();
		if (key == null) {
			return null;
		}

		ExpirableCacheEntry entry;
		synchronized (this) {
			entry = map.get(key);

			if (entry != null) {
				if (isCacheEntryExpired(entry)) {
					map.remove(key);
					entry = null;

					logger.debug("Query cache miss (expired) [{}]", key);
				} else {
					logger.debug("Query cache hit [{}]", key);
				}
			} else {
				logger.debug("Query cache miss (absent) [{}]", key);
			}
		}

		return (entry != null) ? entry.list : null;
	}

	private boolean isCacheEntryExpired(ExpirableCacheEntry entry) {
		long currentTimestamp = System.currentTimeMillis();

		return currentTimestamp - entry.timestamp > refreshTimeout;
	}

	/**
	 * Returns a non-null cached value. If it is not present in the cache, it is obtained
	 * by calling {@link QueryCacheEntryFactory#createObject()} without blocking the cache. As
	 * a result there is a potential of multiple threads to be updating cache in parallel -
	 * this wouldn't lead to corruption of the cache, but can be suboptimal.
	 */
	@SuppressWarnings("unchecked")
	public List get(QueryMetadata metadata, QueryCacheEntryFactory factory) {
		List result = get(metadata);
		if (result == null) {
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

			result = (List) newObject;
			put(metadata, result);
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public void put(QueryMetadata metadata, List results) {
		String key = metadata.getCacheKey();
		if (key != null) {

			ExpirableCacheEntry entry = new ExpirableCacheEntry();
			entry.list = results;
			entry.cacheGroup = metadata.getCacheGroup();

			synchronized (this) {
				map.put(key, entry);
			}
		}
	}

	public void remove(String key) {
		if (key != null) {
			synchronized (this) {
				map.remove(key);
			}
		}
	}

	public void removeGroup(String groupKey) {
		if (groupKey != null) {
			synchronized (this) {
				Iterator<ExpirableCacheEntry> it = map.values().iterator();
				while (it.hasNext()) {
					ExpirableCacheEntry entry = it.next();
					if (groupKey.equals(entry.cacheGroup)) {
						it.remove();
					}
				}
			}
		}
	}

	@Override
	public void removeGroup(String groupKey, Class<?> keyType, Class<?> valueType) {
		removeGroup(groupKey);
	}

	public void clear() {
		synchronized (this) {
			map.clear();
		}
	}

	public int size() {
		return map.size();
	}


	static class ExpirableCacheEntry implements Serializable {
		List<?> list;
		String cacheGroup;
		long timestamp;

		public ExpirableCacheEntry() {
			this.timestamp = System.currentTimeMillis();
		}
	}
}
