package ish.oncourse.util.payment;

import ish.oncourse.model.PaymentIn;
import org.apache.tapestry5.ioc.Invokable;

public class ProcessPaymentInvokable implements Invokable<PaymentIn> {
    private  PaymentProcessController paymentProcessController;

    public ProcessPaymentInvokable(PaymentProcessController paymentProcessController) {
        this.paymentProcessController = paymentProcessController;
    }

    @Override
    public PaymentIn invoke() {
        return paymentProcessController.performGatewayOperation();
    }
}