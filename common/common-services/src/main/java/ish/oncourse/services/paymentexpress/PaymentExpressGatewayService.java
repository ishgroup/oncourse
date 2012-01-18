package ish.oncourse.services.paymentexpress;

import ish.common.types.PaymentStatus;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentOut;
import ish.oncourse.model.PaymentOutTransaction;
import ish.oncourse.model.PaymentTransaction;
import ish.oncourse.services.persistence.ICayenneService;
import ish.util.CreditCardUtil;

import java.util.Date;

import javax.xml.rpc.ServiceException;

import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.cayenne.ObjectContext;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Scope;

import com.paymentexpress.stubs.PaymentExpressWSLocator;
import com.paymentexpress.stubs.PaymentExpressWSSoap12Stub;
import com.paymentexpress.stubs.TransactionDetails;
import com.paymentexpress.stubs.TransactionResult;

/**
 * Payment Express gateway processing. {@inheritDoc} <br/>
 * Uses the external web service. See
 * http://www.paymentexpress.com/technical_resources
 * /ecommerce_nonhosted/webservice.html
 * 
 * @author ksenia
 * 
 */
@Scope("perthread")
public class PaymentExpressGatewayService extends AbstractPaymentGatewayService {

	/**
	 * The number of retry attempts.
	 */
	private static final int NUMBER_OF_ATTEMPTS = 5;

	/**
	 * Retry interval.
	 */
	private static final int RETRY_INTERVAL = 2000;

	/**
	 * Webservices calls timeout.
	 */
	private static final int TIMEOUT = 1000 * 120;

	/**
	 * Logger for service.
	 */
	private static final Logger LOG = Logger.getLogger(PaymentExpressGatewayService.class);

	/**
	 * Cayenne service
	 */
	private ICayenneService cayenneService;

	/**
	 * {@inheritDoc} Performs Payment Express gateway.
	 * 
	 * @see AbstractPaymentGatewayService#processGateway(ish.oncourse.model.PaymentIn)
	 */
	@Override
	public void processGateway(PaymentIn payment) {

		TransactionResult tr = null;

		try {

			tr = doTransaction(payment);

			StringBuilder resultDetails = new StringBuilder();

			if (tr != null) {

				if (PaymentExpressUtil.translateFlag(tr.getAuthorized())) {
					resultDetails.append("Payment succeed.");
					payment.setStatusNotes("Payment succeed.");
					payment.succeed();
					payment.setDateBanked(PaymentExpressUtil.translateSettlementDate(tr.getDateSettlement()));
				} else {
					resultDetails.append("Payment failed. Card declined.");
					payment.setStatusNotes("Payment failed. Card declined");
					payment.setStatus(PaymentStatus.FAILED_CARD_DECLINED);
					payment.failPayment();
				}

				resultDetails.append(" authCode:").append(tr.getAuthCode()).append(", authorized:").append(tr.getAuthorized())
						.append(", cardHolderHelpText:").append(tr.getCardHolderHelpText()).append(", cardHolderName:")
						.append(tr.getCardHolderName()).append(", cardHolderResponseDescription:")
						.append(tr.getCardHolderResponseDescription()).append(", currencyRate:").append(tr.getCurrencyRate())
						.append(", currencyType:").append(tr.getCurrencyName()).append(", ourTransactionRef:").append(tr.getTxnRef())
						.append(", responseCode:").append(tr.getReco()).append(", responseText:").append(tr.getResponseText())
						.append(", retry:").append(tr.getRetry()).append(", settlementDate:").append(tr.getDateSettlement())
						.append(", statusRequired:").append(tr.getStatusRequired()).append(", testMode:").append(tr.getTestMode())
						.append(", transactionRef:").append(tr.getDpsTxnRef());
			} else {
				resultDetails.append("Payment failed with null transaction response");
				payment.setStatusNotes("Payment failed. Null transaction response.");
				payment.failPayment();
			}

			LOG.debug(resultDetails.toString());

		} catch (Exception e) {
			LOG.error(String.format("PaymentIn id:%s failed with exception.", payment.getId()), e);
			payment.setStatusNotes("PaymentIn failed with exception.");
			payment.failPayment();
		}
	}

