/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.cache.caffeine;

import com.github.benmanes.caffeine.jcache.configuration.CaffeineConfiguration;
import ish.oncourse.cache.ACacheFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.cache.CacheManager;
import javax.cache.configuration.Configuration;
import java.util.OptionalLong;
import java.util.concurrent.TimeUnit;

import static ish.oncourse.cache.Constants.DEFAULT_ENTRIES_AMOUNT;
import static ish.oncourse.cache.Constants.DEFAULT_TTL;

/**
 * User: akoiro
 * Date: 31/3/18
 */
public class CaffeineFactory<K, V> extends ACacheFactory<K, V> {
	private static final Logger logger = LogManager.getLogger();

	CaffeineFactory(CacheManager cacheManager, Class<K> keyClass, Class<V> valueClass, Configuration<K, V> defaultConfig) {
		super(cacheManager, keyClass, valueClass, defaultConfig);
	}

	CaffeineFactory(CacheManager cacheManager, Class<K> keyClass, Class<V> valueClass) {
		this(cacheManager, keyClass, valueClass, createDefaultConfig(keyClass, valueClass));
	}

	public static <K, V> Configuration<K, V> createDefaultConfig(Class<K> keyClass, Class<V> valueClass) {
		return createDefaultConfig(keyClass, valueClass, DEFAULT_ENTRIES_AMOUNT);
	}

	public static <K, V> CaffeineConfiguration<K, V> createDefaultConfig(Class<K> keyClass, Class<V> valueClass, long maximumSize) {
		CaffeineConfiguration<K, V> config = new CaffeineConfiguration<>();
		config.setMaximumSize(OptionalLong.of(maximumSize));
		config.setManagementEnabled(true);
		config.setStatisticsEnabled(true);
		config.setExpireAfterWrite(OptionalLong.of(TimeUnit.MINUTES.toNanos(DEFAULT_TTL)));
		config.setTypes(keyClass, valueClass);
		return config;
	}

}
