package ish.oncourse.services.paymentexpress;

import com.paymentexpress.stubs.PaymentExpressWSSoap12Stub;
import com.paymentexpress.stubs.TransactionDetails;
import com.paymentexpress.stubs.TransactionResult2;
import ish.common.types.CreditCardType;
import ish.common.types.PaymentStatus;
import ish.math.Money;
import ish.oncourse.model.College;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentOut;
import ish.oncourse.model.PaymentOutTransaction;
import ish.oncourse.model.PaymentTransaction;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.util.payment.PaymentInFail;
import ish.oncourse.util.payment.PaymentInModel;
import ish.oncourse.util.payment.PaymentInModelFromPaymentInBuilder;
import ish.oncourse.util.payment.PaymentInSucceed;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.xml.rpc.ServiceException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test for the {@link PaymentExpressGatewayService}.
 * http://www.paymentexpress.com/downloads/webservicetestscript.pdf
 * 
 * @author ksenia
 * 
 */
@PowerMockIgnore(value = {"com.sun.org.apache.xerces.*", "org.apache.axis.*", "com.paymentexpress.stubs.*", "org.apache.axis.utils.*", "javax.xml.parsers.*", "org.apache.logging.log4j.*", "org.apache.xerces.*", "ish.oncourse.paymentexpress.customization.*", "org.codehaus.groovy.*", "groovy.lang.*"})
@RunWith(PowerMockRunner.class)
@PrepareForTest(value = {PaymentInFail.class, PaymentInSucceed.class})
public class PaymentExpressGatewayServiceTest {

	private static final Logger LOG = LogManager.getLogger();
	
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

	private static final Money SUCCESS_PAYMENT_AMOUNT = new Money("1.00");

	private static final Money FAILTURE_PAYMENT_AMOUNT = new Money("1.76");

	/**
	 * Instance to test.
	 */
	private PaymentExpressGatewayService gatewayService;

	/**
	 * The payment for gateway.
	 */
	//Mocks
	private PaymentIn payment;
	private PaymentOut paymentOut;
	private ObjectContext objectContext;
	private PaymentTransaction paymentTransaction;
	private PaymentOutTransaction paymentOutTransaction;
	private ICayenneService cayenneService;

	/**
	 * The college for payment.
	 */
	private static College college;
	
	private static TransactionResult2 result1;

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
		payment = mock(PaymentIn.class);
		paymentOut = mock(PaymentOut.class);
		objectContext = mock(ObjectContext.class);
		paymentTransaction = mock(PaymentTransaction.class);
		paymentOutTransaction = mock(PaymentOutTransaction.class);
		cayenneService = mock(ICayenneService.class);
		
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
	 * {@link TransactionResult2#getAuthorized()} should return true.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSuccessfulDoTransaction() throws Exception {
		when(payment.getCreditCardNumber()).thenReturn(VALID_CARD_NUMBER);
		when(payment.getAmount()).thenReturn(SUCCESS_PAYMENT_AMOUNT);
		when(payment.getPaymentTransactions()).thenReturn(Collections.singletonList(paymentTransaction));
		
		TransactionResult tr = gatewayService.doTransaction(payment, null);
		LOG.info("PaymentExpressResponse: " + tr.getResult2().getMerchantHelpText());
		
		assertNotNull("Transaction result should be not empty for successfull payment", tr);
		boolean isAuthorized = PaymentExpressUtil.translateFlag(tr.getResult2().getAuthorized());
		assertTrue("Check if authorized.", isAuthorized);
		assertTrue("PaymentTransaction should exist", !payment.getPaymentTransactions().isEmpty() && payment.getPaymentTransactions().size() == 1);
		result1 = tr.getResult2();
	}
	
