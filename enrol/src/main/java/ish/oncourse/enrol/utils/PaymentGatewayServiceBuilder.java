package ish.oncourse.enrol.utils;

import ish.oncourse.enrol.services.payment.DisabledPaymentGatewayService;
import ish.oncourse.enrol.services.payment.IPaymentGatewayService;
import ish.oncourse.enrol.services.payment.PaymentExpressGatewayService;
import ish.oncourse.enrol.services.payment.TestPaymentGatewayService;
import ish.oncourse.model.College;
import ish.oncourse.services.site.IWebSiteService;

import org.apache.tapestry5.ioc.ServiceBuilder;
import org.apache.tapestry5.ioc.ServiceResources;

public class PaymentGatewayServiceBuilder implements ServiceBuilder<IPaymentGatewayService> {

	/**
	 * Defines the appropriate payment gateway service in dependence on the
	 * college.paymentGatewayType property. {@inheritDoc}
	 * 
	 */
	public IPaymentGatewayService buildService(ServiceResources resources) {
		IWebSiteService service = resources.getService(IWebSiteService.class);
		College currentCollege = service.getCurrentCollege();
		if (!currentCollege.isPaymentGatewayEnabled()) {
			return new DisabledPaymentGatewayService();
		}
		switch (currentCollege.getPaymentGatewayType()) {
		case PAYMENT_EXPRESS:
			return new PaymentExpressGatewayService();
		case TEST:
			return new TestPaymentGatewayService();
		default:
			return new TestPaymentGatewayService();
		}

	}

}
