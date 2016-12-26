package ish.oncourse.services.payment

import groovy.transform.AutoClone
import groovy.transform.CompileStatic

@AutoClone
@CompileStatic
class PaymentResponse {

    String cardNameError
    String cardNumberError
    String cardCVVError
    String cardExpiryDateError
    GetPaymentState.PaymentState status

    boolean hasErrors() {
        cardNameError || cardNumberError || cardCVVError || cardExpiryDateError
    }
}
