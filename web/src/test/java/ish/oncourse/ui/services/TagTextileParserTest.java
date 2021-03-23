/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.ui.services;

import ish.oncourse.model.WebSite;
import ish.oncourse.services.DisableJavaScriptStack;
import ish.oncourse.services.cache.IRequestCacheService;
import ish.oncourse.services.cache.RequestCacheService;
import ish.oncourse.services.html.ICacheMetaProvider;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.node.IWebNodeTypeService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.site.IWebSiteVersionService;
import ish.oncourse.services.site.WebSiteService;
import ish.oncourse.services.site.WebSiteVersionService;
import ish.oncourse.services.visitor.ParsedContentVisitor;
import ish.oncourse.test.LoadDataSet;
import ish.oncourse.test.tapestry.ServiceTest;
import ish.oncourse.website.services.html.CacheMetaProvider;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.dom.Document;
import org.apache.tapestry5.internal.InternalConstants;
import org.apache.tapestry5.internal.test.TestableRequest;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.MethodAdviceReceiver;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.*;
import org.apache.tapestry5.ioc.services.ThreadLocale;
import org.apache.tapestry5.services.*;
import org.apache.tapestry5.services.javascript.JavaScriptStack;
import org.apache.tapestry5.services.javascript.JavaScriptStackSource;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;


public class TagTextileParserTest extends ServiceTest {


	private String tags = "<div id=\"tag-tree\">\n" +
			"\t\t\n" +
			"\t\n" +
			"<div class=\"taggroup-1\">\n" +
			"\t\t<ul class=\"courses-list\">\n" +
			"\t\t\t\n" +
			"\t\t\t\t<li class=\"hasChildren parent_tag\">\n" +
			"\t\t\t\t\t<a href=\"/courses/Subjects\">Subjects</a>\n" +
			"\t\t\t\t</li>\n" +
			"\t\t\t\n" +
			"\t\t\t<ul class=\"courses-list-sub\">\n" +
			"\t\t\t\t\n" +
			"\t\t\t\t\t<li class=\"\">\n" +
			"\t<a href=\"/courses/Art\">Art</a>\n" +
			"\t\n" +
			"\t<ul class=\"courses-list-sub2\">\n" +
			"\t\t\n" +
			"\t</ul>\n" +
			"</li>\n" +
			"\t\t\t\t\n" +
			"\t\t\t\t\t<li class=\"active_tag\">\n" +
			"\t<a href=\"/courses/It\">It</a>\n" +
			"\t\n" +
			"\t<ul class=\"courses-list-sub2\">\n" +
			"\t\t\n" +
			"\t</ul>\n" +
			"</li>\n" +
			"\t\t\t\t\n" +
			"\t\t\t\t\t<li class=\"\">\n" +
			"\t<a href=\"/courses/Photos\">Photos</a>\n" +
			"\t\n" +
			"\t<ul class=\"courses-list-sub2\">\n" +
			"\t\t\n" +
			"\t</ul>\n" +
			"</li>\n" +
			"\t\t\t\t\n" +
			"\t\t\t\t\n" +
			"\t\t\t</ul>\n" +
			"\t\t</ul>\n" +
			"\t</div>\n" +
			"\t</div>";

	@Before
	public void setup() {
		initTest("ish.oncourse.ui", "App",  ish.oncourse.ui.services.TestModule.class);
		new LoadDataSet().dataSetFile("ish/oncourse/website/services/TagTextileParserTestDataSet.xml").addReplacement("[null]", null).load(getDataSource());
	}

	@Test
	public void test() {
		ICayenneService cayenneService = getService(ICayenneService.class);
		TestableRequest request = getService(TestableRequest.class);
		WebSite site = ObjectSelect.query(WebSite.class).selectFirst(cayenneService.newContext());
		request.setAttribute(WebSiteService.CURRENT_WEB_SITE, site);
		Document doc = getPageTester().renderPage("TagsTextile");

		assertEquals(tags.replaceAll("\\s+",""), doc.getElementById("tag-tree").toString().replaceAll("\\s+",""));

	}

	@ImportModule({UIModule.class})
	public static class TextileModule {

		public RequestFilter buildLogFilterOverride(org.slf4j.Logger log, RequestGlobals requestGlobals) {
			return new RequestFilter() {
				public boolean service(Request request, Response response, RequestHandler handler) throws IOException {
					return handler.service(request, response);
				}
			};
		}

		public void contributeRequestHandler(OrderedConfiguration<RequestFilter> configuration, @Local RequestFilter logFilter) {
			configuration.override("LogFilter", logFilter);
		}

		public static void bind(ServiceBinder binder) {
			binder.bind(ICacheMetaProvider.class,CacheMetaProvider.class).withId("WebCacheMetaProvider");
			binder.bind(IWebSiteVersionService.class, WebSiteVersionService.class);
			binder.bind(IRequestCacheService.class, RequestCacheService.class);
		}

		public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration, @Local ICacheMetaProvider cacheMetaProvider) {
			configuration.add(ICacheMetaProvider.class, cacheMetaProvider);
		}


		public static void contributeApplicationDefaults(
			MappedConfiguration<String, String> configuration) {
			configuration.add(SymbolConstants.PRODUCTION_MODE, "false");
			configuration.add(SymbolConstants.COMPACT_JSON, "false");
			configuration.add(SymbolConstants.COMPRESS_WHITESPACE, "false");
			configuration.add(ParsedContentVisitor.WEB_CONTENT_CACHE, "false");
		}


		public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration, @Local ThreadLocale locale) {
			configuration.add(ThreadLocale.class, locale);
		}

		@Contribute(MarkupRenderer.class)
		public static void deactiveDefaultCSS(OrderedConfiguration<MarkupRendererFilter> configuration)
		{
			configuration.override("InjectDefaultStyleheet", null);
		}

		@Contribute(JavaScriptStackSource.class)
		public static void deactiveJavaScript(MappedConfiguration<String, JavaScriptStack> configuration)
		{
			configuration.overrideInstance(InternalConstants.CORE_STACK_NAME, DisableJavaScriptStack.class);
		}


		@Advise(serviceInterface=IWebNodeService.class)
		public static void adviceWebNodeService(final MethodAdviceReceiver receiver, @Inject final IRequestCacheService requestCacheService)
		{
			requestCacheService.applyRequestCachedAdvice(receiver);
		}

		@Advise(serviceInterface=IWebNodeTypeService.class)
		public static void adviceWebNodeTypeService(final MethodAdviceReceiver receiver, @Inject final IRequestCacheService requestCacheService)
		{
			requestCacheService.applyRequestCachedAdvice(receiver);
		}

		@Advise(serviceInterface=IWebSiteService.class)
		public static void adviceWebSiteService(final MethodAdviceReceiver receiver, @Inject final IRequestCacheService requestCacheService)
		{
			requestCacheService.applyRequestCachedAdvice(receiver);
		}

		@Advise(serviceInterface=IWebSiteVersionService.class)
		public static void adviceWebSiteVersionService(final MethodAdviceReceiver receiver, @Inject final IRequestCacheService requestCacheService)
		{
			requestCacheService.applyRequestCachedAdvice(receiver);
		}
	}

}
