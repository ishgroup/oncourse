package ish.oncourse.cayenne.cache

import groovy.transform.CompileStatic

import javax.cache.Cache
import javax.cache.CacheManager
import javax.cache.configuration.Configuration
import javax.cache.configuration.Factory
import javax.cache.configuration.MutableConfiguration
import javax.cache.expiry.CreatedExpiryPolicy
import javax.cache.expiry.Duration
import javax.cache.expiry.ExpiryPolicy
import java.util.concurrent.atomic.AtomicBoolean

class JCacheDefaultConfigurationFactory implements JCacheConfigurationFactory {
    private final Configuration<String, List> configuration = createDefaultConfig(String.class, List.class)

    @Override
    Configuration<String, List> create(String cacheGroup) {
        configuration
    }


    static <K, V> Configuration<K, V> createDefaultConfig(Class<K> keyType, Class<V> valueType,
                                                          Factory<ExpiryPolicy> factory = CreatedExpiryPolicy.factoryOf(Duration.TEN_MINUTES)) {
        return new MutableConfiguration<K, V>()
                .setTypes(keyType, valueType)
                .setStoreByValue(false)
                .setExpiryPolicyFactory(factory)
                .setManagementEnabled(true)
                .setStatisticsEnabled(true)
    }

    @CompileStatic
    static class GetOrCreateCache {
        private final CacheManager cacheManager
        private final AtomicBoolean busy = new AtomicBoolean(false)

        GetOrCreateCache(CacheManager cacheManager) {
            this.cacheManager = cacheManager
        }

        public <K, V> Cache<K, V> getOrCreate(String name, Class<K> keyType, Class<V> valueType) {
            while (!busy.compareAndSet(false, true)) {
                Thread.sleep(10)
            }
            Cache<K, V> cache = cacheManager.getCache(name, keyType, valueType)
            if (cache == null) {
                cache = cacheManager.createCache(name, createDefaultConfig(keyType, valueType))
            }
            busy.set(false)
            return cache
        }

    }


}
