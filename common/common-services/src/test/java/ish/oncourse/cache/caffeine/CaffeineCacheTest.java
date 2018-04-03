/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.cache.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.jcache.configuration.CaffeineConfiguration;
import com.github.benmanes.caffeine.jcache.spi.CaffeineCachingProvider;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import java.util.OptionalLong;
import java.util.concurrent.TimeUnit;

/**
 * User: akoiro
 * Date: 2/4/18
 */
public class CaffeineCacheTest {
	@Test
	public void test_evict() throws InterruptedException {
		Cache<String, String> cache = Caffeine.newBuilder()
//				.weakKeys()
//				.weakValues()
				.expireAfterWrite(1, TimeUnit.MINUTES)
				.maximumSize(10)
				.build();


		Disposable disposable = Observable.interval(0, 1, TimeUnit.SECONDS)
				.map(i -> "key" + i)
				.subscribe(k -> cache.put(k, RandomStringUtils.randomAlphabetic(1000000)));


//		Disposable disposable1 = Observable.interval(2, 2, TimeUnit.SECONDS)
//				.map(i -> "key" + i)
//				.subscribe(k -> cache.get(k, (s) -> {
//							System.out.println(k);
//							return null;
//						}
//				));

		while (!disposable.isDisposed()) {
			Thread.sleep(1000);
		}
	}

	@Test
	public void test_jcache() throws InterruptedException {
		CachingProvider cachingProvider = Caching.getCachingProvider(CaffeineCachingProvider.class.getName());
		CacheManager cacheManager = cachingProvider.getCacheManager();

		CaffeineConfiguration<String, String> config = new CaffeineConfiguration<>();
		config.setManagementEnabled(true);
		config.setStatisticsEnabled(true);
		config.setExpireAfterWrite(OptionalLong.of(60L * 1000000000L));
		javax.cache.Cache<String, String> cache = cacheManager.createCache("test", config);
		Disposable disposable = Observable.interval(0, 1, TimeUnit.SECONDS)
				.map(i -> "key" + i)
				.subscribe(k -> cache.put(k, RandomStringUtils.randomAlphabetic(1000000)));
		while (!disposable.isDisposed()) {
			Thread.sleep(1000);
		}


	}
}
