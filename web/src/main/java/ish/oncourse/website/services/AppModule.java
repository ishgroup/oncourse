/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.website.services;

import ish.oncourse.linktransform.PageLinkTransformer;
import ish.oncourse.model.services.ModelModule;
import ish.oncourse.services.DisableJavaScriptStack;
import ish.oncourse.services.ServiceModule;
import ish.oncourse.services.cache.IRequestCacheService;
import ish.oncourse.services.cache.RequestCacheService;
import ish.oncourse.services.cache.RequestCached;
import ish.oncourse.services.html.ICacheMetaProvider;
import ish.oncourse.services.jmx.IJMXInitService;
import ish.oncourse.services.jmx.JMXInitService;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.node.IWebNodeTypeService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.site.IWebSiteVersionService;
import ish.oncourse.services.site.WebSiteVersionService;
import ish.oncourse.ui.services.UIModule;
import ish.oncourse.ui.services.locale.PerSiteVariantThreadLocale;
import ish.oncourse.website.services.html.CacheMetaProvider;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.internal.InternalConstants;
import org.apache.tapestry5.ioc.*;
import org.apache.tapestry5.ioc.annotations.*;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;
import org.apache.tapestry5.ioc.services.ThreadLocale;
import org.apache.tapestry5.services.ApplicationGlobals;
import org.apache.tapestry5.services.MarkupRenderer;
import org.apache.tapestry5.services.MarkupRendererFilter;
import org.apache.tapestry5.services.javascript.JavaScriptStack;
import org.apache.tapestry5.services.javascript.JavaScriptStackSource;
import org.apache.tapestry5.services.linktransform.PageRenderLinkTransformer;

import java.lang.reflect.Method;

/**
 * The module that is automatically included as part of the Tapestry IoC
 * registry.
 */
@SubModule({ ModelModule.class, ServiceModule.class, UIModule.class })
public class AppModule {

	public static void bind(ServiceBinder binder) {
		binder.bind(ICacheMetaProvider.class,CacheMetaProvider.class).withId("WebCacheMetaProvider");
		binder.bind(IWebSiteVersionService.class, WebSiteVersionService.class);
        binder.bind(IRequestCacheService.class, RequestCacheService.class);
	}

	public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration, @Local ICacheMetaProvider cacheMetaProvider) {
		configuration.add(ICacheMetaProvider.class, cacheMetaProvider);
	}


	@EagerLoad
	public static IJMXInitService buildJMXInitService(ApplicationGlobals applicationGlobals, RegistryShutdownHub hub) {
		JMXInitService jmxService = new JMXInitService(applicationGlobals,"website","ish.oncourse:type=WebSiteApplicationData");
		hub.addRegistryShutdownListener(jmxService);
		return jmxService;
	}

	public static void contributeApplicationDefaults(
			MappedConfiguration<String, String> configuration) {
		configuration.add(SymbolConstants.PRODUCTION_MODE, "false");
		configuration.add(SymbolConstants.COMPACT_JSON, "false");
		configuration.add(SymbolConstants.COMPRESS_WHITESPACE, "false");
	}

	@Contribute(PageRenderLinkTransformer.class)
	@Primary
	public static void provideURLRewriting(OrderedConfiguration<PageRenderLinkTransformer> configuration) {
		configuration.addInstance("PageLinkRule", PageLinkTransformer.class);
	}

	public ThreadLocale buildThreadLocaleOverride(IWebSiteService webSiteService) {
		return new PerSiteVariantThreadLocale(webSiteService);
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
        applyRequestCachedAdvice(receiver, requestCacheService);
    }

    @Advise(serviceInterface=IWebNodeTypeService.class)
    public static void adviceWebNodeTypeService(final MethodAdviceReceiver receiver, @Inject final IRequestCacheService requestCacheService)
    {
        applyRequestCachedAdvice(receiver, requestCacheService);
    }

    @Advise(serviceInterface=IWebSiteService.class)
    public static void adviceWebSiteService(final MethodAdviceReceiver receiver, @Inject final IRequestCacheService requestCacheService)
    {
        applyRequestCachedAdvice(receiver, requestCacheService);
    }

    @Advise(serviceInterface=IWebSiteVersionService.class)
    public static void adviceWebSiteVersionService(final MethodAdviceReceiver receiver, @Inject final IRequestCacheService requestCacheService)
    {
        applyRequestCachedAdvice(receiver, requestCacheService);
    }


    private static void applyRequestCachedAdvice(final MethodAdviceReceiver receiver, final IRequestCacheService requestCacheService) {
        MethodAdvice advice = new MethodAdvice()
        {
            public void advise(Invocation invocation)
            {
                String key = receiver.getInterface().getName() + '.' + invocation.getMethodName();

                Object result = requestCacheService.getFromRequest(invocation.getResultType(), key);
                if (result == null) {
                    invocation.proceed();
                    requestCacheService.putToRequest(key, invocation.getResult());
                }
                else {
                    invocation.overrideResult(result);
                }

            }
        };

        for (Method m : receiver.getInterface().getMethods())
        {
            if (m.getAnnotation(RequestCached.class) != null)
                receiver.adviseMethod(m, advice);
        }
    }

}
