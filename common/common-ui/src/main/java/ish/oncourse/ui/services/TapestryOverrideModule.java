package ish.oncourse.ui.services;

import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.node.IWebNodeTypeService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.resource.IResourceService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.site.IWebSiteVersionService;
import org.apache.tapestry5.internal.pageload.ComponentTemplateSourceOverride;
import org.apache.tapestry5.internal.pageload.PageLoaderOverride;
import org.apache.tapestry5.internal.pageload.PageSourceOverride;
import org.apache.tapestry5.internal.services.ComponentTemplateSource;
import org.apache.tapestry5.internal.services.PageLoader;
import org.apache.tapestry5.internal.services.PageSource;
import org.apache.tapestry5.internal.services.TemplateParser;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.annotations.Autobuild;
import org.apache.tapestry5.ioc.annotations.Local;
import org.apache.tapestry5.ioc.services.ClasspathURLConverter;
import org.apache.tapestry5.services.*;
import org.apache.tapestry5.services.templates.ComponentTemplateLocator;

public class TapestryOverrideModule {

	public PageLoader buildPageLoaderOverride(@Autobuild PageLoaderOverride service, @ComponentTemplates InvalidationEventHub templatesHub,
	                                          @ComponentMessages InvalidationEventHub messagesHub, @ComponentClasses InvalidationEventHub classesInvalidationEventHub,
	                                          IWebNodeService webNodeService,
	                                          Request request) {

		service.setRequest(request);
		classesInvalidationEventHub.addInvalidationListener(service);
		templatesHub.addInvalidationListener(service);
		messagesHub.addInvalidationListener(service);

		service.setWebNodeService(webNodeService);
		return service;
	}

	public PageSource buildPageSourceOverride(PageLoader pageLoader, @ComponentTemplates InvalidationEventHub templatesHub,
	                                          @ComponentMessages InvalidationEventHub messagesHub, @ComponentClasses InvalidationEventHub classesInvalidationEventHub,
	                                          IWebNodeService webNodeService, IWebSiteVersionService webSiteVersionService,
	                                          Request request) {

		PageSourceOverride service = new PageSourceOverride(pageLoader, webNodeService, request, webSiteVersionService);
		classesInvalidationEventHub.addInvalidationListener(service);

		messagesHub.addInvalidationListener(service);
		templatesHub.addInvalidationListener(service);

		return service;
	}

	public ComponentTemplateSource buildComponentTemplateSourceOverride(TemplateParser parser, ComponentTemplateLocator locator,
	                                                                    UpdateListenerHub updateListenerHub, Request request,
	                                                                    IResourceService resourceService, IWebNodeService webNodeService,
	                                                                    ICayenneService cayenneService, IWebSiteService webSiteService, IWebSiteVersionService webSiteVersionService) {

		ComponentTemplateSourceOverride service = new ComponentTemplateSourceOverride(parser, locator, request,
				resourceService, webNodeService, cayenneService, webSiteService, webSiteVersionService);

		updateListenerHub.addUpdateListener(service);

		return service;
	}

	public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration,
	                                      @Local ComponentTemplateSource componentTemplateSourceOverride) {
		configuration.add(ComponentTemplateSource.class, componentTemplateSourceOverride);
	}

	public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration, @Local PageLoader override) {
		configuration.add(PageLoader.class, override);
	}

	public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration, @Local PageSource override) {
		configuration.add(PageSource.class, override);
	}

	public void contributeComponentClassResolver(Configuration<LibraryMapping> configuration) {
		configuration.add(new LibraryMapping("ui", "ish.oncourse.ui"));
	}
}
