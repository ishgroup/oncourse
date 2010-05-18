package ish.oncourse.ui.services;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.MethodAdviceReceiver;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.ioc.annotations.Local;
import org.apache.tapestry5.ioc.annotations.Match;
import org.apache.tapestry5.ioc.services.ThreadLocale;
import org.apache.tapestry5.services.LibraryMapping;
import org.apache.tapestry5.services.RequestFilter;
import org.apache.tapestry5.services.RequestGlobals;
import org.slf4j.Logger;

import ish.oncourse.ui.services.filter.LogFilter;
import ish.oncourse.ui.services.locale.PerSiteVariantThreadLocale;
import ish.oncourse.ui.services.template.IComponentTemplateSourceAdvisor;
import ish.oncourse.ui.services.template.PerSiteComponentTemplateSourceAdvisor;

import ish.oncourse.services.persistence.ICayenneService;

/**
 * A Tapestry IoC module definition of the common components library.
 */
public class UIModule {

	public static void bind(ServiceBinder binder) {
		binder.bind(IComponentTemplateSourceAdvisor.class,
				PerSiteComponentTemplateSourceAdvisor.class);
		binder.bind(ThreadLocale.class, PerSiteVariantThreadLocale.class)
				.withId("Override");
	}

	public void contributeServiceOverride(
			MappedConfiguration<Class<?>, Object> configuration,
			@Local ThreadLocale override) {
		configuration.add(ThreadLocale.class, override);
	}

	@Match("ComponentTemplateSource")
	public void adviseGetTemplate(IComponentTemplateSourceAdvisor advisor,
			MethodAdviceReceiver receiver) {
		advisor.advice(receiver);
	};

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
}
