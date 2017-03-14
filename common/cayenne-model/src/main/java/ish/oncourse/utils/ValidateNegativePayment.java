package ish.oncourse.utils;

import ish.common.types.PaymentType;
import ish.math.Money;
import ish.oncourse.model.PaymentIn;

import java.util.Arrays;
import java.util.List;

public class ValidateNegativePayment {

    private static final List<PaymentType> SYSTEM_TYPES = Arrays.asList(
            PaymentType.CONTRA, PaymentType.INTERNAL, PaymentType.REVERSE, PaymentType.VOUCHER);

    private PaymentIn paymentIn;

    private ValidateNegativePayment() {
    }

    /**
     * If payment have credit card type or system type then this payment should be positive.
     * @return
     */
    public boolean validate() {
        if (paymentIn.getAmount().isLessThan(Money.ZERO) &&
                (paymentIn.getType().equals(PaymentType.CREDIT_CARD) || SYSTEM_TYPES.contains(paymentIn.getType()))) {
            return false;
        }
        return true;
    }

    public static ValidateNegativePayment valueOf(PaymentIn paymentIn) {
        ValidateNegativePayment validateNegativePayment = new ValidateNegativePayment();
        validateNegativePayment.paymentIn = paymentIn;

        return validateNegativePayment;
    }
}