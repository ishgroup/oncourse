package ish.common.function;

import ish.common.types.PaymentStatus;
import ish.oncourse.cayenne.PaymentInInterface;

public class GetPaymentInStatus {

    public static final String REVERSED_STATUS = "Reversed";

    PaymentInInterface payment;

    public static GetPaymentInStatus valueOf(PaymentInInterface payment) {
        GetPaymentInStatus obj = new GetPaymentInStatus();
        obj.payment = payment;
        return obj;
    }

    public String get() {
        if (PaymentStatus.SUCCESS.equals(payment.getStatus())
                && payment.isRelatedToReverse()) {
            return REVERSED_STATUS;
        }

        return payment.getStatus().getDisplayName();
    }

}
