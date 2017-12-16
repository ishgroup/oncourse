package ish.oncourse.services.paymentexpress

import groovy.transform.CompileStatic
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.services.preference.PreferenceController
import org.apache.tapestry5.ioc.annotations.Inject

import static ish.oncourse.model.PaymentGatewayType.PAYMENT_EXPRESS
import static ish.oncourse.model.PaymentGatewayType.TEST

@CompileStatic
class NewPaymentGatewayServiceBuilder implements INewPaymentGatewayServiceBuilder {
	private final PreferenceController preferenceController
	private final ICayenneService cayenneService
	
	@Inject
	NewPaymentGatewayServiceBuilder(PreferenceController preferenceController, ICayenneService cayenneService) {
		this.preferenceController = preferenceController
		this.cayenneService = cayenneService

	}

	/**
	 * Defines the appropriate payment gateway service in dependence on the
	 * college.paymentGatewayType property. {@inheritDoc}
	 *
	 */
	INewPaymentGatewayService buildService() {
		switch (preferenceController.paymentGatewayType) {
			case PAYMENT_EXPRESS:
				return new NewPaymentExpressGatewayService(cayenneService.newNonReplicatingContext())
			case TEST:
				return new NewTestPaymentGatewayService(cayenneService.newNonReplicatingContext())
			default:
				return new NewDisabledPaymentGatewayService()
		}
	}
}
