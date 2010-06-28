package ish.oncourse.ui.services;

import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.resource.PrivateResource;
import ish.oncourse.ui.services.filter.LogFilter;
import ish.oncourse.ui.services.locale.PerSiteVariantThreadLocale;
import ish.oncourse.ui.services.template.T5FileResource;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.ioc.annotations.Local;
import org.apache.tapestry5.ioc.services.Coercion;
import org.apache.tapestry5.ioc.services.CoercionTuple;
import org.apache.tapestry5.ioc.services.ThreadLocale;
import org.apache.tapestry5.services.LibraryMapping;
import org.apache.tapestry5.services.RequestFilter;
import org.apache.tapestry5.services.RequestGlobals;
import org.slf4j.Logger;

/**
 * A Tapestry IoC module definition of the common components library.
 */
public class UIModule {

	public static void bind(ServiceBinder binder) {
		binder.bind(ThreadLocale.class, PerSiteVariantThreadLocale.class)
				.withId("Override");
	}

	public void contributeServiceOverride(
			MappedConfiguration<Class<?>, Object> configuration,
			@Local ThreadLocale override) {
		configuration.add(ThreadLocale.class, override);
	}
	
	public void contributeComponentClassResolver(
			Configuration<LibraryMapping> configuration) {
		configuration.add(new LibraryMapping("ui", "ish.oncourse.ui"));
	}

	public void contributeApplicationDefaults(
			MappedConfiguration<String, String> configuration,
			ICayenneService cayenneService) {

		// ensure Tapestry does not advertise itself on our pages...
		configuration.add(SymbolConstants.OMIT_GENERATOR_META, "true");

		// this is overridden in other palces anyways, as we are using locale
		// variant for site template customization
		configuration.add(SymbolConstants.SUPPORTED_LOCALES, "en");
	}

	public void contributeRequestHandler(
			OrderedConfiguration<RequestFilter> configuration,
			@InjectService("LogFilter") RequestFilter logFilter) {
		configuration.add("LogFilter", logFilter);
	}

	public RequestFilter buildLogFilter(Logger log,
			RequestGlobals requestGlobals) {
		return new LogFilter(log, requestGlobals);
	}

	public static void contributeTypeCoercer(
			Configuration<CoercionTuple> configuration) {
		configuration.add(new CoercionTuple<PrivateResource, Resource>(
				PrivateResource.class, Resource.class,
				new Coercion<PrivateResource, Resource>() {
					public Resource coerce(PrivateResource input) {
						return new T5FileResource(input.getFile());
					}
				}));
	}
}


