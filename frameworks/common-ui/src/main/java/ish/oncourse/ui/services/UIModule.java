package ish.oncourse.ui.services;

import ish.oncourse.model.services.persistence.ICayenneService;
import ish.oncourse.services.resource.PrivateResource;
import ish.oncourse.ui.services.filter.LogFilter;
import ish.oncourse.ui.services.locale.PerSiteVariantThreadLocale;
import ish.oncourse.ui.template.T5FileResource;

import org.apache.log4j.Logger;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.internal.pageload.ComponentAssemblerSource;
import org.apache.tapestry5.internal.pageload.ComponentTemplateSourceOverride;
import org.apache.tapestry5.internal.pageload.PageLoaderOverride;
import org.apache.tapestry5.internal.pageload.PageSourceOverride;
import org.apache.tapestry5.internal.pageload.RequestPageCacheOverride;
import org.apache.tapestry5.internal.services.ComponentTemplateSource;
import org.apache.tapestry5.internal.services.PageLoader;
import org.apache.tapestry5.internal.services.PageSource;
import org.apache.tapestry5.internal.services.RequestPageCache;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.ioc.annotations.Local;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.ioc.services.Coercion;
import org.apache.tapestry5.ioc.services.CoercionTuple;
import org.apache.tapestry5.ioc.services.ThreadLocale;
import org.apache.tapestry5.services.LibraryMapping;
import org.apache.tapestry5.services.RequestFilter;
import org.apache.tapestry5.services.RequestGlobals;

import com.howardlewisship.tapx.core.services.CoreModule;

/**
 * A Tapestry IoC module definition of the common components library.
 */
@SubModule({ CoreModule.class })
public class UIModule {

	private static final Logger LOGGER = Logger.getLogger(UIModule.class);

	public static void bind(ServiceBinder binder) {

		binder.bind(ThreadLocale.class, PerSiteVariantThreadLocale.class)
				.withId("Override");

		binder.bind(ComponentTemplateSource.class,
				ComponentTemplateSourceOverride.class).withId(
				"ComponentTemplateSourceOverride");

		binder.bind(ComponentAssemblerSource.class, PageLoaderOverride.class)
				.withId("ComponentAssemblerSourceOverride");

		binder.bind(PageLoader.class, PageLoaderOverride.class).withId(
				"PageLoaderOverride");

		binder.bind(RequestPageCache.class, RequestPageCacheOverride.class)
				.withId("NonPoolingRequestPageCacheImplOverride");

		binder.bind(PageSource.class, PageSourceOverride.class).withId(
				"PageSourceServiceOverride");

	}

	public void contributeServiceOverride(
			MappedConfiguration<Class<?>, Object> configuration,
			@Local PageSource override) {
		configuration.add(PageSource.class, override);
	}

	public void contributeServiceOverride(
			MappedConfiguration<Class<?>, Object> configuration,
			@Local RequestPageCache override) {
		configuration.add(RequestPageCache.class, override);
	}

	public void contributeServiceOverride(
			MappedConfiguration<Class<?>, Object> configuration,
			@Local ThreadLocale override) {
		configuration.add(ThreadLocale.class, override);
	}

	public void contributeServiceOverride(
			MappedConfiguration<Class<?>, Object> configuration,
			@Local ComponentTemplateSource componentTemplateSourceOverride) {
		configuration.add(ComponentTemplateSource.class,
				componentTemplateSourceOverride);
	}

	public void contributeServiceOverride(
			MappedConfiguration<Class<?>, Object> configuration,
			@Local ComponentAssemblerSource override) {
		configuration.add(ComponentAssemblerSource.class, override);
	}

	public void contributeServiceOverride(
			MappedConfiguration<Class<?>, Object> configuration,
			@Local PageLoader override) {
		configuration.add(PageLoader.class, override);
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