	@Override
	public void processGateway(PaymentOut paymentOut) {

		TransactionResult tr;

		try {
			tr = doTransaction(paymentOut);

			StringBuilder resultDetails = new StringBuilder();
			if (tr != null) {
				if (PaymentExpressUtil.translateFlag(tr.getAuthorized())) {
					resultDetails.append("PaymentOut succeed.");
					paymentOut.setStatusNotes("PaymentOut succeed.");
					paymentOut.succeed();
					paymentOut.setDateBanked(PaymentExpressUtil.translateSettlementDate(tr.getDateSettlement()));
					paymentOut.setDatePaid(new Date());
				} else {
					// TODO set statusNotes="cardDeclined" to payment here
					resultDetails.append("PaymentOut failed. Declined by paymentExpress.");
					paymentOut.setStatusNotes("PaymentOut failed. Declined by paymentExpress.");
					paymentOut.setStatus(PaymentStatus.FAILED_CARD_DECLINED);
					paymentOut.failed();
				}

				resultDetails.append(" authCode:").append(tr.getAuthCode()).append(", authorized:").append(tr.getAuthorized())
						.append(", cardHolderHelpText:").append(tr.getCardHolderHelpText()).append(", cardHolderName:")
						.append(tr.getCardHolderName()).append(", cardHolderResponseDescription:")
						.append(tr.getCardHolderResponseDescription()).append(", currencyRate:").append(tr.getCurrencyRate())
						.append(", currencyType:").append(tr.getCurrencyName()).append(", ourTransactionRef:").append(tr.getTxnRef())
						.append(", responseCode:").append(tr.getReco()).append(", responseText:").append(tr.getResponseText())
						.append(", retry:").append(tr.getRetry()).append(", settlementDate:").append(tr.getDateSettlement())
						.append(", statusRequired:").append(tr.getStatusRequired()).append(", testMode:").append(tr.getTestMode())
						.append(", transactionRef:").append(tr.getDpsTxnRef());
			} else {
				resultDetails.append("PaymentOut failed with null transaction response.");
				paymentOut.setStatusNotes("PaymentOut failed with null transaction response.");
				paymentOut.failed();
			}

			LOG.debug(resultDetails.toString());

		} catch (Exception e) {
			LOG.error(String.format("PaymentOut id:%s failed with exception.", paymentOut.getId()), e);
			paymentOut.setStatusNotes("PaymentOut failed with exception.");
			paymentOut.failed();
		}
	}

	/**
	 * Performs the payment transaction trought the payment express gateway.
	 * 
	 * @param payment
	 *            the paymment to be processed.
	 * @return the result of submitted transaction.
	 * @throws Exception
	 */
	public TransactionResult doTransaction(PaymentIn payment) throws Exception {

		PaymentExpressWSSoap12Stub stub = soapClientStub();

		// "ISHPaymentExpress.testingGatewayAccount", "ishGroup_Dev"
		String username = payment.getCollege().getPaymentGatewayAccount();
		// "ISHPaymentExpress.testingGatewayPass", "test1234"
		String password = payment.getCollege().getPaymentGatewayPass();
		TransactionDetails transactionDetails = getTransactionDetails(payment);

		LOG.debug("Submitting payment to paymentexpress, gatewayAccount: " + username + ", gatewayPassword: " + password);

		int n = 0;
		boolean shouldRetry = true;

		TransactionResult result = null;
		
		while (shouldRetry && n < NUMBER_OF_ATTEMPTS) {

			PaymentTransaction t = null;

			try {
				t = createNewPaymentTransaction(payment); 
				result = stub.submitTransaction(username, password, transactionDetails);
				
				t.setSoapResponse(getSoapResponseAsString());
				
				if (result != null) {
					t.setResponse(result.getResponseText());
					t.setTxnReference(result.getTxnRef());
					t.setIsFinalised(true);// in any case, this transaction is completed
					shouldRetry = PaymentExpressUtil.translateFlag(result.getRetry());

					if (shouldRetry) {
						Thread.sleep(RETRY_INTERVAL);
					}
				}

				n++;
				
			} finally {
				if (t != null) {
					t.getObjectContext().commitChanges();
				}
			}			
		}

		if (result != null) {
			if (PaymentExpressUtil.translateFlag(result.getAuthorized())) {
				payment.setGatewayResponse(result.getResponseText());
				payment.setGatewayReference(result.getDpsTxnRef());
			}
		}
		else {
			throw new Exception(String.format("Got null paymentIn response from PaymentExpress. After %s retries.", n));
		}

		return result;
	}

