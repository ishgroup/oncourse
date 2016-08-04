package ish.oncourse.utils;

import ish.common.types.PaymentType;
import ish.math.Money;
import ish.oncourse.model.PaymentIn;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class ValidateNegativePaymentTest {

    @Test
    public void testCreditCardPayment() throws Exception {
        PaymentIn paymentIn = Mockito.mock(PaymentIn.class);
        when(paymentIn.getType()).thenReturn(PaymentType.CREDIT_CARD);
        when(paymentIn.getAmount()).thenReturn(new Money(-10, 0));
        assertFalse("Credit card type or system type payment should be positive", ValidateNegativePayment.valueOf(paymentIn).validate());

        when(paymentIn.getAmount()).thenReturn(new Money(10, 0));
        assertTrue("Credit card type or system type payment can be positive", ValidateNegativePayment.valueOf(paymentIn).validate());
    }

    @Test
    public void testSystemTypePayment() throws Exception {
        PaymentIn paymentIn = Mockito.mock(PaymentIn.class);
        when(paymentIn.getType()).thenReturn(PaymentType.CONTRA);
        when(paymentIn.getAmount()).thenReturn(new Money(-10, 0));
        assertFalse("CONTRA payment should be positive", ValidateNegativePayment.valueOf(paymentIn).validate());

        when(paymentIn.getType()).thenReturn(PaymentType.INTERNAL);
        assertFalse("INTERNAL payment should be positive", ValidateNegativePayment.valueOf(paymentIn).validate());

        when(paymentIn.getType()).thenReturn(PaymentType.REVERSE);
        assertFalse("REVERSE payment should be positive", ValidateNegativePayment.valueOf(paymentIn).validate());

        when(paymentIn.getType()).thenReturn(PaymentType.VOUCHER);
        assertFalse("VOUCHER payment should be positive", ValidateNegativePayment.valueOf(paymentIn).validate());
    }
}