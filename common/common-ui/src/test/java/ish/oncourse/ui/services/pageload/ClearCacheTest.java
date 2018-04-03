/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.ui.services.pageload;

import ish.oncourse.cache.CreateIfAbsent;
import ish.oncourse.cache.ehcache.EhcacheFactory;
import ish.oncourse.cache.jcache.JCacheFactory;
import org.apache.tapestry5.internal.parser.ComponentTemplate;
import org.apache.tapestry5.internal.util.MultiKey;
import org.ehcache.jsr107.EhcacheCachingProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.Configuration;

/**
 * User: akoiro
 * Date: 14/12/17
 */
public class ClearCacheTest {
	private CacheManager cacheManager;
	private Configuration<MultiKey, ComponentTemplate> configuration;
	private CreateIfAbsent<MultiKey, ComponentTemplate> createIfAbsent;

	@Before
	public void before() {
		cacheManager = Caching.getCachingProvider(EhcacheCachingProvider.class.getName()).getCacheManager();
		configuration = JCacheFactory.createDefaultConfig(MultiKey.class, ComponentTemplate.class);
		createIfAbsent = new CreateIfAbsent<>(cacheManager, MultiKey.class, ComponentTemplate.class);
	}

	@Test
	public void test_jcache() {
		Cache<MultiKey, ComponentTemplate> cache = createIfAbsent.createIfAbsent("siteKey", configuration);

		MultiKey multiKey = new MultiKey();
		cache.put(multiKey, PageLoadService.missingTemplate);

		cacheManager.destroyCache("siteKey");
		Assert.assertNull(cacheManager.getCache("siteKey"));

		createIfAbsent.createIfAbsent("siteKey", configuration);
		Assert.assertNotNull(cacheManager.getCache("siteKey", MultiKey.class, ComponentTemplate.class));
	}

	@Test
	public void test_ehcache() {
		configuration = EhcacheFactory.createDefaultConfig(MultiKey.class, ComponentTemplate.class);
		createIfAbsent = new CreateIfAbsent<>(cacheManager, MultiKey.class, ComponentTemplate.class);

		Cache<MultiKey, ComponentTemplate> cache = createIfAbsent.createIfAbsent("siteKey", configuration);
		MultiKey multiKey = new MultiKey();
		cache.put(multiKey, PageLoadService.missingTemplate);

		cacheManager.destroyCache("siteKey");
		Assert.assertNull(cacheManager.getCache("siteKey"));

		createIfAbsent.createIfAbsent("siteKey", configuration);
		Assert.assertNotNull(cacheManager.getCache("siteKey", MultiKey.class, ComponentTemplate.class));
	}

}
