/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.cache.ehcache;

import ish.oncourse.cache.ACacheFactory;
import ish.oncourse.cache.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.expiry.Duration;
import org.ehcache.expiry.Expirations;
import org.ehcache.jsr107.Eh107Configuration;

import javax.cache.CacheManager;
import javax.cache.configuration.Configuration;
import java.util.concurrent.TimeUnit;

import static org.ehcache.config.builders.CacheConfigurationBuilder.newCacheConfigurationBuilder;
import static org.ehcache.config.builders.ResourcePoolsBuilder.newResourcePoolsBuilder;

public class EhcacheFactory<K, V> extends ACacheFactory<K, V> {

	private static final Logger logger = LogManager.getLogger();

	protected EhcacheFactory(CacheManager cacheManager, Class<K> keyType, Class<V> valueType) {
		super(cacheManager, keyType, valueType, createDefaultConfig(keyType,
				valueType
		));
	}

	public static <K, V> Configuration<K, V> createDefaultConfig(Class<K> keyType,
																  Class<V> valueType) {
		CacheConfiguration<K, V> config = newCacheConfigurationBuilder(keyType, valueType,
				newResourcePoolsBuilder().heap(Constants.DEFAULT_ENTRIES_AMOUNT, EntryUnit.ENTRIES).build())
				.withExpiry(Expirations.timeToLiveExpiration(Duration.of(Constants.DEFAULT_TTL, TimeUnit.MINUTES)))
				.build();
		return Eh107Configuration.fromEhcacheCacheConfiguration(config);
	}
}
