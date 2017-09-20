package ish.oncourse.cayenne.cache

import org.apache.cayenne.cache.QueryCache
import org.apache.cayenne.di.Binder
import org.apache.cayenne.di.Module

import javax.cache.CacheManager

class JCacheModule implements Module {
    
    @Override
    void configure(Binder binder) {
        binder.bind(CacheManager.class).toProvider(JCacheManagerProvider)
        binder.bind(JCacheConfigurationFactory.class).to(JCacheDefaultConfigurationFactory.class)
        binder.bind(QueryCache.class).to(JCacheQueryCache.class)
    }
}
