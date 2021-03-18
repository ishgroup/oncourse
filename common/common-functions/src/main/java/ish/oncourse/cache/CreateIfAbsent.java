/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.cache;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.configuration.Configuration;
import java.util.concurrent.atomic.AtomicBoolean;

public class CreateIfAbsent<K, V> {

	private final CacheManager cacheManager;
	private Class<K> keyType;
	private Class<V> valueType;
	private final AtomicBoolean busy = new AtomicBoolean(false);

	public CreateIfAbsent(CacheManager cacheManager,
						  Class<K> keyType,
						  Class<V> valueType) {
		this.cacheManager = cacheManager;
		this.keyType = keyType;
		this.valueType = valueType;
	}

	public Cache<K, V> createIfAbsent(String name,
									  Configuration<K, V> config)

	{
		while (!busy.compareAndSet(false, true)) ;
		Cache<K, V> cache = cacheManager.getCache(name, keyType, valueType);
		if (cache == null)
			cache = cacheManager.createCache(name, config);
		busy.set(false);
		return cache;
	}

}
