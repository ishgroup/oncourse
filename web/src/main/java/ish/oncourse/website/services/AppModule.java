/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.website.services;

import ish.oncourse.linktransform.PageLinkTransformer;
import ish.oncourse.services.BinderFunctions;
import ish.oncourse.services.DisableJavaScriptStack;
import ish.oncourse.services.cache.IRequestCacheService;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.node.IWebNodeTypeService;
import ish.oncourse.services.search.SearchService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.site.IWebSiteVersionService;
import ish.oncourse.services.site.WebSiteService;
import ish.oncourse.services.visitor.ParsedContentVisitor;
import ish.oncourse.ui.services.UIModule;
import ish.oncourse.ui.services.locale.PerSiteVariantThreadLocale;
import ish.oncourse.website.services.html.CacheMetaProvider;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.internal.InternalConstants;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.MethodAdviceReceiver;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.*;
import org.apache.tapestry5.ioc.services.ThreadLocale;
import org.apache.tapestry5.services.javascript.JavaScriptStack;
import org.apache.tapestry5.services.javascript.JavaScriptStackSource;
import org.apache.tapestry5.services.linktransform.PageRenderLinkTransformer;

import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * The module that is automatically included as part of the Tapestry IoC
 * registry.
 */
@ImportModule({UIModule.class})
public class AppModule {

	private static final String HMAC_PASSPHRASE = "807A760F20C70F8C9E0ACD8D955EA05399E501E5";

	public static void bind(ServiceBinder binder) {
		BinderFunctions.bindReferenceServices(binder);
		BinderFunctions.bindEntityServices(binder);
		BinderFunctions.bindWebSiteServices(binder, WebSiteService.class);
		BinderFunctions.bindPaymentGatewayServices(binder);
		BinderFunctions.bindEnvServices(binder, "portal", false);
		BinderFunctions.bindTapestryServices(binder, CacheMetaProvider.class);
	}

	public static void contributeApplicationDefaults(
			MappedConfiguration<String, String> configuration) {
		configuration.add(SymbolConstants.HMAC_PASSPHRASE, HMAC_PASSPHRASE);
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

	@Decorate(serviceInterface = ThreadLocale.class)
	public static ThreadLocale threadLocaleDecorate(ThreadLocale threadLocale, IWebSiteService webSiteService) {
		return new PerSiteVariantThreadLocale(webSiteService);
	}

	@Contribute(JavaScriptStackSource.class)
	public static void deactiveJavaScript(MappedConfiguration<String, JavaScriptStack> configuration) {
		configuration.overrideInstance(InternalConstants.CORE_STACK_NAME, DisableJavaScriptStack.class);
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
