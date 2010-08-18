package ish.oncourse.ui.services;

import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.resource.PrivateResource;
import ish.oncourse.ui.services.filter.LogFilter;
import ish.oncourse.ui.services.locale.PerSiteVariantThreadLocale;
import ish.oncourse.ui.services.template.ComponentTemplateLocatorAdvice;
import ish.oncourse.ui.services.template.T5FileResource;

import java.lang.reflect.Method;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.MethodAdvice;
import org.apache.tapestry5.ioc.MethodAdviceReceiver;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.ioc.annotations.Local;
import org.apache.tapestry5.ioc.annotations.Match;
import org.apache.tapestry5.ioc.services.Coercion;
import org.apache.tapestry5.ioc.services.CoercionTuple;
import org.apache.tapestry5.ioc.services.ThreadLocale;
import org.apache.tapestry5.model.ComponentModel;
import org.apache.tapestry5.services.LibraryMapping;
import org.apache.tapestry5.services.RequestFilter;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.templates.ComponentTemplateLocator;

/**
 * A Tapestry IoC module definition of the common components library.
 */
public class UIModule {

	private static final Logger LOGGER = Logger.getLogger(UIModule.class);

	public static void bind(ServiceBinder binder) {
		binder.bind(ThreadLocale.class, PerSiteVariantThreadLocale.class)
				.withId("Override");
		binder.bind(MethodAdvice.class, ComponentTemplateLocatorAdvice.class)
				.withId("ComponentTemplateLocatorAdvice");
	}

	@Match("ComponentTemplateLocator")
	public static void adviseComponentTemplateSource(
			MethodAdviceReceiver receiver,
			@InjectService(value = "ComponentTemplateLocatorAdvice") MethodAdvice sourceAdvice) {
		try {
			Method method = ComponentTemplateLocator.class.getMethod(
					"locateTemplate", ComponentModel.class, Locale.class);
			receiver.adviseMethod(method, sourceAdvice);
		} catch (Exception e) {
			LOGGER.error("Unable to advise getTemplate method.", e);
			throw new RuntimeException("Unable to advise getTemplate method.",
					e);
		}
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

	public RequestFilter buildLogFilter(org.slf4j.Logger log,
			RequestGlobals requestGlobals) {
		return new LogFilter(log, requestGlobals);
	}

	public static void contributeTypeCoercer(
			Configuration<CoercionTuple<PrivateResource, Resource>> configuration) {
		configuration.add(new CoercionTuple<PrivateResource, Resource>(
				PrivateResource.class, Resource.class,
				new Coercion<PrivateResource, Resource>() {
					public Resource coerce(PrivateResource input) {
						return new T5FileResource(input.getFile());
					}
				}));
	}
}
