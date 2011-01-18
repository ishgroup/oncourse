package ish.oncourse.enrol.services;

import ish.oncourse.enrol.services.concessions.ConcessionsService;
import ish.oncourse.enrol.services.concessions.IConcessionsService;
import ish.oncourse.enrol.services.invoice.IInvoiceProcessingService;
import ish.oncourse.enrol.services.invoice.InvoiceProcessingService;
import ish.oncourse.enrol.services.payment.IPaymentGatewayService;
import ish.oncourse.enrol.services.student.IStudentService;
import ish.oncourse.enrol.services.student.StudentService;
import ish.oncourse.enrol.utils.PaymentGatewayServiceBuilder;
import ish.oncourse.model.PaymentGatewayType;
import ish.oncourse.model.services.ModelModule;
import ish.oncourse.services.ServiceModule;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.ui.services.UIModule;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.MetaDataConstants;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ScopeConstants;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.services.MetaDataLocator;

/**
 * The module that is automatically included as part of the Tapestry IoC
 * registry.
 */
@SubModule({ ModelModule.class, ServiceModule.class, UIModule.class })
public class AppModule {
	@Inject
	private IWebSiteService webSiteService;

	public static void bind(ServiceBinder binder) {
		binder.bind(IConcessionsService.class, ConcessionsService.class);
		binder.bind(IStudentService.class, StudentService.class);
		binder.bind(IInvoiceProcessingService.class, InvoiceProcessingService.class);
		binder.bind(IPaymentGatewayService.class, new PaymentGatewayServiceBuilder()).scope(
				ScopeConstants.PERTHREAD);
	}

	public void contributeMetaDataLocator(MappedConfiguration<String, String> configuration) {
		configuration.add(MetaDataConstants.SECURE_PAGE, "true");
	}

	public MetaDataLocator decorateMetaDataLocator(final MetaDataLocator original) {
		return new MetaDataLocator() {
			@Override
			public <T> T findMeta(String key, String pageName, Class<T> expectedType) {
				if (MetaDataConstants.SECURE_PAGE.equals(key)) {
					//checks if the request should be secured
					return (T) (Boolean) PaymentGatewayType.SECURED_TYPES.contains(webSiteService
							.getCurrentCollege().getPaymentGatewayType());
				}
				return original.findMeta(key, pageName, expectedType);
			}

			@Override
			public <T> T findMeta(String key, ComponentResources resources, Class<T> expectedType) {
				return original.findMeta(key, resources, expectedType);
			}

		};
	}

}