	/**
	 * Performs the payment transaction trought the payment express gateway.
	 * 
	 * @param payment
	 *            the paymment to be processed.
	 * @return the result of submitted transaction.
	 * @throws Exception
	 */
	public TransactionResult doTransaction(PaymentOut paymentOut) throws Exception {

		PaymentExpressWSSoap12Stub stub = soapClientStub();

		// "ISHPaymentExpress.testingGatewayAccount", "ishGroup_Dev"
		String username = paymentOut.getCollege().getPaymentGatewayAccount();
		// "ISHPaymentExpress.testingGatewayPass", "test1234"
		String password = paymentOut.getCollege().getPaymentGatewayPass();

		TransactionDetails transactionDetails = getTransactionDetails(paymentOut);

		LOG.debug("Submitting payment to paymentexpress, gatewayAccount: " + username + ", gatewayPassword: " + password);

		int n = 0;
		boolean shouldRetry = true;
		TransactionResult result = null;

		while (shouldRetry && n < NUMBER_OF_ATTEMPTS) {
			
			PaymentOutTransaction t = null;
			
			try {
				t = createNewPaymentOutTransaction(paymentOut);
				result = stub.submitTransaction(username, password, transactionDetails);
				
				t.setSoapResponse(getSoapResponseAsString());
				
				if (result != null) {
					t.setResponse(result.getResponseText());
					t.setTxnReference(result.getTxnRef());
					t.setIsFinalised(true);// in any case, this transaction is
											// completed
					shouldRetry = PaymentExpressUtil.translateFlag(result.getRetry());

					if (shouldRetry) {
						Thread.sleep(RETRY_INTERVAL);
					}
				}
				
				n++;
			}
			finally {
				if (t != null) {
					t.getObjectContext().commitChanges();
				}
			}
		}
		
		if (result == null) {
			throw new Exception(String.format("Got null paymentOut response from PaymentExpress. After %s retries.", n));
		}

		return result;
	}

	/**
	 * Creates the {@link TransactionDetails} object for the given payment for
	 * the further processing.
	 * 
	 * @param payment
	 *            the given payment.
	 * @return the created deatils object.
	 */
	public TransactionDetails getTransactionDetails(PaymentIn payment) {
		TransactionDetails details = new TransactionDetails();

		StringBuilder transactionDetails = new StringBuilder("Preparing payment transaction data. ");

		details.setAmount(PaymentExpressUtil.translateInputAmountAsDecimalString(payment.getAmount()));
		transactionDetails.append("amount: ").append(details.getAmount());

		details.setCardHolderName(payment.getCreditCardName());
		transactionDetails.append(", cardHolderName: ").append(details.getCardHolderName());

		details.setCardNumber(payment.getCreditCardNumber());
		transactionDetails.append(", cardNumber: ").append(CreditCardUtil.obfuscateCCNumber(details.getCardNumber()));

		details.setCvc2(payment.getCreditCardCVV());
		transactionDetails.append(", cardCVV: ").append(CreditCardUtil.obfuscateCVVNumber(details.getCvc2()));

		details.setDateExpiry(PaymentExpressUtil.translateInputExpiryDate(payment.getCreditCardExpiry()));
		transactionDetails.append(", cardExpiry: ").append(details.getDateExpiry());

		// TODO use other currencies
		details.setInputCurrency("AUD");
		transactionDetails.append(", currency: ").append(details.getInputCurrency());

		String ref = payment.getClientReference();

		details.setMerchantReference(ref);
		transactionDetails.append(", merchantReference: ").append(details.getMerchantReference());

		details.setTxnRef(ref);
		transactionDetails.append(", transactionReference: ").append(details.getTxnRef());

		details.setTxnType(PaymentExpressUtil.PAYMENT_EXPRESS_TXN_TYPE);
		transactionDetails.append(", transaction type: ").append(details.getTxnType());

		LOG.debug(transactionDetails.toString());

		return details;
	}

