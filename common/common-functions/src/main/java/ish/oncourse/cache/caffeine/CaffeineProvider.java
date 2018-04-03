/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.cache.caffeine;

import com.github.benmanes.caffeine.jcache.spi.CaffeineCachingProvider;
import ish.oncourse.cache.ICacheFactory;
import ish.oncourse.cache.ICacheProvider;

import javax.cache.CacheManager;
import javax.cache.Caching;

/**
 * User: akoiro
 * Date: 31/3/18
 */
public class CaffeineProvider implements ICacheProvider {
	private CacheManager cacheManager;

	public CaffeineProvider() {
		this.cacheManager = Caching.getCachingProvider(CaffeineCachingProvider.class.getName()).getCacheManager();
	}

	@Override
	public <K, V> ICacheFactory<K, V> createFactory(Class<K> keyType, Class<V> valueType) {
		return new CaffeineFactory<>(cacheManager, keyType, valueType);
	}

	@Override
	public CacheManager getCacheManager() {
		return cacheManager;
	}
}