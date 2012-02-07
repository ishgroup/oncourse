package ish.oncourse.enrol.services.payment;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import ish.common.types.CreditCardType;
import ish.oncourse.model.College;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentOut;
import ish.oncourse.model.PaymentOutTransaction;
import ish.oncourse.model.PaymentTransaction;
import ish.oncourse.services.paymentexpress.PaymentExpressGatewayService;
import ish.oncourse.services.paymentexpress.PaymentExpressUtil;
import ish.oncourse.services.persistence.ICayenneService;

import java.io.File;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collections;

import org.apache.cayenne.ObjectContext;
import org.apache.log4j.Logger;
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

	private static final Logger LOG = Logger.getLogger(PaymentExpressGatewayServiceTest.class);
	
	private static final String PAYMENT_REF = "W111";

	private static final String GATEWAY_PASSWORD = "test1234";

	private static final String GATEWAY_ACCOUNT = "ishGroup_Dev";

	private static final Calendar VALID_EXPIRY_DATE = Calendar.getInstance();

	private static final String VALID_EXPIRY_DATE_STR = VALID_EXPIRY_DATE.get(Calendar.MONTH) + 1 + "/"
			+ VALID_EXPIRY_DATE.get(Calendar.YEAR);
	private static final String INVALID_FORMAT_EXPIRY_DATE_STR = "Jun/" + (VALID_EXPIRY_DATE.get(Calendar.YEAR) + 10);
	private static final String EXPIRED_DATE_STR = VALID_EXPIRY_DATE.get(Calendar.MONTH) + 1 + "/"
			+ (VALID_EXPIRY_DATE.get(Calendar.YEAR) - 10);

	private static final String VALID_CARD_NUMBER = "5431111111111111";

	private static final String DECLINED_CARD_NUMBER = "9999990000000378";

	private static final String INVALID_CARD_NUMBER = "111111";

	private static final String CARD_HOLDER_NAME = "john smith";

	private static final BigDecimal SUCCESS_PAYMENT_AMOUNT = new BigDecimal(1.00);

	private static final BigDecimal FAILTURE_PAYMENT_AMOUNT = new BigDecimal(1.76);

	/**
	 * Instance to test.
	 */
	private PaymentExpressGatewayService gatewayService;

	/**
	 * The payment for gateway.
	 */
	@Mock
	private PaymentIn payment;
	
	@Mock
	private PaymentOut paymentOut;

	@Mock
	private ObjectContext objectContext;

	@Mock
	private PaymentTransaction paymentTransaction;
	
	@Mock
	private PaymentOutTransaction paymentOutTransaction;
	
	@Mock
	private ICayenneService cayenneService;

	/**
	 * The college for payment.
	 */
	private static College college;
	
	private static TransactionResult result1;

	/**
	 * Initializes parameters for the whole test.
	 */
	@BeforeClass
	public static void init() { 
		VALID_EXPIRY_DATE.add(Calendar.YEAR, 2);
		college = new College();
		college.setPaymentGatewayAccount(GATEWAY_ACCOUNT);
		college.setPaymentGatewayPass(GATEWAY_PASSWORD);
	}

	/**
	 * Performs common operations for every method.
	 */
	@SuppressWarnings("unchecked")
	@Before
	public void initMethod() {
		when(payment.getCollege()).thenReturn(college);
		when(payment.getPaymentInLines()).thenReturn(Collections.EMPTY_LIST);
		when(payment.getClientReference()).thenReturn(PAYMENT_REF);
		when(payment.getCreditCardName()).thenReturn(CARD_HOLDER_NAME);
		when(payment.getCreditCardExpiry()).thenReturn(VALID_EXPIRY_DATE_STR);
		when(payment.getObjectContext()).thenReturn(objectContext);
		when(payment.getCreditCardType()).thenReturn(CreditCardType.MASTERCARD);
		when(paymentTransaction.getObjectContext()).thenReturn(objectContext);
		when(objectContext.newObject(PaymentTransaction.class)).thenReturn(paymentTransaction);
		when(cayenneService.newNonReplicatingContext()).thenReturn(objectContext);
		
		when(paymentOut.getCollege()).thenReturn(college);
		when(paymentOut.getClientReference()).thenReturn(PAYMENT_REF);
		when(paymentOut.getObjectContext()).thenReturn(objectContext);
		when(paymentOutTransaction.getObjectContext()).thenReturn(objectContext);
		when(objectContext.newObject(PaymentOutTransaction.class)).thenReturn(paymentOutTransaction);
		
		this.gatewayService = new PaymentExpressGatewayService(cayenneService);
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
		when(payment.getPaymentTransactions()).thenReturn(Collections.singletonList(paymentTransaction));
		
		TransactionResult tr = gatewayService.doTransaction(payment);
		LOG.info("PaymentExpressResponse: " + tr.getMerchantHelpText());
		
		assertNotNull("Transaction result should be not empty for successfull payment", tr);
		boolean isAuthorized = PaymentExpressUtil.translateFlag(tr.getAuthorized());
		assertTrue("Check if authorized.", isAuthorized);
		assertTrue("PaymentTransaction should exist", !payment.getPaymentTransactions().isEmpty() && payment.getPaymentTransactions().size() == 1);
		result1 = tr;
	}
	
	/**
	 * Emulates the successful transaction,
	 * {@link TransactionResult#getAuthorized()} should return true.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSuccessfulDoOutTransaction() throws Exception {
		LOG.info("Create payment in for test DoOutTransaction");
		testSuccessfulDoTransaction();
		
		// a short delay is needed before refund will be accepted in DPS
		Thread.sleep(10*1000);
		
		TransactionResult tr1 = result1;
		LOG.info("DpsTxnRef to refund is "+ tr1.getDpsTxnRef());
		when(paymentOut.getPaymentInTxnReference()).thenReturn(tr1.getDpsTxnRef());
		when(paymentOut.getTotalAmount()).thenReturn(SUCCESS_PAYMENT_AMOUNT);
		when(paymentOut.getPaymentOutTransactions()).thenReturn(Collections.singletonList(paymentOutTransaction));
		
		TransactionResult tr = gatewayService.doTransaction(paymentOut);
		LOG.info("PaymentExpressResponse: " + tr.getMerchantHelpText());
		
		assertNotNull("Transaction result should be not empty for successfull payment", tr);
		boolean isAuthorized = PaymentExpressUtil.translateFlag(tr.getAuthorized());
		assertTrue("Check if authorized.", isAuthorized);
		assertTrue("PaymentTransaction should exist", !paymentOut.getPaymentOutTransactions().isEmpty() && paymentOut.getPaymentOutTransactions().size() == 1);
	}
	
	/**
	 * Emulates the failed transaction,
	 * {@link TransactionResult#getAuthorized()} should return false.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testUnsuccessfulDoTransaction() throws Exception {
		when(payment.getCreditCardNumber()).thenReturn(DECLINED_CARD_NUMBER);
		when(payment.getAmount()).thenReturn(FAILTURE_PAYMENT_AMOUNT);
		
		TransactionResult tr = gatewayService.doTransaction(payment);
		LOG.debug("PaymentExpressResponse: " + tr.getMerchantHelpText());
		
		assertFalse(PaymentExpressUtil.translateFlag(tr.getAuthorized()));
	}
	
	/**
	 * Emulates the failed transaction,
	 * {@link TransactionResult#getAuthorized()} should return false.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testUnsuccessfulDoOutTransaction() throws Exception {
		when(paymentOut.getTotalAmount()).thenReturn(FAILTURE_PAYMENT_AMOUNT);
		when(paymentOut.getPaymentOutTransactions()).thenReturn(Collections.singletonList(paymentOutTransaction));
		
		TransactionResult tr = gatewayService.doTransaction(paymentOut);
		LOG.debug("PaymentExpress response:" + tr.getMerchantHelpText());
		
		assertNotNull("Transaction result should be not empty for unsuccessfull payment out", tr);
		boolean isAuthorized = PaymentExpressUtil.translateFlag(tr.getAuthorized());
		assertFalse("Check if authorized.", isAuthorized);
		assertTrue("PaymentTransaction should exist", !paymentOut.getPaymentOutTransactions().isEmpty() && paymentOut.getPaymentOutTransactions().size() == 1);
	}

	/**
	 * Emulates the successful payment processing, {@link PaymentIn#succeed()}
	 * should be invoked.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSuccessfulProcessGateway() throws Exception {
		when(payment.getCreditCardNumber()).thenReturn(VALID_CARD_NUMBER);
		when(payment.getAmount()).thenReturn(SUCCESS_PAYMENT_AMOUNT);
		when(payment.getPaymentTransactions()).thenReturn(Collections.singletonList(paymentTransaction));
		gatewayService.processGateway(payment);
		verify(payment).succeed();
		assertTrue("PaymentTransaction should exist", !payment.getPaymentTransactions().isEmpty() && payment.getPaymentTransactions().size() == 1);
		verify(paymentTransaction).setSoapResponse(anyString());
	}
	
	/**
	 * Emulates the successful payment processing, {@link PaymentOut#succeed()}
	 * should be invoked.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSuccessfulOutProcessGateway() throws Exception {
		LOG.info("Create payment in for test OutProcessGateway");
		testSuccessfulDoTransaction();
		TransactionResult tr1 = result1;
		LOG.info("DpsTxnRef to refund is "+ tr1.getDpsTxnRef());
		when(paymentOut.getPaymentInTxnReference()).thenReturn(tr1.getDpsTxnRef());
		when(paymentOut.getTotalAmount()).thenReturn(SUCCESS_PAYMENT_AMOUNT);
		when(paymentOut.getPaymentOutTransactions()).thenReturn(Collections.singletonList(paymentOutTransaction));		
		gatewayService.processGateway(paymentOut);
		verify(paymentOut).succeed();
		assertTrue("PaymentTransaction should exist", !paymentOut.getPaymentOutTransactions().isEmpty() && paymentOut.getPaymentOutTransactions().size() == 1);
		verify(paymentOutTransaction).setSoapResponse(anyString());
	}

	/**
	 * Emulates the unsuccessful payment processing(with the declined gateway
	 * response), {@link PaymentIn#failPayment()} should be invoked.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testUnsuccessfulProcessGateway() throws Exception {
		when(payment.getCreditCardNumber()).thenReturn(DECLINED_CARD_NUMBER);
		when(payment.getAmount()).thenReturn(FAILTURE_PAYMENT_AMOUNT);
		when(payment.getPaymentTransactions()).thenReturn(Collections.singletonList(paymentTransaction));
		gatewayService.processGateway(payment);
		verify(payment).failPayment();
		assertTrue("PaymentTransaction should exist", !payment.getPaymentTransactions().isEmpty() && payment.getPaymentTransactions().size() == 1);
		verify(paymentTransaction).setSoapResponse(anyString());
	}
	
	/**
	 * Emulates the unsuccessful payment processing(with the declined gateway
	 * response), {@link PaymentOut#failed()} should be invoked.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testUnsuccessfulOffProcessGateway() throws Exception {
		when(paymentOut.getTotalAmount()).thenReturn(FAILTURE_PAYMENT_AMOUNT);
		when(paymentOut.getPaymentOutTransactions()).thenReturn(Collections.singletonList(paymentOutTransaction));
		gatewayService.processGateway(paymentOut);
		verify(paymentOut).failed();
		assertTrue("PaymentTransaction should exist", !paymentOut.getPaymentOutTransactions().isEmpty() && paymentOut.getPaymentOutTransactions().size() == 1);
		verify(paymentOutTransaction).setSoapResponse(anyString());
	}

	/**
	 * Emulates the successful payment, {@link PaymentIn#succeed()} should be
	 * invoked.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSuccessfulPerformGatewayOperation() throws Exception {
		forceValidation();

		when(payment.getCreditCardType()).thenReturn(CreditCardType.VISA, CreditCardType.VISA);
		when(payment.getCreditCardNumber()).thenReturn(VALID_CARD_NUMBER, VALID_CARD_NUMBER);
		when(payment.getAmount()).thenReturn(SUCCESS_PAYMENT_AMOUNT, SUCCESS_PAYMENT_AMOUNT);
		// they also will be invoked in validation
		when(payment.getCreditCardName()).thenReturn(CARD_HOLDER_NAME);
		when(payment.getCreditCardExpiry()).thenReturn(VALID_EXPIRY_DATE_STR);

		gatewayService.performGatewayOperation(payment);
		verify(payment).succeed();
	}

	/**
	 * Emulates the payment failed because of unsuccessful validation(negative
	 * amount), {@link PaymentIn#failPayment()} should be invoked.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testPerformGatewayOperationInvalidAmount() throws Exception {
		forceValidation();
		when(payment.getAmount()).thenReturn(BigDecimal.TEN.negate());
		when(payment.getCreditCardType()).thenReturn(CreditCardType.VISA, CreditCardType.VISA);
		when(payment.getCreditCardNumber()).thenReturn(VALID_CARD_NUMBER);

		gatewayService.performGatewayOperation(payment);
		verify(payment).failPayment();
	}

	/**
	 * Emulates the payment failed because of unsuccessful validation(credit
	 * card type is null), {@link PaymentIn#failPayment()} should be invoked.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testPerformGatewayOperationInvalidCCType() throws Exception {
		forceValidation();
		when(payment.getAmount()).thenReturn(SUCCESS_PAYMENT_AMOUNT);
		when(payment.getCreditCardType()).thenReturn(null);
		when(payment.getCreditCardNumber()).thenReturn(VALID_CARD_NUMBER);

		gatewayService.performGatewayOperation(payment);
		verify(payment).failPayment();
	}

	/**
	 * Emulates the payment failed because of unsuccessful validation(owner's
	 * name is empty), {@link PaymentIn#failPayment()} should be invoked.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testPerformGatewayOperationInvalidCardName() throws Exception {
		forceValidation();
		when(payment.getAmount()).thenReturn(SUCCESS_PAYMENT_AMOUNT);
		when(payment.getCreditCardType()).thenReturn(CreditCardType.VISA, CreditCardType.VISA);
		when(payment.getCreditCardNumber()).thenReturn(VALID_CARD_NUMBER);
		when(payment.getCreditCardName()).thenReturn("");

		// invoke first time to reset value from @BeforeClassMethod
		payment.getCreditCardName();

		gatewayService.performGatewayOperation(payment);
		verify(payment).failPayment();
	}

	/**
	 * Emulates the payment failed because of unsuccessful validation(credit
	 * card number is invalid), {@link PaymentIn#failPayment()} should be
	 * invoked.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testPerformGatewayOperationInvalidCCNumber() throws Exception {
		forceValidation();
		when(payment.getAmount()).thenReturn(SUCCESS_PAYMENT_AMOUNT);
		when(payment.getCreditCardType()).thenReturn(CreditCardType.VISA, CreditCardType.VISA);
		when(payment.getCreditCardNumber()).thenReturn(INVALID_CARD_NUMBER);

		gatewayService.performGatewayOperation(payment);
		verify(payment).failPayment();
	}

	/**
	 * Emulates the payment failed because of unsuccessful validation(credit
	 * card expiry date is invalid), {@link PaymentIn#failPayment()} should be
	 * invoked.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testPerformGatewayOperationInvalidCCExpiry() throws Exception {
		forceValidation();
		when(payment.getCreditCardName()).thenReturn(CARD_HOLDER_NAME);
		when(payment.getAmount()).thenReturn(SUCCESS_PAYMENT_AMOUNT, SUCCESS_PAYMENT_AMOUNT);
		when(payment.getCreditCardType()).thenReturn(CreditCardType.VISA, CreditCardType.VISA, CreditCardType.VISA,
				CreditCardType.VISA);
		when(payment.getCreditCardNumber()).thenReturn(VALID_CARD_NUMBER, VALID_CARD_NUMBER);
		when(payment.getCreditCardExpiry()).thenReturn(INVALID_FORMAT_EXPIRY_DATE_STR, EXPIRED_DATE_STR);

		// invoke first time to reset value from @BeforeClassMethod
		payment.getCreditCardExpiry();

		gatewayService.performGatewayOperation(payment);
		gatewayService.performGatewayOperation(payment);

		verify(payment, times(2)).failPayment();
	}

	/**
	 * Enforces real validation on {@link #payment} mock. Note! these methods
	 * are not reset after invocation within the whole test method.
	 */
	public void forceValidation() {
		when(payment.validateBeforeSend()).thenCallRealMethod();
		when(payment.validatePaymentAmount()).thenCallRealMethod();
		when(payment.validateCCType()).thenCallRealMethod();
		when(payment.validateCCName()).thenCallRealMethod();
		when(payment.validateCCNumber()).thenCallRealMethod();
		when(payment.validateCCExpiry()).thenCallRealMethod();
	}

}
