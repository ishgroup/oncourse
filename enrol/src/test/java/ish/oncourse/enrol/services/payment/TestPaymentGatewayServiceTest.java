package ish.oncourse.enrol.services.payment;

import ish.common.types.CreditCardType;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.services.paymentexpress.TestPaymentGatewayService;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

/**
 * Test for {@link TestPaymentGatewayService}.
 * 
 * @author ksenia
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class TestPaymentGatewayServiceTest {

	/**
	 * Instance to test.
	 */
	private static TestPaymentGatewayService gatewayService;

	/**
	 * Mock payment.
	 */
	@Mock
	private PaymentIn payment;

	/**
	 * Initializes the test.
	 */
	@BeforeClass
	public static void init() {
		gatewayService = new TestPaymentGatewayService();
	}

	/**
	 * Test for
	 * {@link TestPaymentGatewayService#performGatewayOperation(PaymentIn)}.
	 * Emulates the situation when the payment passes through the gateway
	 * successfully. The {@link PaymentIn#succeed()} should be invoked.
	 */
	@Test
	public void gatewaySucceedTest() {
		when(payment.getCreditCardType()).thenReturn(CreditCardType.MASTERCARD);
		gatewayService.performGatewayOperation(payment);
		verify(payment).succeed();
	}

	/**
	 * Test for
	 * {@link TestPaymentGatewayService#performGatewayOperation(PaymentIn)}.
	 * Emulates the situation when the payment passes through the gateway with
	 * failed result. The {@link PaymentIn#failed()} should be invoked.
	 */
	@Test
	public void gatewayFailedTest() {
		when(payment.getCreditCardType()).thenReturn(CreditCardType.VISA);
		gatewayService.performGatewayOperation(payment);
		verify(payment).failed();
	}
}
