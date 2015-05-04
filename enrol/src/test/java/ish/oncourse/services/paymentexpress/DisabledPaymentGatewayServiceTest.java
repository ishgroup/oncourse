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
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test for the {@link DisabledPaymentGatewayService}.
 * 
 * @author ksenia
 * 
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(value = {PaymentInFail.class, PaymentInSucceed.class})
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
		PaymentInSucceed paymentInSucceed = mock(PaymentInSucceed.class);
		PaymentInFail paymentInFail = mock(PaymentInFail.class);
		
		PowerMockito.mockStatic(PaymentInSucceed.class, PaymentInFail.class);
		
		when(PaymentInSucceed.valueOf(any(PaymentInModel.class))).thenReturn(paymentInSucceed);
		when(PaymentInFail.valueOf(any(PaymentInModel.class))).thenReturn(paymentInFail);
		
		PaymentInModel model = PaymentInModelFromPaymentInBuilder.valueOf(payment).build().getModel();

		boolean illegalThrown = false;
		try {
			gatewayService.performGatewayOperation(model);
		}
		 catch (IllegalArgumentException e) {
			illegalThrown=true;
		}
		assertTrue("DisabledPaymentGatewayService should throw an exception for this call", illegalThrown);

		PowerMockito.verifyStatic(times(0));
		
		PaymentInSucceed.valueOf(any(PaymentInModel.class));
		PaymentInFail.valueOf(any(PaymentInModel.class));
		
		verify(paymentInSucceed, never()).perform();
		verify(paymentInFail, never()).perform();
	}
}
