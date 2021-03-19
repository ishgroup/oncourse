/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.ui.services.pageload;

import com.github.benmanes.caffeine.jcache.spi.CaffeineCachingProvider;
import ish.oncourse.cache.CreateIfAbsent;
import ish.oncourse.cache.ehcache.EhcacheFactory;
import ish.oncourse.cache.jcache.JCacheFactory;
import ish.oncourse.model.WebSiteLayout;
import org.apache.tapestry5.internal.parser.ComponentTemplate;
import org.apache.tapestry5.internal.util.MultiKey;
import org.ehcache.jsr107.EhcacheCachingProvider;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.Configuration;
import java.util.Locale;

/**
 * User: akoiro
 * Date: 24/11/17
 */
public class MutliKeyTest {

	@Test
	public void test_jcache() {
		CacheManager cacheManager = Caching.getCachingProvider(CaffeineCachingProvider.class.getName()).getCacheManager();

		Locale locale = Locale.getDefault();
		String name = "template1";
		String serverName = "serverName1";
		WebSiteLayout webSiteLayout = new WebSiteLayout();

		MultiKey setMultiKey = new MultiKey(name, locale, serverName, webSiteLayout);
		MultiKey getMultiKey = new MultiKey(name, locale, serverName, webSiteLayout);

		Assert.assertEquals(setMultiKey, getMultiKey);
		Assert.assertEquals(setMultiKey.hashCode(), getMultiKey.hashCode());
		CreateIfAbsent<MultiKey, Object> createIfAbsent = new CreateIfAbsent<>(cacheManager, MultiKey.class, Object.class);
		Configuration<MultiKey, Object> configuration = JCacheFactory.createDefaultConfig(MultiKey.class, Object.class);

		createIfAbsent.createIfAbsent(serverName, configuration);

		ComponentTemplate template = Mockito.mock(ComponentTemplate.class);
		createIfAbsent.createIfAbsent(serverName, configuration).put(setMultiKey, template);

		ComponentTemplate result = (ComponentTemplate) createIfAbsent.createIfAbsent(serverName, configuration).get(getMultiKey);
		Assert.assertEquals(template, result);
	}

	@Test
	public void test_ehcache() {
		CacheManager cacheManager = Caching.getCachingProvider(EhcacheCachingProvider.class.getName()).getCacheManager();

		Locale locale = Locale.getDefault();
		String name = "template1";
		String serverName = "serverName1";
		WebSiteLayout webSiteLayout = new WebSiteLayout();

		MultiKey setMultiKey = new MultiKey(name, locale, serverName, webSiteLayout);
		MultiKey getMultiKey = new MultiKey(name, locale, serverName, webSiteLayout);

		Assert.assertEquals(setMultiKey, getMultiKey);
		Assert.assertEquals(setMultiKey.hashCode(), getMultiKey.hashCode());
		CreateIfAbsent<MultiKey, Object> createIfAbsent = new CreateIfAbsent<>(cacheManager, MultiKey.class, Object.class);
		Configuration<MultiKey, Object> configuration = EhcacheFactory.createDefaultConfig(MultiKey.class, Object.class);

		createIfAbsent.createIfAbsent(serverName, configuration);

		ComponentTemplate template = Mockito.mock(ComponentTemplate.class);
		createIfAbsent.createIfAbsent(serverName, configuration).put(setMultiKey, template);

		ComponentTemplate result = (ComponentTemplate) createIfAbsent.createIfAbsent(serverName, configuration).get(getMultiKey);
		Assert.assertEquals(template, result);
	}


}
