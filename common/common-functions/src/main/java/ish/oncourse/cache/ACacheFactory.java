/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.cache;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.configuration.Configuration;


public abstract class ACacheFactory<K, V> implements ICacheFactory<K, V> {
	private final Configuration<K, V> defaultConfig;
	private final CreateIfAbsent<K, V> createIfAbsent;
	protected final CacheManager cacheManager;
	private final Class<K> keyType;
	private final Class<V> valueType;

	protected ACacheFactory(CacheManager cacheManager,
							Class<K> keyType,
							Class<V> valueType,
							Configuration<K, V> defaultConfig) {
		this.cacheManager = cacheManager;
		this.keyType = keyType;
		this.valueType = valueType;
		this.defaultConfig = defaultConfig;
		this.createIfAbsent = new CreateIfAbsent<>(this.cacheManager, keyType, valueType);
	}

	@Override
	public Cache<K, V> createIfAbsent(String cacheGroup) {
		return createIfAbsent(cacheGroup, defaultConfig);
	}

	@Override
	public Cache<K, V> createIfAbsent(String cacheGroup, Configuration<K, V> config) {
		return this.createIfAbsent.createIfAbsent(cacheGroup, config);
	}

	@Override
	public Cache<K, V> getCache(String cacheGroup) {
		return cacheManager.getCache(cacheGroup, keyType, valueType);
	}
}
