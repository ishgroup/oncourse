package ish.oncourse.cayenne.cache

import ish.oncourse.cache.ICacheFactory
import ish.oncourse.cache.ICacheProvider
import org.apache.cayenne.cache.QueryCache
import org.apache.cayenne.cache.QueryCacheEntryFactory
import org.apache.cayenne.di.BeforeScopeEnd
import org.apache.cayenne.di.Inject
import org.apache.cayenne.query.QueryMetadata

import javax.cache.Cache
import java.util.concurrent.ConcurrentHashMap

class JCacheQueryCache implements QueryCache {

    private final ICacheProvider cacheProvider

    private ICacheEnabledService enabledService

    private final ICacheFactory<String, List> cacheFactory

    private Set<String> seenCacheNames = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>())

    JCacheQueryCache(@Inject ICacheProvider cacheProvider, @Inject ICacheEnabledService enabledService, @Inject ICacheInvalidationService cacheInvalidationService) {
        this.cacheProvider = cacheProvider
        this.cacheFactory = cacheProvider.createFactory(String.class, List.class)
        this.enabledService = enabledService
        cacheInvalidationService.setCache(this)
        cacheInvalidationService.init()
    }

    @Override
    List get(QueryMetadata metadata) {
        String key = Objects.requireNonNull(metadata.getCacheKey())
        Cache<String, List> cache = createIfAbsent(metadata)

        return cache.get(key)
    }

    @Override
    List get(QueryMetadata metadata, QueryCacheEntryFactory factory) {
        String key = Objects.requireNonNull(metadata.getCacheKey())
        Cache<String, List> cache = cacheFactory.createIfAbsent(cacheName(metadata),)

        List<?> result = cache.get(key)
        return result && enabledService.cacheEnabled ? result : (List) cache.invoke(key, new JCacheEntryLoader(factory, enabledService.cacheEnabled))
    }

    @Override
    void put(QueryMetadata metadata, List results) {
        String key = Objects.requireNonNull(metadata.getCacheKey())
        Cache<String, List> cache = createIfAbsent(metadata)

        cache.put(key, results)
    }

    @Override
    void remove(String key) {
        if (key != null)
            for (String cache : seenCacheNames) getCache(cache).remove(key)
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
        removeGroup(groupKey)
    }

    @Override
    void clear() {
        for (String name : seenCacheNames) {
            getCache(name).clear()
        }
    }

    protected Cache<String, List> createIfAbsent(QueryMetadata metadata) {
        return createIfAbsent(cacheName(metadata))
    }

    @SuppressWarnings("unchecked")
    protected Cache<String, List> createIfAbsent(String cacheName) {
        Cache<String, List> cache = cacheFactory.createIfAbsent(cacheName)
        seenCacheNames.add(cacheName)
        return cache
    }


    protected Cache<String, List> getCache(String name) {
        return cacheProvider.cacheManager.getCache(name)
    }

    private static String cacheName(QueryMetadata metadata) {
        return metadata.cacheGroup ? metadata.cacheGroup : JCacheConstants.DEFAULT_CACHE_NAME
    }

    @BeforeScopeEnd
    void shutdown() {
        cacheProvider.cacheManager.close()
    }
}