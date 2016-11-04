package ish.oncourse.services.paymentexpress

import ish.oncourse.services.ServiceModule
import ish.oncourse.services.preference.PreferenceController
import org.apache.tapestry5.ioc.annotations.Inject

class NewPaymentGatewayServiceBuilder implements INewPaymentGatewayServiceBuilder {


	private final PreferenceController preferenceController;

	@Inject
	def NewPaymentGatewayServiceBuilder(PreferenceController preferenceController) {
		super();
		this.preferenceController = preferenceController;
	}

	/**
	 * Defines the appropriate payment gateway service in dependence on the
	 * college.paymentGatewayType property. {@inheritDoc}
	 *
	 */
	def INewPaymentGatewayService buildService() {

		def isInTestMode = Boolean.valueOf(System.getProperty(ServiceModule.APP_TEST_MODE));

		if (isInTestMode) {
			return new NewTestPaymentGatewayService();
		}

		switch (preferenceController.paymentGatewayType) {
			case PAYMENT_EXPRESS:
				return new NewPaymentExpressGatewayService();
			case TEST:
				return new NewTestPaymentGatewayService();
			default:
				return new NewDisabledPaymentGatewayService();
		}
	}
}
