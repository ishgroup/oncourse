package ish.oncourse.services.paymentexpress;

import ish.oncourse.services.ServiceModule;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.PreferenceController;
import org.apache.tapestry5.ioc.annotations.Inject;

public class PaymentGatewayServiceBuilder implements IPaymentGatewayServiceBuilder {

	private final PreferenceController preferenceController;

	private final ICayenneService cayenneService;

	@Inject
	public PaymentGatewayServiceBuilder(PreferenceController preferenceController, ICayenneService cayenneService) {
		super();
		this.preferenceController = preferenceController;
		this.cayenneService = cayenneService;
	}

	/**
	 * Defines the appropriate payment gateway service in dependence on the
	 * college.paymentGatewayType property. {@inheritDoc}
	 * 
	 */
	public IPaymentGatewayService buildService() {

		boolean isInTestMode = Boolean.valueOf(System.getProperty(ServiceModule.APP_TEST_MODE));

		if (isInTestMode) {
			return new TestPaymentGatewayService(cayenneService);
		}

		switch (preferenceController.getPaymentGatewayType()) {
		case PAYMENT_EXPRESS:
			return new PaymentExpressGatewayService(cayenneService);
		case TEST:
			return new TestPaymentGatewayService(cayenneService);
		case DISABLED:
			return new DisabledPaymentGatewayService();
		default:
			return new DisabledPaymentGatewayService();
		}
	}
}
