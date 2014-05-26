package ish.oncourse.ui.services;

import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.node.IWebNodeTypeService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.resource.IResourceService;
import ish.oncourse.services.resource.PrivateResource;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.site.IWebSiteVersionService;
import ish.oncourse.textile.services.TextileModule;
import ish.oncourse.ui.services.filter.LogFilter;
import ish.oncourse.ui.template.T5FileResource;
import ish.oncourse.util.UIRequestExceptionHandler;
import org.apache.tapestry5.internal.pageload.ComponentTemplateSourceOverride;
import org.apache.tapestry5.internal.pageload.PageLoaderOverride;
import org.apache.tapestry5.internal.pageload.PageSourceOverride;
import org.apache.tapestry5.internal.services.ComponentTemplateSource;
import org.apache.tapestry5.internal.services.PageLoader;
import org.apache.tapestry5.internal.services.PageSource;
import org.apache.tapestry5.internal.services.TemplateParser;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.ioc.annotations.Autobuild;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.ioc.annotations.Local;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.ioc.services.ClasspathURLConverter;
import org.apache.tapestry5.ioc.services.Coercion;
import org.apache.tapestry5.ioc.services.CoercionTuple;
import org.apache.tapestry5.services.*;
import org.apache.tapestry5.services.templates.ComponentTemplateLocator;

/**
 * A Tapestry IoC module definition of the common components library.
 */
@SubModule(TextileModule.class)
public class UIModule {

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
			IWebNodeService webNodeService,
			Request request) {

		PageSourceOverride service = new PageSourceOverride(pageLoader, webNodeService, request);
		classesInvalidationEventHub.addInvalidationListener(service);

		messagesHub.addInvalidationListener(service);
		templatesHub.addInvalidationListener(service);

		return service;
	}

	public ComponentTemplateSource buildComponentTemplateSourceOverride(TemplateParser parser, ComponentTemplateLocator locator,
			ClasspathURLConverter classpathURLConverter, UpdateListenerHub updateListenerHub, Request request,
			IResourceService resourceService, IWebNodeService webNodeService, IWebNodeTypeService webNodeTypeService,
			ICayenneService cayenneService, IWebSiteService webSiteService, IWebSiteVersionService webSiteVersionService) {

		ComponentTemplateSourceOverride service = new ComponentTemplateSourceOverride(parser, locator, classpathURLConverter, request,
				resourceService, webNodeService, webNodeTypeService, cayenneService, webSiteService, webSiteVersionService);

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

	public void contributeRequestHandler(OrderedConfiguration<RequestFilter> configuration,
			@InjectService("LogFilter") RequestFilter logFilter) {
		configuration.add("LogFilter", logFilter);
	}

	public RequestFilter buildLogFilter(org.slf4j.Logger log, RequestGlobals requestGlobals) {
		return new LogFilter(log, requestGlobals);
	}

	public static void contributeTypeCoercer(Configuration<CoercionTuple<PrivateResource, Resource>> configuration) {
		configuration.add(new CoercionTuple<>(PrivateResource.class, Resource.class,
				new Coercion<PrivateResource, Resource>() {
					public Resource coerce(PrivateResource input) {
						return new T5FileResource(input.getFile());
					}
				}));
	}


    public RequestExceptionHandler buildAppRequestExceptionHandler(ComponentSource componentSource, ResponseRenderer renderer, Request request, 
    	Response response) {
    	return new UIRequestExceptionHandler(componentSource, renderer, request, response, UIRequestExceptionHandler.ERROR_500_PAGE, 
    		UIRequestExceptionHandler.APPLICATION_ROOT_PAGE, true);
    }

    public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration, @Local RequestExceptionHandler handler) {
        configuration.add(RequestExceptionHandler.class, handler);
    }
}
