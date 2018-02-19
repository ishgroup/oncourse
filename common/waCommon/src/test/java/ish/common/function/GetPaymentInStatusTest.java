package ish.common.function;

import ish.common.types.PaymentStatus;
import ish.oncourse.cayenne.PaymentInInterface;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class GetPaymentInStatusTest {

    public static final String RESULT_IN_TRASNACTION = "In transaction";
    public static final String RESULT_SUCCESS = "Success";
    public static final String RESULT_REVERSED = "Reversed";

    @Test
    public void test() {

        assertEquals(RESULT_IN_TRASNACTION, GetPaymentInStatus.valueOf(getPayment(PaymentStatus.IN_TRANSACTION, false)).get());
        assertEquals(RESULT_IN_TRASNACTION, GetPaymentInStatus.valueOf(getPayment(PaymentStatus.IN_TRANSACTION, true)).get());
        assertEquals(RESULT_SUCCESS, GetPaymentInStatus.valueOf(getPayment(PaymentStatus.SUCCESS, false)).get());
        assertEquals(RESULT_REVERSED, GetPaymentInStatus.valueOf(getPayment(PaymentStatus.SUCCESS, true)).get());

    }

    PaymentInInterface getPayment(PaymentStatus status, boolean hasReverse) {
        PaymentInInterface payment = Mockito.mock(PaymentInInterface.class);
        when(payment.getStatus()).thenReturn(status);
        when(payment.isRelatedToReverse()).thenReturn(hasReverse);
        return payment;
    }
}
