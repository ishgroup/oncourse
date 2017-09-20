package ish.oncourse.cayenne.cache

import javax.cache.configuration.Configuration
import javax.cache.configuration.MutableConfiguration
import javax.cache.expiry.CreatedExpiryPolicy
import javax.cache.expiry.Duration

class JCacheDefaultConfigurationFactory implements JCacheConfigurationFactory {

    private final Configuration<String, List> configuration = new MutableConfiguration<String, List>()
            .setTypes(String.class, List.class)
            .setStoreByValue(false)
            .setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(Duration.TEN_MINUTES))
    
    @Override
    Configuration<String, List> create(String cacheGroup) {
        configuration
    }
}
