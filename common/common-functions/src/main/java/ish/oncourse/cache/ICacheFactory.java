/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.cache;

import javax.cache.Cache;
import javax.cache.configuration.Configuration;

/**
 * There are three implementation of this interface:
 * * JCacheFactory - uses jcache configuration
 * * EhcacheFactory - uses ehcache configuration
 * * CaffeineFactory - uses caffeine configuration
 *
 * To apply one of implementations for willow applications:
 * *
 */
public interface ICacheFactory<K, V> {
	Cache<K, V> createIfAbsent(String cacheGroup, Configuration<K, V> config);

	Cache<K, V> createIfAbsent(String cacheGroup);

	Cache<K, V> getCache(String cacheGroup);
}
