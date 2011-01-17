package ish.oncourse.enrol.services.payment;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import ish.oncourse.enrol.utils.PaymentExpressUtil;
import ish.oncourse.model.College;
import ish.oncourse.model.PaymentIn;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collections;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.paymentexpress.stubs.TransactionResult;

/**
 * Test for the {@link PaymentExpressGatewayService}.
 * http://www.paymentexpress.com/downloads/webservicetestscript.pdf
 * 
 * @author ksenia
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class PaymentExpressGatewayServiceTest {

	private static final String PAYMENT_REF = "W111";

	private static final String GATEWAY_PASSWORD = "test1234";

	private static final String GATEWAY_ACCOUNT = "ishGroup_Dev";

	private static final Calendar VALID_EXPIRY_DATE = Calendar.getInstance();

	private static final String VALID_EXPIRY_DATE_STR = VALID_EXPIRY_DATE.get(Calendar.MONTH)+1+"/"+VALID_EXPIRY_DATE.get(Calendar.YEAR);

	private static final String VALID_CARD_NUMBER = "4111111111111111";
	
	private static final String INVALID_CARD_NUMBER = "9999990000000378";

	private static final String CARD_HOLDER_NAME = "john smith";

	private static final BigDecimal SUCCESS_PAYMENT_AMOUNT = new BigDecimal(1.00);

	private static final BigDecimal FAILTURE_PAYMENT_AMOUNT = new BigDecimal(1.76);

	/**
	 * Instance to test.
	 */
	private static PaymentExpressGatewayService gatewayService;

	/**
	 * The payment for gateway.
	 */
	@Mock
	private PaymentIn payment;

	/**
	 * The college for payment.
	 */
	private static College college;

	/**
	 * Initializes parameters for the whole test.
	 */
	@BeforeClass
	public static void init() {
		gatewayService = new PaymentExpressGatewayService();
		VALID_EXPIRY_DATE.add(Calendar.YEAR, 2);
		college = new College();
		college.setPaymentGatewayAccount(GATEWAY_ACCOUNT);
		college.setPaymentGatewayPass(GATEWAY_PASSWORD);
	}
	/**
	 * Performs common operations for every method.
	 */
	@Before
	public void initMethod(){
		when(payment.getCollege()).thenReturn(college);
		when(payment.getPaymentInLines()).thenReturn(Collections.EMPTY_LIST);
		when(payment.getClientReference()).thenReturn(PAYMENT_REF);
		when(payment.getCreditCardName()).thenReturn(CARD_HOLDER_NAME);
		when(payment.getCreditCardExpiry()).thenReturn(VALID_EXPIRY_DATE_STR);
	}


	/**
	 * Emulates the successful transaction,
	 * {@link TransactionResult#getAuthorized()} should return true.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSuccessfulDoTransaction() throws Exception {
		when(payment.getCreditCardNumber()).thenReturn(VALID_CARD_NUMBER);
		when(payment.getAmount()).thenReturn(SUCCESS_PAYMENT_AMOUNT);
		TransactionResult tr = gatewayService.doTransaction(payment);
		assertTrue(PaymentExpressUtil.translateFlag(tr.getAuthorized()));
	}

	/**
	 * Emulates the failed transaction,
	 * {@link TransactionResult#getAuthorized()} should return false.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testUnsuccessfulDoTransaction() throws Exception {
		when(payment.getCreditCardNumber()).thenReturn(INVALID_CARD_NUMBER);
		when(payment.getAmount()).thenReturn(FAILTURE_PAYMENT_AMOUNT);
		TransactionResult tr = gatewayService.doTransaction(payment);
		assertFalse(PaymentExpressUtil.translateFlag(tr.getAuthorized()));
	}
	
	/**
	 * Emulates the successful payment,
	 * {@link PaymentIn#succeed()} should be invoked.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSuccessfulPerformGatewayOperation() throws Exception {
		when(payment.getCreditCardNumber()).thenReturn(VALID_CARD_NUMBER);
		when(payment.getAmount()).thenReturn(SUCCESS_PAYMENT_AMOUNT);
		gatewayService.performGatewayOperation(payment);
		verify(payment).succeed();
	}
	
	/**
	 * Emulates the unsuccessful payment(with the declined gateway response),
	 * {@link PaymentIn#failed()} should be invoked.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testUnsuccessfulPerformGatewayOperation() throws Exception {
		when(payment.getCreditCardNumber()).thenReturn(INVALID_CARD_NUMBER);
		when(payment.getAmount()).thenReturn(FAILTURE_PAYMENT_AMOUNT);
		gatewayService.performGatewayOperation(payment);
		verify(payment).failed();
	}
}
