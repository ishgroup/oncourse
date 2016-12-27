package ish.oncourse.services.payment

import groovy.transform.CompileStatic
import ish.common.types.PaymentStatus
import ish.math.Money
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

@CompileStatic
class NewPaymentProcessController {

	ExtendedModel model
	INewPaymentGatewayServiceBuilder paymentGatewayServiceBuilder
	ICayenneService cayenneService
	Messages messages
	GetPaymentState.PaymentState state
	
	void processRequest(PaymentRequest request, PaymentResponse response) {
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

	boolean readyToProcess() {
		return READY_TO_PROCESS == state
	}

	boolean fillCCDetails() {
		return FILL_CC_DETAILS == state
	}
	
	boolean isFailed() {
		FAILED == state
	}

	boolean isSuccess() {
		SUCCESS == state
	}

	boolean isChooseAbandonOther() {
		return CHOOSE_ABANDON_OTHER == state
	}
	
	boolean inProgress() {
		return DPS_PROCESSING == state
	}

	boolean isDPSError() {
		return DPS_ERROR == state
	}
	
	boolean isError() {
		return ERROR == state
	}


	void processPayment(PaymentRequest request, PaymentResponse response) {
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

	void proceedToDetails(PaymentResponse response = null) {
		if (readyToProcess()) {
			model.paymentIn.status = PaymentStatus.CARD_DETAILS_REQUIRED
			model.paymentIn.objectContext.commitChanges()
			state = FILL_CC_DETAILS
			response?.status = FILL_CC_DETAILS
		} else {
		 	response?.status = ERROR
		}
	}
	
	
	void tryOtherCard(PaymentResponse response) {
		if (isChooseAbandonOther()) {
			model.paymentIn.status = PaymentStatus.CARD_DETAILS_REQUIRED
			model.paymentIn.objectContext.commitChanges()
			state = FILL_CC_DETAILS
			response.status = FILL_CC_DETAILS
		} else {
			response.status = ERROR
		}
	}

	void abandonPaymentKeepInvoice(PaymentResponse response) {
		if (isChooseAbandonOther() || fillCCDetails()) {
			PaymentInAbandon.valueOf(model, true).perform()
			model.paymentIn.objectContext.commitChanges()
			state = FAILED
			response.status = FAILED
		} else {
			response.status = ERROR
		}
	}

	private void validate(PaymentRequest request, PaymentResponse response) {
		if (request.getName() == null || request.getName() == StringUtils.EMPTY) {
			response.setCardNameError(messages.get("cardNameErrorMessage"))
		}
		String cardNumberErrorMessage = CreditCardValidator.validateNumber(request.getNumber())
		if (cardNumberErrorMessage != null) {
			response.setCardNumberError(cardNumberErrorMessage)
		}
		if (!CreditCardValidator.validCvv(request.getCvv())) {
			response.setCardCVVError(messages.get("cardcvv"))
		}
		if (!CreditCardValidator.validCCExpiry(request.getMonth(), request.getYear())) {
			response.setCardExpiryDateError(messages.get("expiryDateError"))
		}
	}

}
