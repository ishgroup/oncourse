package ish.oncourse.willow.cache

import org.apache.cayenne.configuration.RuntimeProperties
import org.apache.cayenne.di.DIRuntimeException
import org.apache.cayenne.di.Inject
import org.apache.cayenne.di.Provider

import javax.cache.CacheException
import javax.cache.CacheManager
import javax.cache.Caching
import javax.cache.spi.CachingProvider

class JCacheManagerProvider implements Provider<CacheManager> {

    @Inject
    private RuntimeProperties properties

    @Override
    CacheManager get() throws DIRuntimeException {
        CachingProvider provider
        try {
            provider = Caching.cachingProvider
        } catch (CacheException e) {
            throw new RuntimeException("'cayenne-jcache' doesn't bundle any JCache providers. " +
                    "You must place a JCache 1.0 provider on classpath explicitly.", e)
        }

        CacheManager manager
        URI jcacheConfig = config

        if (!jcacheConfig) {
            manager = provider.cacheManager
        } else {
            manager = provider.getCacheManager(jcacheConfig, (ClassLoader) null)
        }

        return manager
    }

    private URI getConfig() {
        String config = properties.get(JCacheConstants.JCACHE_PROVIDER_CONFIG)
        if (!config) {
            this.class.classLoader.getResource('ehcache.xml').toURI()
        } else {
            try {
                new URI(config)
            } catch (URISyntaxException ex) {
                throw new RuntimeException("Wrong value for JCache provider config property", ex)
            }
        }
    }
}
