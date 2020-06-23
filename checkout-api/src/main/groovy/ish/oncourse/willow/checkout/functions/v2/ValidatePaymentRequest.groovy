package ish.oncourse.willow.checkout.functions.v2

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import ish.math.Money
import ish.oncourse.willow.checkout.payment.HasErrors
import ish.oncourse.willow.model.checkout.CheckoutModel
import ish.oncourse.willow.model.common.CommonError
import org.apache.cayenne.ObjectContext

class ValidatePaymentRequest {

    CheckoutModel checkoutModel
    Money actualPayNow
    ObjectContext context
    private Money availableCredit


    ValidatePaymentRequest(CheckoutModel checkoutModel, Money actualPayNow, ObjectContext context, Money availableCredit) {
        this.checkoutModel = checkoutModel
        this.actualPayNow = actualPayNow
        this.context = context
        this.availableCredit = availableCredit
    }
    
    CommonError commonError

    @CompileStatic(TypeCheckingMode.SKIP)
    ValidatePaymentRequest validate() {
        
        Money expectedPayNow = checkoutModel.amount.ccPayment.toMoney()
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

        checkoutModel.error = commonError
        
        this
        
    }
    
}
