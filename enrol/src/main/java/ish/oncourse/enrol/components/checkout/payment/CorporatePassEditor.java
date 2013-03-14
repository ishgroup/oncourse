package ish.oncourse.enrol.components.checkout.payment;

import ish.oncourse.enrol.checkout.payment.PaymentEditorDelegate;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

public class CorporatePassEditor implements IPaymentControlDelegate {

    @Parameter(required = true)
    @Property
    private PaymentEditorDelegate delegate;

    @Override
    public Object makePayment() {
        delegate.makePayment();
        return this;
    }
}
