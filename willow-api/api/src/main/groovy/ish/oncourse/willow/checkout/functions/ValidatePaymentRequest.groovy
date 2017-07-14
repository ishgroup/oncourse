package ish.oncourse.willow.checkout.functions

import ish.math.Money
import ish.oncourse.willow.checkout.payment.HasErrors
import ish.oncourse.willow.checkout.payment.ValidateCreditCardForm
import ish.oncourse.willow.model.checkout.CheckoutModel
import ish.oncourse.willow.model.checkout.payment.PaymentRequest
import ish.oncourse.willow.model.common.CommonError
import ish.oncourse.willow.model.common.ValidationError
import org.apache.cayenne.ObjectContext

class ValidatePaymentRequest {

    CheckoutModel checkoutModel
    PaymentRequest paymentRequest
    ObjectContext context


    ValidatePaymentRequest(CheckoutModel checkoutModel, PaymentRequest paymentRequest, ObjectContext context) {
        this.checkoutModel = checkoutModel
        this.paymentRequest = paymentRequest
        this.context = context
    }
    
    CommonError commonError
    ValidationError validationError
    
    
    ValidatePaymentRequest validate() {
        
        Money minPayNow = checkoutModel.amount.payNow.toMoney()
        Money maxPayNow = minPayNow.add(checkoutModel.amount.owing.toMoney())

        Money actualPayNow = paymentRequest.payNow?.toMoney() ?: Money.ZERO
        
        if (new HasErrors(checkoutModel).hasErrors()) {
            commonError  = new CommonError(message: 'Purchase items are not valid')
        } 
        
        if (checkoutModel.amount.isEditable) {
            if (actualPayNow.isLessThan(minPayNow)) {
                commonError  = new CommonError(message: "Payment amount can not be less than $minPayNow" )
            } else if (actualPayNow.isGreaterThan(maxPayNow)) {
                commonError  = new CommonError(message: "Payment amount can not be more than $maxPayNow" )
            }
        } else {
            if (actualPayNow != minPayNow) {
                checkoutModel.error = new CommonError(message: 'Payment amount is wrong')
            }
        }
        
        ValidationError erroe = new ValidateCreditCardForm(paymentRequest, context).validate(Money.ZERO == actualPayNow)
        if (!erroe.formErrors.empty || !erroe.fieldsErrors.empty) {
            validationError = erroe
        }
        
        checkoutModel.error = commonError
        
        this
        
    }
    
}
