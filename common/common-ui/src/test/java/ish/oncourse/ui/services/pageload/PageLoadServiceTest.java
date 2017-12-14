/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.ui.services.pageload;

import ish.oncourse.cayenne.cache.JCacheDefaultConfigurationFactory.GetOrCreateCache;
import org.apache.tapestry5.internal.parser.ComponentTemplate;
import org.apache.tapestry5.internal.util.MultiKey;
import org.junit.Assert;
import org.junit.Test;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;

import static ish.oncourse.ui.services.pageload.PageLoadService.CacheKey.templates;

/**
 * User: akoiro
 * Date: 14/12/17
 */
public class PageLoadServiceTest  {

	@Test
	public void testClearCache() {
		CacheManager cacheManager = Caching.getCachingProvider().getCacheManager();
		Cache<MultiKey, ComponentTemplate> cache = new GetOrCreateCache(cacheManager).getOrCreate(templates.getCacheName("test"), MultiKey.class, ComponentTemplate.class);
		MultiKey multiKey = new MultiKey();
		cache.put(multiKey, PageLoadService.missingTemplate);
		cacheManager.destroyCache(templates.getCacheName("test"));
		Assert.assertNull(cacheManager.getCache(templates.getCacheName("test")));

		new GetOrCreateCache(cacheManager).getOrCreate(templates.getCacheName("test"), MultiKey.class, ComponentTemplate.class);
		Assert.assertNotNull(cacheManager.getCache(templates.getCacheName("test"), MultiKey.class, ComponentTemplate.class));

	}
}
