/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.ui.services.pageload;

import ish.oncourse.services.site.IWebSiteVersionService;
import org.apache.tapestry5.ioc.services.ThreadLocale;
import org.apache.tapestry5.services.pageload.ComponentResourceSelector;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * The class  WillowComponentRequestSelectorAnalyzer builds ComponentResourceSelector
 * which web application uses to figure out cache key for templates elements.
 *
 * It is very important to add appKey to this built ComponentResourceSelector,
 * if it is not added Tapestry will mix templates for different sites.
 *
 * Also very important to add textile parameters, because template for the same textiles
 * but specified with other parameters should be rendered in deferment way
 */
public class WillowComponentRequestSelectorAnalyzerTest {
	private WillowComponentRequestSelectorAnalyzer analyzer;

	@Before
	public void before() {
		ThreadLocale threadLocale = Mockito.mock(ThreadLocale.class);
		Mockito.when(threadLocale.getLocale()).thenReturn(new Locale("us", "US"));

		IWebSiteVersionService webSiteVersionService = Mockito.mock(IWebSiteVersionService.class);
		Mockito.when(webSiteVersionService.getApplicationKey()).thenReturn("web_cce");

		analyzer = new WillowComponentRequestSelectorAnalyzer(threadLocale, webSiteVersionService);
	}


	@Test
	public void test_buildSelectorForRequest() {
		ComponentResourceSelector request = analyzer.buildSelectorForRequest();

		ComponentResourceSelector expected = new ComponentResourceSelector(new Locale("us", "US"))
				.withAxis(Map.class, Collections.singletonMap("appKey", "web_cce"));
		Assert.assertEquals(expected, request);
	}


	@Test
	public void test_buildSelectorForRequest_with_textile_params() {

		HashMap<String, Object> textileParams = new HashMap<>();
		textileParams.put("param1", "value1");
		textileParams.put("param2", "value2");
		textileParams.put("param3", "value3");
		textileParams.put("param4", "value4");


		ComponentResourceSelector request = analyzer.buildSelectorForRequest(textileParams);

		Map<String, Object> expectedMap = new HashMap<>(textileParams);
		expectedMap.put("appKey", "web_cce");
		ComponentResourceSelector expected = new ComponentResourceSelector(new Locale("us", "US"))
				.withAxis(Map.class, expectedMap);
		Assert.assertEquals(expected, request);
	}


}
