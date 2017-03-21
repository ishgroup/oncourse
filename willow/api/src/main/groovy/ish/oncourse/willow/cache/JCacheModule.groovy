package ish.oncourse.willow.cache

import org.apache.cayenne.cache.QueryCache
import org.apache.cayenne.configuration.Constants
import org.apache.cayenne.di.Binder
import org.apache.cayenne.di.Module

import javax.cache.CacheManager

class JCacheModule implements Module {

    static void contributeJCacheProviderConfig(Binder binder, String providerConfigURI) {
        binder.bindMap(Constants.PROPERTIES_MAP).put(JCacheConstants.JCACHE_PROVIDER_CONFIG, providerConfigURI)
    }

    @Override
    void configure(Binder binder) {
        binder.bind(CacheManager.class).toProvider(JCacheManagerProvider)
        binder.bind(JCacheConfigurationFactory.class).to(JCacheDefaultConfigurationFactory.class)
        binder.bind(QueryCache.class).to(JCacheQueryCache.class)
    }
}
