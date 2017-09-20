package ish.oncourse.cayenne.cache

import javax.cache.configuration.Configuration

interface JCacheConfigurationFactory {
    Configuration<String, List> create(String cacheGroup)
}
