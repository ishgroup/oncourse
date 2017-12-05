/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.content.cache;

import ish.oncourse.cayenne.cache.JCacheDefaultConfigurationFactory;

import javax.cache.Cache;
import javax.cache.CacheManager;

/**
 * User: akoiro
 * Date: 8/09/2016
 */
public class ContentCacheService implements IContentCacheService<WillowContentKey, String> {
	private final CacheManager cacheManager;

	private final JCacheDefaultConfigurationFactory.GetOrCreateCache getOrCreateCache;

	public ContentCacheService(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
		this.getOrCreateCache = new JCacheDefaultConfigurationFactory.GetOrCreateCache(this.cacheManager);
	}


	@Override
	public void put(WillowContentKey key, String value) {
		Cache<WillowContentKey, String> cache = this.getOrCreateCache.getOrCreate(key.getElementName(), WillowContentKey.class, String.class);
		cache.put(key, value);
	}

	@Override
	public String get(WillowContentKey key) {
		Cache<WillowContentKey, String> cache = cacheManager.getCache(key.getElementName(), WillowContentKey.class, String.class);
		if (cache != null) {
			return cache.get(key);
		}
		return null;
	}


	@Override
	public void remove(WillowContentKey key) {
		Cache<WillowContentKey, String> cache = cacheManager.getCache(key.getElementName(), WillowContentKey.class, String.class);
		if (cache != null) {
			cache.remove(key);
		}
	}
}
