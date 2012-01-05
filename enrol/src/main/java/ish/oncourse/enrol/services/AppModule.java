package ish.oncourse.enrol.services;

import ish.oncourse.enrol.services.concessions.ConcessionsService;
import ish.oncourse.enrol.services.concessions.IConcessionsService;
import ish.oncourse.enrol.services.invoice.IInvoiceProcessingService;
import ish.oncourse.enrol.services.invoice.InvoiceProcessingService;
import ish.oncourse.enrol.services.quartz.QuartzInitializer;
import ish.oncourse.enrol.services.student.IStudentService;
import ish.oncourse.enrol.services.student.StudentService;
import ish.oncourse.model.PaymentGatewayType;
import ish.oncourse.model.services.ModelModule;
import ish.oncourse.services.ServiceModule;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.ui.services.UIModule;
import ish.oncourse.ui.services.locale.PerSiteVariantThreadLocale;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.MetaDataConstants;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.ServiceBuilder;
import org.apache.tapestry5.ioc.ServiceResources;
import org.apache.tapestry5.ioc.annotations.Local;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;
import org.apache.tapestry5.ioc.services.ThreadLocale;
import org.apache.tapestry5.services.MetaDataLocator;

/**
 * The module that is automatically included as part of the Tapestry IoC
 * registry.
 */
@SubModule({ ModelModule.class, ServiceModule.class, UIModule.class })
public class AppModule {

	public static void bind(ServiceBinder binder) {
		binder.bind(IConcessionsService.class, ConcessionsService.class);
		binder.bind(IStudentService.class, StudentService.class);
		binder.bind(IInvoiceProcessingService.class, InvoiceProcessingService.class);
		binder.bind(QuartzInitializer.class, new ServiceBuilder<QuartzInitializer>() {
			@Override
			public QuartzInitializer buildService(ServiceResources res) {
				QuartzInitializer quartzInit = new QuartzInitializer(res);
				RegistryShutdownHub hub = res.getService(RegistryShutdownHub.class);
				hub.addRegistryShutdownListener(quartzInit);
				return quartzInit;
			}
		}).eagerLoad();
	}

	public void contributeMetaDataLocator(MappedConfiguration<String, String> configuration) {
		configuration.add(MetaDataConstants.SECURE_PAGE, "true");
	}

	public MetaDataLocator decorateMetaDataLocator(final MetaDataLocator original, final PreferenceController preferenceController) {
		return new MetaDataLocator() {
			
			@Override
			public <T> T findMeta(String key, String pageName, Class<T> expectedType) {
				
				if (MetaDataConstants.SECURE_PAGE.equals(key)) {
					
					// checks if the request should be secured
					boolean isSecured = PaymentGatewayType.SECURED_TYPES.contains(preferenceController
							.getPaymentGatewayType()) && pageName.toLowerCase().equals("enrolcourses");
					
					return (T) Boolean.valueOf(isSecured);
				}
				
				return original.findMeta(key, pageName, expectedType);
			}

			@Override
			public <T> T findMeta(String key, ComponentResources resources, Class<T> expectedType) {
				return original.findMeta(key, resources, expectedType);
			}

		};
	}

	public ThreadLocale buildThreadLocaleOverride(IWebSiteService webSiteService) {
		return new PerSiteVariantThreadLocale(webSiteService);
	}

	public void contributeServiceOverride(MappedConfiguration<Class<?>, Object> configuration, @Local ThreadLocale locale) {
		configuration.add(ThreadLocale.class, locale);
	}
}
