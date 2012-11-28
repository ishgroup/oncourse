package ish.oncourse.enrol.components.checkout.payment;

import ish.oncourse.enrol.checkout.payment.PaymentEditorDelegate;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

public class PaymentProgress {

    @Parameter(required = true)
    @Property
    private PaymentEditorDelegate delegate;

    @AfterRender
    void afterRender() {
        delegate.updatePaymentStatus();
    }
}