	/**
	 * Emulates the successful transaction,
	 * {@link TransactionResult2#getAuthorized()} should return true.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSuccessfulDoOutTransaction() throws Exception {
		LOG.info("Create payment in for test DoOutTransaction");
		testSuccessfulDoTransaction();
		
		// a short delay is needed before refund will be accepted in DPS
		Thread.sleep(10*1000);
		
		TransactionResult2 tr1 = result1;
		LOG.info("DpsTxnRef to refund is "+ tr1.getDpsTxnRef());
		when(paymentOut.getPaymentInTxnReference()).thenReturn(tr1.getDpsTxnRef());
		when(paymentOut.getTotalAmount()).thenReturn(SUCCESS_PAYMENT_AMOUNT);
		when(paymentOut.getPaymentOutTransactions()).thenReturn(Collections.singletonList(paymentOutTransaction));
		
		TransactionResult tr = gatewayService.doTransaction(paymentOut);
		LOG.info("PaymentExpressResponse: " + tr.getResult2().getMerchantHelpText());
		
		assertNotNull("Transaction result should be not empty for successfull payment", tr);
		boolean isAuthorized = PaymentExpressUtil.translateFlag(tr.getResult2().getAuthorized());
		assertTrue("Check if authorized.", isAuthorized);
		assertTrue("PaymentTransaction should exist", !paymentOut.getPaymentOutTransactions().isEmpty() && paymentOut.getPaymentOutTransactions().size() == 1);
	}
	
	/**
	 * Emulates the failed transaction,
	 * {@link TransactionResult2#getAuthorized()} should return false.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testUnsuccessfulDoTransaction() throws Exception {
		when(payment.getCreditCardNumber()).thenReturn(DECLINED_CARD_NUMBER);
		when(payment.getAmount()).thenReturn(FAILTURE_PAYMENT_AMOUNT);
		
		TransactionResult tr = gatewayService.doTransaction(payment, null);
		LOG.debug("PaymentExpressResponse: " + tr.getResult2().getMerchantHelpText());
		
		assertFalse(PaymentExpressUtil.translateFlag(tr.getResult2().getAuthorized()));
	}
	
	/**
	 * Emulates the failed transaction,
	 * {@link TransactionResult2#getAuthorized()} should return false.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testUnsuccessfulDoOutTransaction() throws Exception {
		when(paymentOut.getTotalAmount()).thenReturn(FAILTURE_PAYMENT_AMOUNT);
		when(paymentOut.getPaymentOutTransactions()).thenReturn(Collections.singletonList(paymentOutTransaction));
		
		TransactionResult tr = gatewayService.doTransaction(paymentOut);
		LOG.debug("PaymentExpress response:" + tr.getResult2().getMerchantHelpText());
		
		assertNotNull("Transaction result should be not empty for unsuccessfull payment out", tr);
		boolean isAuthorized = PaymentExpressUtil.translateFlag(tr.getResult2().getAuthorized());
		assertFalse("Check if authorized.", isAuthorized);
		assertTrue("PaymentTransaction should exist", !paymentOut.getPaymentOutTransactions().isEmpty() && paymentOut.getPaymentOutTransactions().size() == 1);
	}

	/**
	 * Emulates the successful payment processing, {@link PaymentInSucceed#perform()}
	 * should be invoked.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSuccessfulProcessGateway() throws Exception {
		PaymentInSucceed paymentInSucceed = mock(PaymentInSucceed.class);
		PowerMockito.mockStatic(PaymentInSucceed.class);
		when(PaymentInSucceed.valueOf(any(PaymentInModel.class))).thenReturn(paymentInSucceed);
		
		when(payment.getCreditCardNumber()).thenReturn(VALID_CARD_NUMBER);
		when(payment.getAmount()).thenReturn(SUCCESS_PAYMENT_AMOUNT);
		when(payment.getPaymentTransactions()).thenReturn(Collections.singletonList(paymentTransaction));
		when(payment.validateBeforeSend()).thenReturn(true);
		PaymentInModel model = PaymentInModelFromPaymentInBuilder.valueOf(payment).build().getModel();
		gatewayService.performGatewayOperation(model);
		assertTrue("PaymentTransaction should exist", !payment.getPaymentTransactions().isEmpty() && payment.getPaymentTransactions().size() == 1);
		
		PowerMockito.verifyStatic(times(1));
		PaymentInSucceed.valueOf(any(PaymentInModel.class));
		verify(paymentInSucceed).perform();
		
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
		
		// a short delay is needed before refund will be accepted in DPS
		Thread.sleep(10*1000);
				
		TransactionResult2 tr1 = result1;
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
		PaymentInFail paymentInFail = mock(PaymentInFail.class);
		PowerMockito.mockStatic(PaymentInFail.class);
		when(PaymentInFail.valueOf(any(PaymentInModel.class))).thenReturn(paymentInFail);
		
		when(payment.getCreditCardNumber()).thenReturn(DECLINED_CARD_NUMBER);
		when(payment.getAmount()).thenReturn(FAILTURE_PAYMENT_AMOUNT);
		when(payment.getPaymentTransactions()).thenReturn(Collections.singletonList(paymentTransaction));
		when(payment.validateBeforeSend()).thenReturn(true);
		when(payment.getStatus()).thenReturn(PaymentStatus.FAILED);
		PaymentInModel model = PaymentInModelFromPaymentInBuilder.valueOf(payment).build().getModel();
		gatewayService.performGatewayOperation(model);
		
		PowerMockito.verifyStatic(times(1));
		PaymentInFail.valueOf(any(PaymentInModel.class));
		verify(paymentInFail).perform();
		
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
	 * Emulates the successful payment, {@link PaymentInSucceed#perform()} should be
	 * invoked.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSuccessfulPerformGatewayOperation() throws Exception {
		forceValidation();

		PaymentInSucceed paymentInSucceed = mock(PaymentInSucceed.class);
		PowerMockito.mockStatic(PaymentInSucceed.class);
		when(PaymentInSucceed.valueOf(any(PaymentInModel.class))).thenReturn(paymentInSucceed);

		when(payment.getCreditCardType()).thenReturn(CreditCardType.VISA, CreditCardType.VISA);
		when(payment.getCreditCardNumber()).thenReturn(VALID_CARD_NUMBER, VALID_CARD_NUMBER);
		when(payment.getAmount()).thenReturn(SUCCESS_PAYMENT_AMOUNT, SUCCESS_PAYMENT_AMOUNT);
		// they also will be invoked in validation
		when(payment.getCreditCardName()).thenReturn(CARD_HOLDER_NAME);
		when(payment.getCreditCardExpiry()).thenReturn(VALID_EXPIRY_DATE_STR);

		PaymentInModel model = PaymentInModelFromPaymentInBuilder.valueOf(payment).build().getModel();

		gatewayService.performGatewayOperation(model);

		PowerMockito.verifyStatic(times(1));
		PaymentInSucceed.valueOf(any(PaymentInModel.class));
		verify(paymentInSucceed).perform();
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

		PaymentInFail paymentInFail = mock(PaymentInFail.class);
		PowerMockito.mockStatic(PaymentInFail.class);
		when(PaymentInFail.valueOf(any(PaymentInModel.class))).thenReturn(paymentInFail);

		when(payment.getAmount()).thenReturn(new Money(BigDecimal.TEN.negate()));
		when(payment.getCreditCardType()).thenReturn(CreditCardType.VISA, CreditCardType.VISA);
		when(payment.getCreditCardNumber()).thenReturn(VALID_CARD_NUMBER);
		when(payment.getStatus()).thenReturn(PaymentStatus.FAILED);

		PaymentInModel model = PaymentInModelFromPaymentInBuilder.valueOf(payment).build().getModel();

		gatewayService.performGatewayOperation(model);
		
		PowerMockito.verifyStatic(times(1));
		PaymentInFail.valueOf(any(PaymentInModel.class));
		verify(paymentInFail).perform();
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

		PaymentInFail paymentInFail = mock(PaymentInFail.class);
		PowerMockito.mockStatic(PaymentInFail.class);
		when(PaymentInFail.valueOf(any(PaymentInModel.class))).thenReturn(paymentInFail);
		
		when(payment.getAmount()).thenReturn(SUCCESS_PAYMENT_AMOUNT);
		when(payment.getCreditCardType()).thenReturn(null);
		when(payment.getCreditCardNumber()).thenReturn(VALID_CARD_NUMBER);
		when(payment.getStatus()).thenReturn(PaymentStatus.FAILED);

		PaymentInModel model = PaymentInModelFromPaymentInBuilder.valueOf(payment).build().getModel();

		gatewayService.performGatewayOperation(model);
		
		PowerMockito.verifyStatic(times(1));
		PaymentInFail.valueOf(any(PaymentInModel.class));
		verify(paymentInFail).perform();
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

		PaymentInFail paymentInFail = mock(PaymentInFail.class);
		PowerMockito.mockStatic(PaymentInFail.class);
		when(PaymentInFail.valueOf(any(PaymentInModel.class))).thenReturn(paymentInFail);
		
		when(payment.getAmount()).thenReturn(SUCCESS_PAYMENT_AMOUNT);
		when(payment.getCreditCardType()).thenReturn(CreditCardType.VISA, CreditCardType.VISA);
		when(payment.getCreditCardNumber()).thenReturn(VALID_CARD_NUMBER);
		when(payment.getCreditCardName()).thenReturn("");
		when(payment.getStatus()).thenReturn(PaymentStatus.FAILED);

		// invoke first time to reset value from @BeforeClassMethod
		payment.getCreditCardName();

		PaymentInModel model = PaymentInModelFromPaymentInBuilder.valueOf(payment).build().getModel();

		gatewayService.performGatewayOperation(model);
		
		PowerMockito.verifyStatic(times(1));
		PaymentInFail.valueOf(any(PaymentInModel.class));
		verify(paymentInFail).perform();
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

		PaymentInFail paymentInFail = mock(PaymentInFail.class);
		PowerMockito.mockStatic(PaymentInFail.class);
		when(PaymentInFail.valueOf(any(PaymentInModel.class))).thenReturn(paymentInFail);
		
		when(payment.getAmount()).thenReturn(SUCCESS_PAYMENT_AMOUNT);
		when(payment.getCreditCardType()).thenReturn(CreditCardType.VISA, CreditCardType.VISA);
		when(payment.getCreditCardNumber()).thenReturn(INVALID_CARD_NUMBER);
		when(payment.getStatus()).thenReturn(PaymentStatus.FAILED);

		PaymentInModel model = PaymentInModelFromPaymentInBuilder.valueOf(payment).build().getModel();

		gatewayService.performGatewayOperation(model);

		PowerMockito.verifyStatic(times(1));
		PaymentInFail.valueOf(any(PaymentInModel.class));
		verify(paymentInFail).perform();
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

		PaymentInFail paymentInFail = mock(PaymentInFail.class);
		PowerMockito.mockStatic(PaymentInFail.class);
		when(PaymentInFail.valueOf(any(PaymentInModel.class))).thenReturn(paymentInFail);
		
		when(payment.getCreditCardName()).thenReturn(CARD_HOLDER_NAME);
		when(payment.getAmount()).thenReturn(SUCCESS_PAYMENT_AMOUNT, SUCCESS_PAYMENT_AMOUNT);
		when(payment.getCreditCardType()).thenReturn(CreditCardType.VISA, CreditCardType.VISA, CreditCardType.VISA,
				CreditCardType.VISA);
		when(payment.getCreditCardNumber()).thenReturn(VALID_CARD_NUMBER, VALID_CARD_NUMBER);
		when(payment.getCreditCardExpiry()).thenReturn(INVALID_FORMAT_EXPIRY_DATE_STR, EXPIRED_DATE_STR);
		when(payment.getStatus()).thenReturn(PaymentStatus.FAILED);

		// invoke first time to reset value from @BeforeClassMethod
		payment.getCreditCardExpiry();

		PaymentInModel model = PaymentInModelFromPaymentInBuilder.valueOf(payment).build().getModel();

		gatewayService.performGatewayOperation(model);
		
		PowerMockito.verifyStatic(times(1));
		PaymentInFail.valueOf(any(PaymentInModel.class));
		verify(paymentInFail).perform();
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


    @Test
    public void testSuccessGetStatusOperation() throws ServiceException {

        when(payment.getClientReference()).thenReturn("W" + RandomStringUtils.random(10,false,true));

        when(payment.getCreditCardNumber()).thenReturn(VALID_CARD_NUMBER);
        when(payment.getAmount()).thenReturn(SUCCESS_PAYMENT_AMOUNT);
        when(payment.getPaymentTransactions()).thenReturn(Collections.singletonList(paymentTransaction));


        PaymentExpressWSSoap12Stub stub = gatewayService.soapClientStub();

        PaymentInSupport paymentInSupport = new PaymentInSupport(payment,cayenneService);
        TransactionDetails transactionDetails = paymentInSupport.getTransactionDetails();
        SubmitTransactionOperation submitTransactionOperation = new SubmitTransactionOperation(GATEWAY_ACCOUNT,
                GATEWAY_PASSWORD,
                transactionDetails,stub);

        TransactionResult submitResult = submitTransactionOperation.getResult();
        GetStatusOperation getStatusOperation = new GetStatusOperation(GATEWAY_ACCOUNT, GATEWAY_PASSWORD, transactionDetails.getTxnRef(),stub);
        TransactionResult getStatusResult = getStatusOperation.getResult();

        assertTrue("submitResult is SUCCESS", PaymentExpressUtil.translateFlag(submitResult.getResult2().getAuthorized()));
        assertTrue("getStatusResult is SUCCESS", PaymentExpressUtil.translateFlag(getStatusResult.getResult2().getAuthorized()));

        assertGetStatusResult(submitResult.getResult2(), getStatusResult.getResult2());
    }


    @Test
    public void testUnsuccessfulGetStatusOperation() throws ServiceException {
        when(payment.getClientReference()).thenReturn("O" + RandomStringUtils.random(10,false,true));

        when(payment.getCreditCardNumber()).thenReturn(INVALID_CARD_NUMBER);
        when(payment.getAmount()).thenReturn(SUCCESS_PAYMENT_AMOUNT);
        when(payment.getPaymentTransactions()).thenReturn(Collections.singletonList(paymentTransaction));


        PaymentExpressWSSoap12Stub stub = gatewayService.soapClientStub();

        PaymentInSupport paymentInSupport = new PaymentInSupport(payment,cayenneService);
        TransactionDetails transactionDetails = paymentInSupport.getTransactionDetails();
        SubmitTransactionOperation submitTransactionOperation = new SubmitTransactionOperation(GATEWAY_ACCOUNT,
                GATEWAY_PASSWORD,
                transactionDetails,stub);

        TransactionResult submitResult = submitTransactionOperation.getResult();
        GetStatusOperation getStatusOperation = new GetStatusOperation(GATEWAY_ACCOUNT, GATEWAY_PASSWORD, transactionDetails.getTxnRef(),stub);
        TransactionResult getStatusResult = getStatusOperation.getResult();

        assertFalse("submitResult is FAILED", PaymentExpressUtil.translateFlag(submitResult.getResult2().getAuthorized()));
        assertFalse("getStatusResult is FAILED", PaymentExpressUtil.translateFlag(getStatusResult.getResult2().getAuthorized()));

        assertGetStatusResult(submitResult.getResult2(), getStatusResult.getResult2());
    }

	@Test
	public void testBillingId() throws ServiceException {
		when(payment.getCreditCardNumber()).thenReturn(VALID_CARD_NUMBER);
		when(payment.getAmount()).thenReturn(new Money("200.00"));
		when(payment.getPaymentTransactions()).thenReturn(Collections.singletonList(paymentTransaction));
		
		TransactionResult tr = gatewayService.doTransaction(payment, null);
		assertNotNull("Transaction result should be not empty for successfull payment", tr);
		boolean isAuthorized = PaymentExpressUtil.translateFlag(tr.getResult2().getAuthorized());
		assertTrue("Check if authorized.", isAuthorized);
		
		String billingId = tr.getResult2().getDpsBillingId();
		assertTrue(billingId != null);


		when(payment.getCreditCardNumber()).thenReturn(null);
		when(payment.getCreditCardExpiry()).thenReturn(null);
		when(payment.getCreditCardName()).thenReturn(null);
		when(payment.getCreditCardCVV()).thenReturn(null);
		when(payment.getCreditCardType()).thenReturn(null);
		when(payment.getAmount()).thenReturn(new Money("300.00"));

		//supply billingId from previous CC payment 
		tr = gatewayService.doTransaction(payment, billingId);
		assertNotNull("Transaction result should be not empty for successfull payment", tr);
		isAuthorized = PaymentExpressUtil.translateFlag(tr.getResult2().getAuthorized());
		assertTrue("Check if authorized.", isAuthorized);

		//billings for both transactions should be identical
		assertEquals(billingId, tr.getResult2().getDpsBillingId());

	}

	@Test
	public void testCheckTransaction() throws ServiceException {
		when(payment.getCreditCardNumber()).thenReturn(VALID_CARD_NUMBER);
		when(payment.getAmount()).thenReturn(new Money("200.00"));
		when(payment.getPaymentTransactions()).thenReturn(Collections.singletonList(paymentTransaction));

		TransactionResult tr = gatewayService.doTransaction(payment, null);
		assertNotNull("Transaction result should be not empty for successfull payment", tr);
		boolean isAuthorized = PaymentExpressUtil.translateFlag(tr.getResult2().getAuthorized());
		assertTrue("Check if authorized.", isAuthorized);

		String originalResponse = tr.getResult2().getResponseText();
		String originalReference = tr.getResult2().getDpsTxnRef();
		String originalBillingId = tr.getResult2().getDpsBillingId();
		
		TransactionResult tr2 = gatewayService.checkPaymentTransaction(payment);
		
		assertNotNull("Check if authorized.", tr2.getResult2());
		assertEquals(TransactionResult.ResultStatus.SUCCESS,  tr2.getStatus());
		
		assertEquals(originalResponse, tr2.getResult2().getResponseText());
		assertEquals(originalReference, tr2.getResult2().getDpsTxnRef());
//		assertEquals(originalBillingId, tr2.getResult2().getDpsBillingId());

	}

    private void assertGetStatusResult(TransactionResult2 submitResult, TransactionResult2 getStatusResult) {
        assertEquals("Test getAuthorized",submitResult.getAuthorized(), getStatusResult.getAuthorized());
//        assertEquals(submitResult.getAcquirerReco(), getStatusResult.getAcquirerReco());
        assertEquals("Test getAcquirerResponseText",submitResult.getAcquirerResponseText().toLowerCase(), getStatusResult.getAcquirerResponseText().toLowerCase());
        assertEquals("Test getAmount",submitResult.getAmount(), getStatusResult.getAmount());
        assertEquals("Test getAuthCode",submitResult.getAuthCode(), getStatusResult.getAuthCode());
        assertEquals("Test getBillingId",submitResult.getBillingId(), getStatusResult.getBillingId());
        assertEquals("Test getCardHolderHelpText",submitResult.getCardHolderHelpText(), getStatusResult.getCardHolderHelpText());
        assertEquals("Test getCardHolderName",submitResult.getCardHolderName(), getStatusResult.getCardHolderName());
        assertEquals("Test getCardHolderResponseDescription",submitResult.getCardHolderResponseDescription(), getStatusResult.getCardHolderResponseDescription());
        assertEquals("Test getCardHolderResponseText",submitResult.getCardHolderResponseText(), getStatusResult.getCardHolderResponseText());
        assertEquals("Test getCardName",submitResult.getCardName(), getStatusResult.getCardName());
        assertEquals("Test getCardNumber",submitResult.getCardNumber(), getStatusResult.getCardNumber());
//        assertEquals(submitResult.getCurrencyName(), getStatusResult.getCurrencyName());
        assertEquals("Test getCurrencyRate",submitResult.getCurrencyRate(), getStatusResult.getCurrencyRate());
        assertEquals("Test getCvc2",submitResult.getCvc2(), getStatusResult.getCvc2());
//        assertEquals(submitResult.getDateExpiry(), getStatusResult.getDateExpiry());
        assertEquals("Test getDateSettlement",submitResult.getDateSettlement(), getStatusResult.getDateSettlement());
//        assertEquals("Test getDpsBillingId",submitResult.getDpsBillingId(), getStatusResult.getDpsBillingId());
//        assertEquals(submitResult.getDpsTxnRef(), getStatusResult.getDpsTxnRef());
        assertEquals("Test getHelpText",submitResult.getHelpText(), getStatusResult.getHelpText());
        assertEquals("Test getIccData",submitResult.getIccData(), getStatusResult.getIccData());
        assertEquals("Test getIssuerCountryId",submitResult.getIssuerCountryId(), getStatusResult.getIssuerCountryId());
//        assertEquals(submitResult.getMerchantHelpText(), getStatusResult.getMerchantHelpText());
        assertEquals("Test getMerchantReference",submitResult.getMerchantReference(), getStatusResult.getMerchantReference());
        assertEquals("Test getMerchantResponseDescription",submitResult.getMerchantResponseDescription(), getStatusResult.getMerchantResponseDescription());
        assertEquals("Test getMerchantResponseText",submitResult.getMerchantResponseText(), getStatusResult.getMerchantResponseText());
        assertEquals("Test getReco",submitResult.getReco(), getStatusResult.getReco());
        assertEquals("Test getResponseText",submitResult.getResponseText(), getStatusResult.getResponseText());
        assertEquals("Test getRetry",submitResult.getRetry(), getStatusResult.getRetry());
        assertEquals("Test getStatusRequired",submitResult.getStatusRequired(), getStatusResult.getStatusRequired());
        assertEquals("Test getTestMode",submitResult.getTestMode(), getStatusResult.getTestMode());
//        assertEquals("Test ",submitResult.getTxnMac(), getStatusResult.getTxnMac());
        assertEquals("Test getTxnRef",submitResult.getTxnRef(), getStatusResult.getTxnRef());
        assertEquals("Test getTxnType", submitResult.getTxnType(), getStatusResult.getTxnType());
    }

}
