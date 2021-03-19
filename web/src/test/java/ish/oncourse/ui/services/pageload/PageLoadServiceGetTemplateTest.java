/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.ui.services.pageload;

import ish.oncourse.cache.ICacheProvider;
import ish.oncourse.cache.caffeine.CaffeineProvider;
import ish.oncourse.services.site.IWebSiteVersionService;
import ish.oncourse.services.site.WebSiteVersionService;
import org.apache.tapestry5.internal.parser.ComponentTemplate;
import org.apache.tapestry5.internal.services.TemplateParser;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.model.ComponentModel;
import org.apache.tapestry5.services.pageload.ComponentResourceSelector;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;

import javax.cache.CacheManager;
import java.util.List;

/**
 * User: akoiro
 * Date: 3/3/18
 */
@RunWith(Parameterized.class)
public class PageLoadServiceGetTemplateTest {
	private List<ComponentResourceSelector> selectors = TestDataFactory.selectors();
	private ComponentResourceSelector selector;

	private ICacheProvider cacheProvider;
	private CacheManager cacheManager;
	private PageLoadService pageLoadService;
	private TemplateParser templateParser;
	private IWebSiteVersionService webSiteVersionService;

	public PageLoadServiceGetTemplateTest(ComponentResourceSelector selector) {
		this.selector = selector;
	}


	@Parameterized.Parameters
	public static List<ComponentResourceSelector> data() {
		return TestDataFactory.selectors();
	}


	@Before
	public void before() {
		cacheProvider = new CaffeineProvider();
		cacheManager = cacheProvider.getCacheManager();
		webSiteVersionService = TestDataFactory.webSiteVersionService();
		templateParser = TestDataFactory.templateParser();

		pageLoadService = new PageLoadService(null,
				null,
				webSiteVersionService,
				TestDataFactory.webNodeService(),
				TestDataFactory.resourceService(),
				cacheProvider,
				TestDataFactory.componentResourceLocator(),
				null,
				templateParser,
				null);

	}

	@After
	public void after() {
		cacheManager.close();
	}

	@Test
	public void test_getTemplate() {
		Mockito.when(webSiteVersionService.getApplicationKey()).thenReturn(WebSiteVersionService.WEB_PREFIX);
		ComponentModel model = TestDataFactory.componentModel();

		ComponentTemplate expected = pageLoadService.getTemplate(model, selector);
		Mockito.verify(templateParser).parseTemplate(Mockito.any(Resource.class));

		ComponentTemplate second = pageLoadService.getTemplate(model, selector);
		Assert.assertEquals(second, expected);

		Mockito.when(webSiteVersionService.getApplicationKey()).thenReturn(WebSiteVersionService.EDITOR_PREFIX);
		second = pageLoadService.getTemplate(model, selector);
		Assert.assertNotEquals(second, expected);


		Mockito.when(webSiteVersionService.getApplicationKey()).thenReturn(WebSiteVersionService.WEB_PREFIX);
		selectors.stream().filter((s) -> !s.equals(selector)).forEach((selector) -> {
			ComponentTemplate result = pageLoadService.getTemplate(model, selector);
			Assert.assertNotEquals(String.format("%s not equals %s for selector %s", result, PageLoadService.missingTemplate, selector), PageLoadService.missingTemplate, result);
			Assert.assertNotEquals(String.format("%s not equals %s for selector %s", expected, result, selector), expected, result);
		});
	}
}

