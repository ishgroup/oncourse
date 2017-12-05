/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.ui.services;

import ish.oncourse.linktransform.PageLinkTransformer;
import ish.oncourse.services.BinderFunctions;
import ish.oncourse.services.ServiceModule;
import ish.oncourse.services.cache.IRequestCacheService;
import ish.oncourse.services.cache.RequestCacheService;
import ish.oncourse.services.jmx.IJMXInitService;
import ish.oncourse.services.jmx.JMXInitService;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.node.IWebNodeTypeService;
import ish.oncourse.services.search.SearchService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.site.IWebSiteVersionService;
import ish.oncourse.services.site.WebSiteVersionService;
import ish.oncourse.services.visitor.ParsedContentVisitor;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.internal.test.TestableRequest;
import org.apache.tapestry5.internal.test.TestableRequestImpl;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.MethodAdviceReceiver;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.*;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;
import org.apache.tapestry5.services.ApplicationGlobals;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.linktransform.PageRenderLinkTransformer;

import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * The module that is automatically included as part of the Tapestry IoC
 * registry.
 */
@ImportModule({ServiceModule.class, UIModule.class})
public class AppModule {

	public static void bind(ServiceBinder binder) {
		binder.bind(IWebSiteVersionService.class, WebSiteVersionService.class);
		binder.bind(IRequestCacheService.class, RequestCacheService.class);
	}


	@Decorate
	public TestableRequest decorateRequest(TestableRequest request) {
		return new TestableRequestImpl(request.getContextPath()) {
			@Override
			public String getServerName() {
				return "test";
			}
		};
	}



	public static void contributeApplicationDefaults(
			MappedConfiguration<String, String> configuration) {
		configuration.add(SymbolConstants.PRODUCTION_MODE, "false");
		configuration.add(SymbolConstants.COMPACT_JSON, "false");
		configuration.add(SymbolConstants.COMPRESS_WHITESPACE, "false");
		configuration.add(SearchService.ALIAS_SUFFIX_PROPERTY, EMPTY);
		configuration.add(ParsedContentVisitor.WEB_CONTENT_CACHE, "false");
	}

	@Contribute(PageRenderLinkTransformer.class)
	@Primary
	public static void provideURLRewriting(OrderedConfiguration<PageRenderLinkTransformer> configuration) {
		configuration.addInstance("PageLinkRule", PageLinkTransformer.class);
	}

	@Advise(serviceInterface = IWebNodeService.class)
	public static void adviceWebNodeService(final MethodAdviceReceiver receiver, @Inject final IRequestCacheService requestCacheService) {
		requestCacheService.applyRequestCachedAdvice(receiver);
	}

	@Advise(serviceInterface = IWebNodeTypeService.class)
	public static void adviceWebNodeTypeService(final MethodAdviceReceiver receiver, @Inject final IRequestCacheService requestCacheService) {
		requestCacheService.applyRequestCachedAdvice(receiver);
	}

	@Advise(serviceInterface = IWebSiteService.class)
	public static void adviceWebSiteService(final MethodAdviceReceiver receiver, @Inject final IRequestCacheService requestCacheService) {
		requestCacheService.applyRequestCachedAdvice(receiver);
	}

	@Advise(serviceInterface = IWebSiteVersionService.class)
	public static void adviceWebSiteVersionService(final MethodAdviceReceiver receiver, @Inject final IRequestCacheService requestCacheService) {
		requestCacheService.applyRequestCachedAdvice(receiver);
	}


}
