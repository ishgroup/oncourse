/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.content.cache;

import ish.oncourse.cache.ICacheFactory;
import ish.oncourse.cache.ICacheProvider;

import javax.cache.Cache;

/**
 * User: akoiro
 * Date: 8/09/2016
 */
public class ContentCacheService implements IContentCacheService<WillowContentKey, String> {
	private ICacheFactory<WillowContentKey, String> cacheFactory;

	public ContentCacheService(ICacheProvider cacheProvider) {
		this.cacheFactory = cacheProvider.createFactory(WillowContentKey.class, String.class);
	}


	@Override
	public void put(WillowContentKey key, String value) {
		Cache<WillowContentKey, String> cache = cacheFactory.createIfAbsent(key.getElementName());
		cache.put(key, value);
	}

	@Override
	public String get(WillowContentKey key) {
		Cache<WillowContentKey, String> cache = cacheFactory.getCache(key.getElementName());
		return cache != null ? cache.get(key) : null;
	}


	@Override
	public void remove(WillowContentKey key) {
		Cache<WillowContentKey, String> cache = cacheFactory.getCache(key.getElementName());
		if (cache != null) cache.remove(key);
	}
}
