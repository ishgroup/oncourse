package ish.oncourse.cms.services;

import ish.oncourse.cms.services.access.AuthenticationService;
import ish.oncourse.cms.services.access.IAuthenticationService;
import ish.oncourse.cms.services.access.PageAccessDispatcher;
import ish.oncourse.cms.services.site.CMSWebSiteVersionService;
import ish.oncourse.linktransform.PageLinkTransformer;
import ish.oncourse.model.services.ModelModule;
import ish.oncourse.services.ServiceModule;
import ish.oncourse.services.cache.IRequestCacheService;
import ish.oncourse.services.cache.RequestCached;
import ish.oncourse.services.jmx.IJMXInitService;
import ish.oncourse.services.jmx.JMXInitService;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.node.IWebNodeTypeService;
import ish.oncourse.services.resource.IResourceService;
import ish.oncourse.services.resource.Resource;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.site.IWebSiteVersionService;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.ui.components.internal.ContentStructure;
import ish.oncourse.ui.components.internal.PageStructure;
import ish.oncourse.ui.pages.internal.Page;
import ish.oncourse.ui.services.UIModule;
import ish.oncourse.ui.services.locale.PerSiteVariantThreadLocale;
import org.apache.tapestry5.MetaDataConstants;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.*;
import org.apache.tapestry5.ioc.annotations.*;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;
import org.apache.tapestry5.ioc.services.ThreadLocale;
import org.apache.tapestry5.services.ApplicationGlobals;
import org.apache.tapestry5.services.Dispatcher;
import org.apache.tapestry5.services.linktransform.PageRenderLinkTransformer;

import java.lang.reflect.Method;

/**
 * The module that is automatically included as part of the Tapestry IoC
 * registry.
 */
@SubModule({ ModelModule.class, ServiceModule.class, UIModule.class })
		
public class AppModule {

	private static final String PAGE_TEMPLATE = Page.class.getSimpleName() + ".tml";
	private static final String PAGE_STRUCTURE_TEMPLATE = PageStructure.class.getSimpleName() + ".tml";
	private static final String CONTENT_STRUCTURE_TEMPLATE = ContentStructure.class.getSimpleName() + ".tml";

	public static void bind(ServiceBinder binder) {
		binder.bind(IAuthenticationService.class, AuthenticationService.class);
		binder.bind(PageAccessDispatcher.class).withId("PageAccessDispatcher");
		binder.bind(IWebSiteVersionService.class, CMSWebSiteVersionService.class);
	}

	public static void contributeApplicationDefaults(
			MappedConfiguration<String, String> configuration) {

		configuration.add(SymbolConstants.PRODUCTION_MODE, "false");
		configuration.add(SymbolConstants.COMPRESS_WHITESPACE, "false");
		configuration.add(SymbolConstants.COMPACT_JSON, "false");
        configuration.add(SymbolConstants.SECURE_ENABLED, "true");
	}

    public void contributeMetaDataLocator(MappedConfiguration<String,String> configuration)
    {
        configuration.add(MetaDataConstants.SECURE_PAGE, "true");
	}
	
	@EagerLoad
	public static IJMXInitService buildJMXInitService(ApplicationGlobals applicationGlobals, RegistryShutdownHub hub) {
		JMXInitService jmxService = new JMXInitService(applicationGlobals,"cms","ish.oncourse:type=CmsApplicationData");
		hub.addRegistryShutdownListener(jmxService);
		return jmxService;
	}

	/**
	 * Contribute access control checker.
	 */
	public void contributeMasterDispatcher(
			OrderedConfiguration<Dispatcher> configuration,
			@InjectService("PageAccessDispatcher") Dispatcher pageAccessDispatcher) {

		configuration.add("PageAccessDispatcher", pageAccessDispatcher,
				"before:PageRender");
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
	
	public IResourceService decorateIResourceService(final IResourceService original) {
		return new IResourceService() {

			public Resource getWebResource(String fileName) {
				return original.getWebResource(fileName);
			}

			public org.apache.tapestry5.ioc.Resource getDbTemplateResource(String layoutKey, String fileName) {
				if (PAGE_TEMPLATE.equalsIgnoreCase(fileName) || PAGE_STRUCTURE_TEMPLATE.equalsIgnoreCase(fileName)
						|| CONTENT_STRUCTURE_TEMPLATE.equalsIgnoreCase(fileName)) {
					return null;
				}
				return original.getDbTemplateResource(layoutKey, fileName);
			}
		};
	}

    //RequestCached annotation handling
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

    @Advise(serviceInterface=ITagService.class)
    public static void adviceTagService(final MethodAdviceReceiver receiver, @Inject final IRequestCacheService requestCacheService)
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
