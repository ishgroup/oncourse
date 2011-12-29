package ish.oncourse.services.paymentexpress;

import ish.oncourse.services.ServiceModule;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.site.IWebSiteService;

import org.apache.tapestry5.ioc.annotations.Inject;

public class PaymentGatewayServiceBuilder implements IPaymentGatewayServiceBuilder {

	private PreferenceController preferenceController;

	@Inject
	public PaymentGatewayServiceBuilder(PreferenceController preferenceController) {
		super();
		this.preferenceController = preferenceController;
	}

	/**
	 * Defines the appropriate payment gateway service in dependence on the
	 * college.paymentGatewayType property. {@inheritDoc}
	 * 
	 */
	public IPaymentGatewayService buildService() {
		
		boolean isInTestMode = "true".equalsIgnoreCase(System.getProperty(ServiceModule.APP_TEST_MODE));

		if (isInTestMode) {
			return new TestPaymentGatewayService();
		}

		switch (preferenceController.getPaymentGatewayType()) {
		case PAYMENT_EXPRESS:
			return new PaymentExpressGatewayService();
		case TEST:
			return new TestPaymentGatewayService();
		case DISABLED:
			return new DisabledPaymentGatewayService();
		default:
			return new DisabledPaymentGatewayService();
		}
	}
}
