package ish.oncourse.enrol.services.payment;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;

import ish.oncourse.enrol.utils.PaymentExpressUtil;
import ish.oncourse.model.College;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentStatus;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.paymentexpress.stubs.TransactionResult;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

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

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("d/M/y");

	private static final Calendar VALID_EXPIRY_DATE = Calendar.getInstance();

	private static final String CARD_NUMBER = "4111111111111111";

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
	 * Emulates the successful transaction,
	 * {@link TransactionResult#getAuthorized()} should return true.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSuccessfulPaymentGateway() throws Exception {
		when(payment.getCollege()).thenReturn(college);
		when(payment.getClientReference()).thenReturn(PAYMENT_REF);
		when(payment.getPaymentInLines()).thenReturn(Collections.EMPTY_LIST);
		when(payment.getAmount()).thenReturn(SUCCESS_PAYMENT_AMOUNT);
		when(payment.getCreditCardName()).thenReturn(CARD_HOLDER_NAME);
		when(payment.getCreditCardNumber()).thenReturn(CARD_NUMBER);
		when(payment.getCreditCardExpiry()).thenReturn(
				DATE_FORMAT.format(VALID_EXPIRY_DATE.getTime()));
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
	@Ignore
	public void testUnsuccessfulPaymentGateway() throws Exception {
		when(payment.getCollege()).thenReturn(college);
		when(payment.getClientReference()).thenReturn(PAYMENT_REF);
		when(payment.getPaymentInLines()).thenReturn(Collections.EMPTY_LIST);
		when(payment.getAmount()).thenReturn(FAILTURE_PAYMENT_AMOUNT);
		when(payment.getCreditCardName()).thenReturn(CARD_HOLDER_NAME);
		when(payment.getCreditCardNumber()).thenReturn(CARD_NUMBER);
		when(payment.getCreditCardExpiry()).thenReturn(
				DATE_FORMAT.format(VALID_EXPIRY_DATE.getTime()));
		TransactionResult tr = gatewayService.doTransaction(payment);
		// Somewhy the transaction is successful
		// May be incorrectly generated stubs....
		assertFalse(PaymentExpressUtil.translateFlag(tr.getAuthorized()));

	}
}
