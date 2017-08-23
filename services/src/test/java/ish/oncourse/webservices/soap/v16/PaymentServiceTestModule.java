package ish.oncourse.webservices.soap.v16;

import ish.oncourse.services.paymentexpress.INewPaymentGatewayService;
import ish.oncourse.services.paymentexpress.INewPaymentGatewayServiceBuilder;
import ish.oncourse.services.paymentexpress.IPaymentGatewayService;
import ish.oncourse.services.paymentexpress.IPaymentGatewayServiceBuilder;
import ish.oncourse.webservices.soap.CommonTestModule;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Scope;

/**
 * Own services module real-services setup.
 * @author vdavidovich
 *
 */
public class PaymentServiceTestModule {
	public static void bind(ServiceBinder binder) {
		CommonTestModule.bind(binder);
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
