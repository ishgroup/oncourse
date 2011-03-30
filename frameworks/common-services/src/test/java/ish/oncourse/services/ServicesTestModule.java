package ish.oncourse.services;

import ish.oncourse.services.cache.CacheGroup;
import ish.oncourse.services.cache.CachedObjectProvider;
import ish.oncourse.services.cache.ICacheService;

import org.apache.cayenne.cache.QueryCache;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.annotations.Local;

public class ServicesTestModule {

	public ICacheService buildCacheServiceServiceOverride() {
		return new ICacheService() {

			@Override
			public QueryCache cayenneCache() {
				return null;
			}

			@Override
			public <T> T get(String key, CachedObjectProvider<T> objectProvider, CacheGroup... cacheGroups) {
				return null;
			}
		};
	}

	public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration, @Local ICacheService cacheService) {
		configuration.add(ICacheService.class, cacheService);
	}
}
