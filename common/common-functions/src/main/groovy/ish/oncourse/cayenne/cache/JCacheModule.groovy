package ish.oncourse.cayenne.cache

import ish.oncourse.cache.ICacheProvider
import ish.oncourse.cache.caffeine.CaffeineProvider
import org.apache.cayenne.cache.QueryCache
import org.apache.cayenne.di.Binder
import org.apache.cayenne.di.Module

import static ish.oncourse.cayenne.cache.ICacheEnabledService.CacheDisableReason.NONE

class JCacheModule implements Module {

    @Override
    void configure(Binder binder) {
        binder.bind(ICacheProvider.class).to(CaffeineProvider.class)
        binder.bind(QueryCache.class).to(JCacheQueryCache.class)
        binder.bind(ICacheEnabledService.class).to(DefaultCacheEnabledService.class)
        binder.bind(ICacheInvalidationService.class).to(DefaultCacheInvalidationService.class)
    }

    static class DefaultCacheEnabledService implements ICacheEnabledService {
        @Override
        boolean isCacheEnabled() {
            return true
        }

        @Override
        void setCacheEnabled(Boolean enabled) {}

        @Override
        void setCacheEnabled(CacheDisableReason reason, Boolean enabled) {}

        @Override
        CacheDisableReason getDisableReason() {
            return NONE;
        }
    }
    
    static class DefaultCacheInvalidationService implements ICacheInvalidationService {

        @Override
        void setCache(QueryCache cache) {

        }

        @Override
        void init() {

        }
    }
}
