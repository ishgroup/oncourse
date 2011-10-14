package ish.oncourse.cms.services;

import java.io.File;
import java.util.List;

import ish.oncourse.linktransform.PageLinkTransformer;
import ish.oncourse.linktransform.URLRewriteRequestFilter;
import ish.oncourse.model.services.ModelModule;
import ish.oncourse.services.ServiceModule;
import ish.oncourse.services.resource.IResourceService;
import ish.oncourse.services.resource.PrivateResource;
import ish.oncourse.services.resource.Resource;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.ui.components.internal.ContentStructure;
import ish.oncourse.ui.components.internal.PageStructure;
import ish.oncourse.ui.pages.internal.Page;
import ish.oncourse.ui.services.UIModule;
import ish.oncourse.ui.services.locale.PerSiteVariantThreadLocale;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.ioc.annotations.Local;
import org.apache.tapestry5.ioc.annotations.Primary;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.ioc.services.ThreadLocale;
import org.apache.tapestry5.services.Dispatcher;
import org.apache.tapestry5.services.HttpServletRequestFilter;
import org.apache.tapestry5.services.linktransform.PageRenderLinkTransformer;

/**
 * The module that is automatically included as part of the Tapestry IoC
 * registry.
 */
@SubModule({ ModelModule.class, ServiceModule.class, CMSServiceModule.class,
		UIModule.class })
		
public class AppModule {

	public static void contributeApplicationDefaults(
			MappedConfiguration<String, String> configuration) {

		configuration.add(SymbolConstants.PRODUCTION_MODE, "false");
		configuration.add(SymbolConstants.COMPRESS_WHITESPACE, "false");
		configuration.add(SymbolConstants.COMPACT_JSON, "false");
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
			
			public File getCustomComponentRoot() {
				return original.getCustomComponentRoot();
			}

			public Resource getWebResource(String fileName) {
				return original.getWebResource(fileName);
			}

			public PrivateResource getTemplateResource(String layoutKey, String fileName) {
				if (fileName.equalsIgnoreCase(Page.class.getSimpleName() + ".tml")
						|| fileName.equalsIgnoreCase(PageStructure.class.getSimpleName() + ".tml")
						|| fileName.equalsIgnoreCase(ContentStructure.class.getSimpleName()
								+ ".tml")) {
					return null;
				}
				return original.getTemplateResource(layoutKey, fileName);
			}

			public List<PrivateResource> getConfigResources(String fileName) {
				return original.getConfigResources(fileName);
			}
		};
	}
}
