package ish.oncourse.services.payment

import ish.common.types.PaymentStatus
import ish.oncourse.model.PaymentIn
import ish.oncourse.services.paymentexpress.INewPaymentGatewayServiceBuilder
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.util.payment.CreditCardValidator
import ish.oncourse.util.payment.PaymentInAbandon
import ish.util.CreditCardUtil
import org.apache.commons.lang3.StringUtils
import org.apache.tapestry5.ioc.Messages

import static ish.oncourse.services.payment.GetPaymentState.PaymentState.*
import static ish.oncourse.services.payment.PaymentAction.*

class NewPaymentProcessController {

	def ExtendedModel model
	def INewPaymentGatewayServiceBuilder paymentGatewayServiceBuilder
	def ICayenneService cayenneService
	def Messages messages
	def GetPaymentState.PaymentState state
	
	def processRequest(PaymentRequest request, PaymentResponse response) {
		switch (request.action) {
			case MAKE_PAYMENT:
				processPayment(request, response)
				break
			case CANCEL:
				abandonPaymentKeepInvoice(response)
				break
			case TRY_OTHER:
				tryOtherCard(response)
				break
			default: throw new IllegalArgumentException('Illegal payment action')
		}
	}

	def boolean readyToProcess() {
		return READY_TO_PROCESS == state
	}

	def boolean fillCCDetails() {
		return FILL_CC_DETAILS == state
	}
	
	def boolean isFailed() {
		FAILED == state
	}

	def boolean isSuccess() {
		SUCCESS == state
	}

	def boolean isChooseAbandonOther() {
		return CHOOSE_ABANDON_OTHER == state
	}
	
	def boolean inProgress() {
		return DPS_PROCESSING == state
	}

	def boolean isDPSError() {
		return DPS_ERROR == state
	}
	
	def boolean isError() {
		return ERROR == state
	}


	def processPayment(PaymentRequest request, PaymentResponse response) {	
		if (fillCCDetails()) {
			validate(request, response)
			if (response.hasErrors()) {
				response.status = WARNING
				return 
			}
			
			PaymentIn paymentIn = model.paymentIn
			paymentIn.creditCardType = CreditCardValidator.determineCreditCardType(request.number)
			paymentIn.creditCardCVV = CreditCardUtil.obfuscateCVVNumber(request.cvv)
			paymentIn.creditCardNumber = CreditCardUtil.obfuscateCCNumber(request.number)
			paymentIn.creditCardExpiry = request.month + "/" + request.year
			paymentIn.status = PaymentStatus.IN_TRANSACTION
			paymentIn.objectContext.commitChanges()


			paymentGatewayServiceBuilder.buildService().submit(model, request)
			
			switch (paymentIn.status) {
				case PaymentStatus.SUCCESS:
					response.status = SUCCESS
					break
				case PaymentStatus.FAILED_CARD_DECLINED:
					PaymentIn newOne = model.paymentIn.makeCopy()
					newOne.status = PaymentStatus.IN_TRANSACTION
					response.status = CHOOSE_ABANDON_OTHER
					break
				default:
					response.status = DPS_ERROR
			}
			
			model.paymentIn.objectContext.commitChanges()
		} else {
			response.status = ERROR
		}
	}

	def proceedToDetails(PaymentResponse response = null) {
		if (readyToProcess()) {
			model.paymentIn.status = PaymentStatus.CARD_DETAILS_REQUIRED
			model.paymentIn.objectContext.commitChanges()
			state = FILL_CC_DETAILS
			response?.status = FILL_CC_DETAILS
		} else {
		 	response?.status = ERROR
		}
	}
	
	
	def tryOtherCard(PaymentResponse response) {
		if (isChooseAbandonOther()) {
			model.paymentIn.status = PaymentStatus.CARD_DETAILS_REQUIRED
			model.paymentIn.objectContext.commitChanges()
			state = FILL_CC_DETAILS
			response.status = FILL_CC_DETAILS
		} else {
			response.status = ERROR
		}
	}

	def void abandonPaymentKeepInvoice(PaymentResponse response) {
		if (isChooseAbandonOther()) {
			PaymentInAbandon.valueOf(model, true).perform()
			model.paymentIn.objectContext.commitChanges()
			state = FAILED
			response.status = FAILED
		} else {
			response.status = ERROR
		}
	}

	private validate(PaymentRequest request, PaymentResponse response) {
		if (request.getName() == null || request.getName().equals(StringUtils.EMPTY)) {
			response.setCardNameError(messages.get("cardNameErrorMessage"));
		}
		String cardNumberErrorMessage = CreditCardValidator.validateNumber(request.getNumber());
		if (cardNumberErrorMessage != null) {
			response.setCardNumberError(cardNumberErrorMessage);
		}
		if (!CreditCardValidator.validCvv(request.getCvv())) {
			response.setCardCVVError(messages.get("cardcvv"));
		}
		if (!CreditCardValidator.validCCExpiry(request.getMonth(), request.getYear())) {
			response.setCardExpiryDateError(messages.get("expiryDateError"));
		}
	}
	
	def getPaymentProperty(String property) {
		switch (state) {
			case READY_TO_PROCESS:
			case FILL_CC_DETAILS:	
			case CHOOSE_ABANDON_OTHER:
			case DPS_PROCESSING:
			case DPS_ERROR:
			case WARNING:
				return model.paymentIn."$property"
			case FAILED:
				return model.failedPayments.get(0)."$property"
			case SUCCESS:
				return model.successPayment."$property"
			case ERROR:
				return model.paymentIn?."$property"
			default: null
		}
	}

}
