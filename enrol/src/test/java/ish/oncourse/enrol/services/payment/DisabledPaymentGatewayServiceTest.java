package ish.oncourse.enrol.services.payment;

import static org.mockito.Mockito.*;
import ish.oncourse.model.PaymentIn;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Test for the {@link DisabledPaymentGatewayService}.
 * 
 * @author ksenia
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class DisabledPaymentGatewayServiceTest {

	/**
	 * Instance to test.
	 */
	private static DisabledPaymentGatewayService gatewayService;

	/**
	 * Payment mock.
	 */
	@Mock
	private PaymentIn payment;

	/**
	 * Inits the service instance.
	 */
	@BeforeClass
	public static void init() {
		gatewayService = new DisabledPaymentGatewayService();
	}

	/**
	 * The test for the
	 * {@link DisabledPaymentGatewayService#performGatewayOperation(ish.oncourse.model.PaymentIn)}
	 * . The payment gateway is disabled, so neither {@link PaymentIn#succeed()}
	 * , nor {@link PaymentIn#failed()} should be invoked.
	 */
	@Test
	public void performGatewayOperationTest() {
		gatewayService.performGatewayOperation(payment);
		verify(payment, never()).succeed();
		verify(payment, never()).failed();
	}
}
