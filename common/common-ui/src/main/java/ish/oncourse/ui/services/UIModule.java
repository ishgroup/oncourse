package ish.oncourse.ui.services;

import ish.oncourse.services.resource.PrivateResource;
import ish.oncourse.textile.services.TextileModule;
import ish.oncourse.ui.services.filter.LogFilter;
import ish.oncourse.ui.services.pageload.PageLoadModule;
import ish.oncourse.ui.template.T5FileResource;
import ish.oncourse.util.UIRequestExceptionHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.ioc.annotations.ImportModule;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.ioc.annotations.Local;
import org.apache.tapestry5.ioc.services.Coercion;
import org.apache.tapestry5.ioc.services.CoercionTuple;
import org.apache.tapestry5.services.*;

/**
 * A Tapestry IoC module definition of the common components library.
 */
@ImportModule({TextileModule.class, PageLoadModule.class})
public class UIModule {


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

	/**
	 * The method was introduced to implement '301 redirect' for our application.
	 */
	public void contributeComponentEventResultProcessor(MappedConfiguration<Class, ComponentEventResultProcessor> configuration,
														final Response response) {
		configuration.add(HttpStatusCode.class, (ComponentEventResultProcessor<HttpStatusCode>) value -> {
			if (!value.getLocation().isEmpty())
				response.setHeader("Location", value.getLocation());
			response.sendError(value.getStatusCode(), StringUtils.EMPTY);
		});
	}

	public void contributeComponentClassResolver(Configuration<LibraryMapping> configuration) {
		configuration.add(new LibraryMapping("ui", "ish.oncourse.ui"));
	}
}
