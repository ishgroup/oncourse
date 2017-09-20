package ish.oncourse.cayenne.cache

import org.apache.cayenne.cache.QueryCache
import org.apache.cayenne.cache.QueryCacheEntryFactory
import org.apache.cayenne.di.BeforeScopeEnd
import org.apache.cayenne.di.Inject
import org.apache.cayenne.query.QueryMetadata

import javax.cache.Cache
import javax.cache.CacheException
import javax.cache.CacheManager
import java.util.concurrent.ConcurrentHashMap

class JCacheQueryCache implements QueryCache {

    @Inject
    protected CacheManager cacheManager
    
    @Inject
    protected JCacheConfigurationFactory configurationFactory

    private Set<String> seenCacheNames = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>())

    @Override
    List get(QueryMetadata metadata) {
        String key = Objects.requireNonNull(metadata.getCacheKey())
        Cache<String, List> cache = createIfAbsent(metadata)

        return cache.get(key)
    }

    @Override
    List get(QueryMetadata metadata, QueryCacheEntryFactory factory) {
        String key = Objects.requireNonNull(metadata.getCacheKey())
        Cache<String, List> cache = createIfAbsent(metadata)

        List<?> result = cache.get(key)
        return result?: (List)cache.invoke(key, new JCacheEntryLoader(factory))
    }

    @Override
     void put(QueryMetadata metadata, List results) {
        String key = Objects.requireNonNull(metadata.getCacheKey())
        Cache<String, List> cache = createIfAbsent(metadata)

        cache.put(key, results)
    }

    @Override
     void remove(String key) {
        if (key != null) {
            for (String cache : cacheManager.cacheNames) {
                getCache(cache).remove(key)
            }
        }
    }

    @Override
     void removeGroup(String groupKey) {
        Cache<String, List> cache = getCache(groupKey)
        if (cache != null) {
            cache.clear()
        }
    }

    @Override
    void removeGroup(String groupKey, Class<?> keyType, Class<?> valueType) {
        removeGroup(groupKey);
    }

    @Override
     void clear() {
        for (String name : seenCacheNames) {
            getCache(name).clear()
        }
    }

    @Override
    @Deprecated
     int size() {
        return -1
    }

    protected Cache<String, List> createIfAbsent(QueryMetadata metadata) {
        return createIfAbsent(cacheName(metadata))
    }

    @SuppressWarnings("unchecked")
    protected Cache<String, List> createIfAbsent(String cacheName) {

        Cache<String, List> cache = getCache(cacheName)
        if (cache == null) {

            try {
                cache = createCache(cacheName)
            } catch (CacheException e) {
                // someone else just created this cache?
                cache = getCache(cacheName)
                if (cache == null) {
                    // giving up... the error was about something else...
                    throw e
                }
            }

            seenCacheNames.add(cacheName)
        }

        return cache
    }

    protected Cache createCache(String cacheName) {
        return cacheManager.createCache(cacheName, configurationFactory.create(cacheName))
    }

    protected Cache<String, List> getCache(String name) {
        return cacheManager.getCache(name, String, List)
    }

    protected String cacheName(QueryMetadata metadata) {

        String[] cacheGroup = metadata.cacheGroups
        if (cacheGroup && cacheGroup[0]) {
            return cacheGroup[0]
        }

        // no explicit cache group
        return JCacheConstants.DEFAULT_CACHE_NAME
    }

    @BeforeScopeEnd
     void shutdown() {
        cacheManager.close()
    }
}