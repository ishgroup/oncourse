/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.util;

import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.tapestry.IWillowComponentRequestSelectorAnalyzer;
import org.apache.tapestry5.internal.services.PageLoader;
import org.apache.tapestry5.internal.structure.Page;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.Response;
import org.apache.tapestry5.services.pageload.ComponentResourceSelector;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * User: akoiro
 * Date: 3/3/18
 */
public class PageRendererTest {
	@Test
	public void test_encodedPage() {
		Request request = Mockito.mock(Request.class);
		Response response = Mockito.mock(Response.class);

		Map<String, Object> params = new HashMap<>();
		params.put("param1", "param1");
		params.put("param2", "param2");
		params.put("param3", "param3");

		RequestGlobals requestGlobals = Mockito.mock(RequestGlobals.class);
		Mockito.when(requestGlobals.getRequest()).thenReturn(request);
		Mockito.when(requestGlobals.getResponse()).thenReturn(response);

		Page page = Mockito.mock(Page.class);
		PageLoader pageLoader = Mockito.mock(PageLoader.class);

		ComponentResourceSelector selector = new ComponentResourceSelector(Locale.getDefault()).withAxis(Map.class, params);
		IWillowComponentRequestSelectorAnalyzer analyzer = Mockito.mock(IWillowComponentRequestSelectorAnalyzer.class);
		Mockito.when(analyzer.buildSelectorForRequest()).thenReturn(selector);
		Mockito.when(analyzer.buildSelectorForRequest(Mockito.anyMap())).thenReturn(selector);

		Mockito.when(pageLoader.loadPage("test", selector)).thenReturn(page);

		IComponentPageResponseRenderer pageResponseRenderer = Mockito.mock(IComponentPageResponseRenderer.class);

		PageRenderer pageRenderer = new PageRenderer(pageLoader, requestGlobals, pageResponseRenderer, analyzer);

		pageRenderer.encodedPage("test", params);

		//verify that CUSTOM_TEMPLATE_DEFINITION attribute is reset
		Mockito.verify(request).setAttribute(TextileUtil.CUSTOM_TEMPLATE_DEFINITION, null);
		//verify that all parameters have been copied to request,
		//they are used in correspondent java-tapestry implementation
		Mockito.verify(request).setAttribute("param1", "param1");
		Mockito.verify(request).setAttribute("param2", "param2");
		Mockito.verify(request).setAttribute("param3", "param3");

		//verify that select contains all properties which this PageRenderer got,
		//this ComponentResourceSelector will be used to build cache key for template
		Mockito.verify(pageLoader).loadPage("test", selector);

		try {
			Mockito.verify(pageResponseRenderer).renderPageResponse(page);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testEncode() {
		Assert.assertEquals("&lt;", PageRenderer.encode("<").toString());
		Assert.assertEquals("&gt;", PageRenderer.encode(">").toString());
		Assert.assertEquals("&amp;", PageRenderer.encode("&").toString());
		Assert.assertEquals("&quot;", PageRenderer.encode("\"").toString());
		Assert.assertEquals("&#39;\'", PageRenderer.encode("\'").toString());
		Assert.assertEquals("qwertyuiopasdfghjklzxcvbnm", PageRenderer.encode("qwertyuiopasdfghjklzxcvbnm").toString());
	}

}