	public TransactionDetails getTransactionDetails(PaymentOut paymentOut) {

		TransactionDetails details = new TransactionDetails();

		StringBuilder transactionDetails = new StringBuilder("Preparing payment transaction data. ");

		details.setAmount(PaymentExpressUtil.translateInputAmountAsDecimalString(paymentOut.getTotalAmount()));
		transactionDetails.append("amount: ").append(details.getAmount());

		details.setDpsTxnRef(paymentOut.getPaymentInTxnReference());

		details.setInputCurrency("AUD");
		transactionDetails.append(", currency: ").append(details.getInputCurrency());

		String ref = paymentOut.getClientReference();
		details.setMerchantReference(ref);
		details.setTxnRef(ref);
		details.setTxnType(PaymentExpressUtil.PAYMENT_EXPRESS_TXN_TYPE_REFUND);

		LOG.debug(transactionDetails.toString());

		return details;
	}

	/**
	 * Creates a new PaymentTransaction for paymentIn
	 * 
	 * @param payment
	 * @return
	 */
	private PaymentTransaction createNewPaymentTransaction(PaymentIn payment) {
		ObjectContext newObjectContext = cayenneService.newNonReplicatingContext();
		PaymentTransaction paymentTransaction = newObjectContext.newObject(PaymentTransaction.class);
		PaymentIn local = (PaymentIn) newObjectContext.localObject(payment.getObjectId(), null);
		paymentTransaction.setPayment(local);
		return paymentTransaction;
	}

	/**
	 * Creates a new PaymentOutTransaction for paymentOut
	 * 
	 * @param paymentOut
	 * @return
	 */
	private PaymentOutTransaction createNewPaymentOutTransaction(PaymentOut paymentOut) {
		ObjectContext newObjectContext = cayenneService.newNonReplicatingContext();
		PaymentOutTransaction refundTransaction = newObjectContext.newObject(PaymentOutTransaction.class);
		PaymentOut local = (PaymentOut) newObjectContext.localObject(paymentOut.getObjectId(), null);
		refundTransaction.setPaymentOut(local);
		return refundTransaction;
	}

	/**
	 * Initializes soap client.
	 * 
	 * @return
	 * @throws ServiceException
	 */
	private PaymentExpressWSSoap12Stub soapClientStub() throws ServiceException {
		PaymentExpressWSLocator serviceLocator = new PaymentExpressWSLocator();
		serviceLocator.setPaymentExpressWSSoapEndpointAddress("https://sec.paymentexpress.com/WSV1/PXWS.asmx");
		PaymentExpressWSSoap12Stub stub = (PaymentExpressWSSoap12Stub) serviceLocator.getPaymentExpressWSSoap12();
		stub.setTimeout(TIMEOUT);
		return stub;
	}
	
	/**
	 * Gets soap response of the current webservice call.
	 * 
	 * @return
	 */
	private String getSoapResponseAsString() {
		try {
			MessageContext context = MessageContext.getCurrentContext();
			Message message = context.getResponseMessage();
			return message.getSOAPPartAsString();
		} catch (Exception e) {
			LOG.warn("Can not get soap request.", e);
		}
		return null;
	}
}
