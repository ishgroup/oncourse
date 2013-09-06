package ish.oncourse.services.paymentexpress;

import ish.common.types.CreditCardType;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentTransaction;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.cayenne.ObjectContext;
import org.junit.Before;
import org.junit.Test;

import static ish.oncourse.services.paymentexpress.TestPaymentGatewayService.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;


/**
 * Test for {@link TestPaymentGatewayService}.
 *
 * @author ksenia
 *
 */



public class TestPaymentGatewayServiceTest {

    private CreditCart creditCart;

    /**
     *Mock
     */
    private ObjectContext objectContext;
    private PaymentTransaction paymentTransaction;
    private ICayenneService cayenneService;
    private PaymentIn payment;
    /**
     * Instance to test.
     */
    private TestPaymentGatewayService gatewayService;

    /**
     * For testing we used valid and invalid cards
     */
    @Before
    public void initMethod() {
        objectContext =mock(ObjectContext.class);
        paymentTransaction=mock(PaymentTransaction.class);
        cayenneService=mock(ICayenneService.class);
        payment=mock(PaymentIn.class);

        when(payment.getObjectContext()).thenReturn(objectContext);
        when(objectContext.newObject(PaymentTransaction.class)).thenReturn(paymentTransaction);
        when(cayenneService.newNonReplicatingContext()).thenReturn(objectContext);

        this.gatewayService = new TestPaymentGatewayService(cayenneService);
    }

    /**
     * Test for
     * {@link TestPaymentGatewayService#performGatewayOperation(PaymentIn)}.
     * Emulates the situation when the payment passes through the gateway
     * successfully. The {@link PaymentIn#succeed()} should be invoked.
     */

    @Test
    public void gatewaySucceedTest() {
       creditCart =MASTERCARD;

        processCreditCard(creditCart);

        verify(payment).succeed();
    }

    /**
     * Test for
     * {@link TestPaymentGatewayService#performGatewayOperation(PaymentIn)}.
     * Emulates the situation when the payment passes through the gateway with
     * failed result. The {@link PaymentIn#failPayment()} should be invoked.
     */

    @Test
    public void gatewayNameFailedTest() {

        creditCart =new CreditCart(MASTERCARD);
        creditCart.setName("");

        processCreditCard(creditCart);

        verify(payment).failPayment();
        assertEquals(gatewayService.getResult().getStatusNotes(), NAME_ERROR+"\n");
    }

    @Test
    public void gatewayCVVFailedTest() {

        creditCart =new CreditCart(VISA);
        creditCart.setCvv("2d31");

        processCreditCard(creditCart);

        verify(payment).failPayment();
        assertEquals(gatewayService.getResult().getStatusNotes(), CVV_ERROR+"\n");
    }

    @Test
    public void gatewayExpireFailedTest() {

        creditCart =new CreditCart(AMEX);
        creditCart.setExpiry("11/2005");

        processCreditCard(creditCart);

        verify(payment).failPayment();
        assertEquals(gatewayService.getResult().getStatusNotes(), EXPIPE_ERROR+"\n");
    }

    @Test
    public void gatewayNumberFailedTest() {

        creditCart =new CreditCart(MASTERCARD);
        creditCart.setNumber("578282246310005");

        processCreditCard(creditCart);

        verify(payment).failPayment();
        assertEquals(gatewayService.getResult().getStatusNotes(), NUMBER_ERROR+"\n");
    }

    @Test
    public void gatewayTypeFailedTest() {

        creditCart =new CreditCart(VISA);
        creditCart.setType(CreditCardType.BANKCARD);

        processCreditCard(creditCart);

        verify(payment).failPayment();
        assertEquals(gatewayService.getResult().getStatusNotes(), NOT_SUPPORTED+"\n");
    }

    @Test
    public void gatewayNotCorreFailedTest() {

         creditCart = new CreditCart(CreditCardType.MASTERCARD,
                "5105105105105100",
                "JOHN MASTER",
                "322",
                "11/2027");
        processCreditCard(creditCart);

        verify(payment).failPayment();
        assertEquals(gatewayService.getResult().getStatusNotes(), DATA_NOT_CORRECT+"\n");
    }

    private void processCreditCard(CreditCart creditCart)
    {
        when(payment.validateCVV()).thenCallRealMethod();
        when(payment.validateCCExpiry()).thenCallRealMethod();
        when(payment.validateCCName()).thenCallRealMethod();
        when(payment.validateCCNumber()).thenCallRealMethod();

        when(payment.getCreditCardType()).thenReturn(creditCart.getType());
        when(payment.getCreditCardExpiry()).thenReturn(creditCart.getExpiry());
        when(payment.getCreditCardNumber()).thenReturn(creditCart.getNumber());
        when(payment.getCreditCardName()).thenReturn(creditCart.getName());
        when(payment.getCreditCardCVV()).thenReturn(creditCart.getCvv());

        gatewayService.performGatewayOperation(payment);
    }

}