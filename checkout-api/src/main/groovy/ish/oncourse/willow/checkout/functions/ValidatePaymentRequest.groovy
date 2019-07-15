package ish.oncourse.willow.checkout.functions

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
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
    private Money availableCredit


    ValidatePaymentRequest(CheckoutModel checkoutModel, PaymentRequest paymentRequest, ObjectContext context, Money availableCredit) {
        this.checkoutModel = checkoutModel
        this.paymentRequest = paymentRequest
        this.context = context
        this.availableCredit = availableCredit
    }
    
    CommonError commonError
    ValidationError validationError

    @CompileStatic(TypeCheckingMode.SKIP)
    ValidatePaymentRequest validate() {
        
        Money expectedPayNow = checkoutModel.amount.ccPayment.toMoney()
        Money actualPayNow = paymentRequest.ccAmount?.toMoney() ?: Money.ZERO

        Money applyCredit = checkoutModel.amount.credit?.toMoney() ?: Money.ZERO

        if (new HasErrors(checkoutModel).hasErrors()) {
            commonError  = new CommonError(message: 'Purchase items are not valid')
        } 

        if (actualPayNow != expectedPayNow) {
            checkoutModel.error = new CommonError(message: 'Credit card payment amount is wrong')
        }

        if (applyCredit.isGreaterThan(availableCredit)) {
            checkoutModel.error = new CommonError(message: 'Credit amount is wrong')
        }

        ValidationError erroe = new ValidateCreditCardForm(paymentRequest, context).validate(Money.ZERO == actualPayNow)
        if (!erroe.formErrors.empty || !erroe.fieldsErrors.empty) {
            validationError = erroe
        }
        
        checkoutModel.error = commonError
        
        this
        
    }
    
}
