package ish.oncourse.webservices.soap.v10;

import ish.oncourse.services.paymentexpress.INewPaymentGatewayService;
import ish.oncourse.services.paymentexpress.INewPaymentGatewayServiceBuilder;
import ish.oncourse.services.paymentexpress.IPaymentGatewayService;
import ish.oncourse.services.paymentexpress.IPaymentGatewayServiceBuilder;
import ish.oncourse.services.persistence.CayenneService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.webservices.soap.CommonTestModule;
import net.sf.ehcache.CacheManager;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.EagerLoad;
import org.apache.tapestry5.ioc.annotations.Scope;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;

/**
 * Own services module real-services setup.
 * @author vdavidovich
 *
 */
public class PaymentServiceTestModule {
	public static void bind(ServiceBinder binder) {

		CommonTestModule.bind(binder);
	}
	
	@EagerLoad
	public static ICayenneService buildCayenneService(RegistryShutdownHub hub, IWebSiteService webSiteService, CacheManager cacheManager) {
		CayenneService cayenneService = new CayenneService(webSiteService, cacheManager);
		hub.addRegistryShutdownListener(cayenneService);
		return cayenneService;
	}
	
	@Scope("perthread")
	public static IPaymentGatewayService buildPaymentGatewayService(IPaymentGatewayServiceBuilder builder) {
		return builder.buildService();
	}

	@Scope("perthread")
	public static INewPaymentGatewayService buildNewPaymentGatewayService(INewPaymentGatewayServiceBuilder builder) {
		return builder.buildService();
	}
}
