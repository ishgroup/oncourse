/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.ui.services.pageload;

import ish.oncourse.cayenne.cache.JCacheDefaultConfigurationFactory.GetOrCreateCache;
import ish.oncourse.model.WebSiteLayout;
import ish.oncourse.services.BinderFunctions;
import org.apache.tapestry5.internal.parser.ComponentTemplate;
import org.apache.tapestry5.internal.util.MultiKey;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import javax.cache.CacheManager;
import javax.cache.Caching;
import java.util.Locale;

import static ish.oncourse.ui.services.pageload.PageLoadService.TEMPLATES_CACHE_KEY;
import static ish.oncourse.ui.services.pageload.template.GetTemplateKey.multiKey;

/**
 * User: akoiro
 * Date: 24/11/17
 */
public class MutliKeyTest {

	@Test
	public void test() {
		CacheManager cacheManager = Caching.getCachingProvider().getCacheManager();

		Locale locale = Locale.getDefault();
		String name = "template1";
		String serverName = "serverName1";
		WebSiteLayout webSiteLayout = new WebSiteLayout();

		MultiKey setMultiKey = new MultiKey(TEMPLATES_CACHE_KEY, multiKey(name, locale, serverName, webSiteLayout));
		MultiKey getMultiKey = new MultiKey(TEMPLATES_CACHE_KEY, multiKey(name, locale, serverName, webSiteLayout));

		Assert.assertEquals(setMultiKey, getMultiKey);
		Assert.assertEquals(setMultiKey.hashCode(), getMultiKey.hashCode());
		GetOrCreateCache getOrCreateCache = new GetOrCreateCache(cacheManager);
		getOrCreateCache.getOrCreate(serverName, MultiKey.class, ComponentTemplate.class);

		ComponentTemplate template = Mockito.mock(ComponentTemplate.class);


		getOrCreateCache.getOrCreate(serverName, MultiKey.class, ComponentTemplate.class).put(setMultiKey, template);


		ComponentTemplate result = getOrCreateCache.getOrCreate(serverName, MultiKey.class, ComponentTemplate.class).get(getMultiKey);
		Assert.assertEquals(template, result);
	}

}
