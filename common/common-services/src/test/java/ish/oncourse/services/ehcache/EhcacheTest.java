/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.ehcache;

import io.reactivex.Observable;
import ish.oncourse.cache.CreateIfAbsent;
import ish.oncourse.cache.ehcache.EhcacheFactory;
import org.apache.commons.lang3.RandomStringUtils;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.junit.Test;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.Configuration;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import static org.ehcache.config.ResourceType.Core.HEAP;

/**
 * User: akoiro
 * Date: 31/3/18
 */
public class EhcacheTest {
	@Test
	public void test() throws URISyntaxException, InterruptedException {
		CacheManager manager = Caching.getCachingProvider()
				.getCacheManager(getClass().getResource("ehcache.xml").toURI(),
						getClass().getClassLoader());


		CreateIfAbsent<String, String> createIfAbsent = new CreateIfAbsent<>(manager, String.class, String.class);

		Configuration<String, String> configuration = EhcacheFactory.createDefaultConfig(String.class, String.class);

		Cache<String, String> cache = createIfAbsent.createIfAbsent("test", configuration);
		Cache<String, String> cache1 = createIfAbsent.createIfAbsent("test", configuration);

		System.out.println(cache1.unwrap(org.ehcache.Cache.class)
				.getRuntimeConfiguration().getResourcePools().getPoolForResource(HEAP).getUnit());

		Observable.interval(1, TimeUnit.MINUTES)
				.flatMap(i -> Observable.range(0, 1000))
				.map(r -> "key" + r)
				.blockingForEach(k -> cache.put(k, RandomStringUtils.randomAlphabetic(1000000)));
	}

	@Test
	public void test_ehcache() throws URISyntaxException, InterruptedException {
		CacheManager manager = Caching.getCachingProvider()
				.getCacheManager(getClass().getResource("ehcache.xml").toURI(),
						getClass().getClassLoader());

		org.ehcache.CacheManager eManager = manager.unwrap(org.ehcache.CacheManager.class);

		org.ehcache.Cache<String, String> cache0 = eManager.createCache("cache0",
				CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, String.class,
						ResourcePoolsBuilder.newResourcePoolsBuilder().heap(500, EntryUnit.ENTRIES).build()
				)
		);

		org.ehcache.Cache<String, String> cache1 = eManager.createCache("cache1",
				CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, String.class,
						ResourcePoolsBuilder.newResourcePoolsBuilder().heap(500, EntryUnit.ENTRIES).build()
				)
		);

		Observable.interval(0, 1, TimeUnit.MINUTES)
				.flatMap(i -> Observable.range(0, 1000))
				.map(r -> "key" + r)
				.subscribe(k -> cache0.put(k, RandomStringUtils.randomAlphabetic(1000000)));


		Observable.interval(0, 1, TimeUnit.MINUTES)
				.flatMap(i -> Observable.range(0, 1000))
				.map(r -> "key" + r)
				.subscribe(k -> cache1.put(k, RandomStringUtils.randomAlphabetic(1000000)));


		while (true) {
			Thread.sleep(100);
		}

	}

}
