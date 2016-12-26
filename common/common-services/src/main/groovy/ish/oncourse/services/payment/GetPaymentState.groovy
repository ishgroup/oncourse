package ish.oncourse.services.payment

import groovy.transform.CompileStatic
import ish.common.types.PaymentStatus
import ish.oncourse.services.paymentexpress.PaymentExpressGatewayService
import ish.oncourse.services.persistence.ICayenneService

import static ish.oncourse.services.payment.GetPaymentState.PaymentState.*

@CompileStatic
class GetPaymentState {

	ExtendedModel model
	ICayenneService cayenneService
	
	PaymentState getState() {

		if (model.paymentIn) {
			switch (model.paymentIn.status) {
				
				case PaymentStatus.NEW:
				case PaymentStatus.IN_TRANSACTION:
					if (model.paymentIn.paymentTransactions.empty) {
						return model.failedPayments.empty ? READY_TO_PROCESS : CHOOSE_ABANDON_OTHER
					} else if (model.paymentIn.paymentTransactions.find { !it.isFinalised }) {
						return PaymentExpressGatewayService.UNKNOW_RESULT_PAYMENT_IN.equals(model.paymentIn.statusNotes) ? DPS_ERROR : DPS_PROCESSING
					} else {
						return ERROR
					}
					
				case PaymentStatus.CARD_DETAILS_REQUIRED:
					return FILL_CC_DETAILS
				default:
					return ERROR
			}
		} else if (model.successPayment) {
			return SUCCESS
		} else if (!model.failedPayments.empty) {
			return FAILED
		}
		return ERROR
	}


	enum PaymentState {
		READY_TO_PROCESS,
		FILL_CC_DETAILS,
		CHOOSE_ABANDON_OTHER,
		DPS_PROCESSING,
		FAILED,
		SUCCESS,
		DPS_ERROR,
		ERROR,
		WARNING

		public static List<PaymentState> RESULT_STATES = [CHOOSE_ABANDON_OTHER, FAILED, SUCCESS, ERROR, DPS_ERROR]
	}
}
