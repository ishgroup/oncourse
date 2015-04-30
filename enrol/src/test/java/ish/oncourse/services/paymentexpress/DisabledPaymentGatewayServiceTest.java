package ish.oncourse.services.paymentexpress;

import ish.oncourse.model.PaymentIn;
import ish.oncourse.util.payment.PaymentInFail;
import ish.oncourse.util.payment.PaymentInModel;
import ish.oncourse.util.payment.PaymentInModelFromPaymentInBuilder;
import ish.oncourse.util.payment.PaymentInSucceed;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

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
	 * {@link DisabledPaymentGatewayService#performGatewayOperation(PaymentInModel)}
	 * . The payment gateway is disabled, so neither {@link PaymentInSucceed#perform()}
	 * , nor {@link PaymentIn#failPayment()} should be invoked.
	 */
	@Test
	public void performGatewayOperationTest() {
		PaymentInModel model = PaymentInModelFromPaymentInBuilder.valueOf(payment).build().getModel();

		boolean illegalThrown = false;
		try {
			gatewayService.performGatewayOperation(model);
		}
		 catch (IllegalArgumentException e) {
			illegalThrown=true;
		}
		assertTrue("DisabledPaymentGatewayService should throw an exception for this call", illegalThrown);
		verify(PaymentInSucceed.valueOf(any(PaymentInModel.class)), never()).perform();
		verify(PaymentInFail.valueOf(any(PaymentInModel.class)), never()).perform();
	}
}
