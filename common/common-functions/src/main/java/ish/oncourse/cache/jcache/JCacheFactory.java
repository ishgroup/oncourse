/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.cache.jcache;

import ish.oncourse.cache.ACacheFactory;

import javax.cache.CacheManager;
import javax.cache.configuration.Configuration;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;

/**
 * User: akoiro
 * Date: 3/4/18
 */
public class JCacheFactory<K, V> extends ACacheFactory<K, V> {
	JCacheFactory(CacheManager cacheManager,
				  Class<K> keyType,
				  Class<V> valueType) {
		super(cacheManager, keyType, valueType, createDefaultConfig(keyType, valueType));
	}

	public static <K, V> Configuration<K, V> createDefaultConfig(Class<K> keyType, Class<V> valueType) {
		return new MutableConfiguration<K, V>()
				.setTypes(keyType, valueType)
				.setStoreByValue(false)
				.setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(Duration.TEN_MINUTES))
				.setManagementEnabled(true)
				.setStatisticsEnabled(true);
	}

}
