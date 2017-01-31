package ish.oncourse.enrol.checkout;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import static org.junit.Assert.*;

public class ClassVoucherForSeveralEnrolmentsTest extends ACheckoutTest {
    @Before
    public void setup() throws Exception {
        super.setup("ish/oncourse/enrol/checkout/ClassVoucherForSeveralEnrolmentsTest.xml");
    }

    @Test
    public void test() throws InterruptedException {
        PurchaseController purchaseController = init(Arrays.asList(1001L, 1002L), Collections.EMPTY_LIST, Collections.EMPTY_LIST, false);
        addFirstContact(1001L);
        addCode("v1003");
        proceedToPayment();
        assertEquals(getModel().getVoucherPayments().get(0).getPaymentInLines().size(), 1);
        makeValidPayment();
    }
}
