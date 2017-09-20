package ish.oncourse.willow.checkout.payment

import ish.common.types.ConfirmationStatus
import ish.common.types.ProductType
import ish.oncourse.util.payment.PaymentInModel

class SetConfirmationStatus {
    
    private PaymentInModel model
    
    static SetConfirmationStatus valueOf(PaymentInModel model) {
        SetConfirmationStatus set = new SetConfirmationStatus()
        set.model = model
        return set
    }
    
    private SetConfirmationStatus() {}
    
    void set() {
        if (model.paymentIn) {
            model.paymentIn.confirmationStatus = ConfirmationStatus.NOT_SENT
        }
        
        model.invoices.each { i ->
            i.confirmationStatus = ConfirmationStatus.NOT_SENT
            i.invoiceLines.each { il -> 
                if (il.enrolment) {
                    il.enrolment.confirmationStatus = ConfirmationStatus.NOT_SENT
                } else if (il.productItems.size() > 0) {
                    il.productItems.each { pi ->
                        if (ProductType.VOUCHER.databaseValue == pi.type) {
                            pi.confirmationStatus = ConfirmationStatus.NOT_SENT
                        }
                    }
                }
            }
        }
    }
}
