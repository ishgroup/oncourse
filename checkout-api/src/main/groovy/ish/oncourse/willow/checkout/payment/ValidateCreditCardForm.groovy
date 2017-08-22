package ish.oncourse.willow.checkout.payment

import ish.oncourse.model.PaymentIn
import ish.oncourse.util.payment.CreditCardValidator
import ish.oncourse.willow.model.checkout.payment.PaymentRequest
import ish.oncourse.willow.model.common.FieldError
import ish.oncourse.willow.model.common.ValidationError
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.commons.lang3.StringUtils

class ValidateCreditCardForm {

    PaymentRequest paymentRequest
    ObjectContext context

    ValidateCreditCardForm(PaymentRequest paymentRequest, ObjectContext context) {
        this.paymentRequest = paymentRequest
        this.context = context
    }
    
    ValidationError validate(boolean isZeroAmount) {
        ValidationError validationError = new ValidationError()

        if (!paymentRequest.agreementFlag) {
            validationError.fieldsErrors << new FieldError(name: 'agreementFlag', error: 'You must agree to the policies before proceeding.')
        }
        
        if (!isZeroAmount) {
            if (paymentRequest.creditCardName == null || paymentRequest.creditCardName == StringUtils.EMPTY) {
                validationError.fieldsErrors << new FieldError(name: 'creditCardName', error: 'Please supply your name as printed on the card (maximum 40 characters)')
            }
            String cardNumberErrorMessage = CreditCardValidator.validateNumber(paymentRequest.creditCardNumber)
            if (cardNumberErrorMessage != null) {
                validationError.fieldsErrors << new FieldError(name: 'creditCardNumber', error: cardNumberErrorMessage)
            }
            
            if (!CreditCardValidator.validCvv(paymentRequest.creditCardCvv)) {
                validationError.fieldsErrors << new FieldError(name: 'creditCardCvv', error: 'A valid credit card CVV must be supplied.')
            }
            
            if (!CreditCardValidator.validCCExpiry(paymentRequest.expiryMonth, paymentRequest.expiryYear)) {
                validationError.fieldsErrors << new FieldError(name: 'expiryYear', error: 'APlease verify your credit card\'s expiry month and year.')
            }
            
            PaymentIn paymentIn = ObjectSelect.query(PaymentIn).where(PaymentIn.SESSION_ID.eq(paymentRequest.sessionId)).selectFirst(context)
            
            if (paymentIn) {
                validationError.fieldsErrors << new FieldError(name: 'sessionId', error: 'sessionId should be unique')
            }
        }
        
        validationError
    }
    
    
}
