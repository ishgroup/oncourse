package ish.oncourse.services.paymentexpress

import groovy.transform.CompileStatic
import ish.oncourse.services.ServiceModule
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.services.preference.PreferenceController
import org.apache.tapestry5.ioc.annotations.Inject

import static ish.oncourse.model.PaymentGatewayType.*

@CompileStatic
class NewPaymentGatewayServiceBuilder implements INewPaymentGatewayServiceBuilder {


	private final PreferenceController preferenceController;
	private final ICayenneService cayenneService;
	
	@Inject
	NewPaymentGatewayServiceBuilder(PreferenceController preferenceController, ICayenneService cayenneService) {
		super();
		this.preferenceController = preferenceController;
		this.cayenneService = cayenneService;

	}

	/**
	 * Defines the appropriate payment gateway service in dependence on the
	 * college.paymentGatewayType property. {@inheritDoc}
	 *
	 */
	INewPaymentGatewayService buildService() {

		def isInTestMode = Boolean.valueOf(System.getProperty(ServiceModule.APP_TEST_MODE));

		if (isInTestMode) {
			return new NewTestPaymentGatewayService(cayenneService.newNonReplicatingContext())
		}

		switch (preferenceController.paymentGatewayType) {
			case PAYMENT_EXPRESS:
				return new NewPaymentExpressGatewayService(cayenneService.newNonReplicatingContext())
			case TEST:
				return new NewTestPaymentGatewayService(cayenneService.newNonReplicatingContext())
			default:
				return new NewDisabledPaymentGatewayService();
		}
	}
}
