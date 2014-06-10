package ish.oncourse.enrol.services;

import ish.oncourse.enrol.services.concessions.ConcessionsService;
import ish.oncourse.enrol.services.concessions.IConcessionsService;
import ish.oncourse.enrol.services.invoice.IInvoiceProcessingService;
import ish.oncourse.enrol.services.invoice.InvoiceProcessingService;
import ish.oncourse.enrol.services.linktransform.EnrolPageLinkTransformer;
import ish.oncourse.enrol.services.payment.IPurchaseControllerBuilder;
import ish.oncourse.enrol.services.payment.PurchaseControllerBuilder;
import ish.oncourse.enrol.services.student.IStudentService;
import ish.oncourse.enrol.services.student.StudentService;
import ish.oncourse.model.services.ModelModule;
import ish.oncourse.services.ServiceModule;
import ish.oncourse.services.jmx.IJMXInitService;
import ish.oncourse.services.jmx.JMXInitService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.site.IWebSiteVersionService;
import ish.oncourse.services.site.WebSiteVersionService;
import ish.oncourse.ui.services.UIModule;
import ish.oncourse.ui.services.locale.PerSiteVariantThreadLocale;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.*;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;
import org.apache.tapestry5.ioc.services.ThreadLocale;
import org.apache.tapestry5.services.ApplicationGlobals;
import org.apache.tapestry5.services.linktransform.PageRenderLinkTransformer;

/**
 * The module that is automatically included as part of the Tapestry IoC
 * registry.
 */
@SubModule({ModelModule.class, ServiceModule.class, UIModule.class})
public class AppModule {

	public static void bind(ServiceBinder binder) {
		binder.bind(IConcessionsService.class, ConcessionsService.class);
		binder.bind(IStudentService.class, StudentService.class);
		binder.bind(IInvoiceProcessingService.class, InvoiceProcessingService.class);
		binder.bind(IPurchaseControllerBuilder.class, PurchaseControllerBuilder.class);
		binder.bind(IWebSiteVersionService.class, WebSiteVersionService.class);
	}

	@EagerLoad
	public static IJMXInitService buildJMXInitService(ApplicationGlobals applicationGlobals, RegistryShutdownHub hub) {
		JMXInitService jmxService = new JMXInitService(applicationGlobals, "enrol", "ish.oncourse:type=EnrolApplicationData");
		hub.addRegistryShutdownListener(jmxService);
		return jmxService;
	}

	public static void contributeApplicationDefaults(MappedConfiguration<String, String> configuration) {
		configuration.add(SymbolConstants.SECURE_ENABLED, "true");
	}

	public ThreadLocale buildThreadLocaleOverride(IWebSiteService webSiteService) {
		return new PerSiteVariantThreadLocale(webSiteService);
	}

	public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration, @Local ThreadLocale locale) {
		configuration.add(ThreadLocale.class, locale);
	}

	@Contribute(PageRenderLinkTransformer.class)
	@Primary
	public static void provideURLRewriting(OrderedConfiguration<PageRenderLinkTransformer> configuration) {
		configuration.addInstance("PageLinkRule", EnrolPageLinkTransformer.class);
	}

}
