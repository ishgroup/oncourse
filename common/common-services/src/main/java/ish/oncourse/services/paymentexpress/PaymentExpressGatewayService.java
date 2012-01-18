package ish.oncourse.services.paymentexpress;

import ish.common.types.PaymentStatus;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentOut;
import ish.oncourse.model.PaymentOutTransaction;
import ish.oncourse.model.PaymentTransaction;
import ish.util.CreditCardUtil;

import java.util.Date;

import javax.xml.rpc.ServiceException;

import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.commons.lang.StringUtils;
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
	 * {@inheritDoc} Performs Payment Express gateway.
	 * 
	 * @see AbstractPaymentGatewayService#processGateway(ish.oncourse.model.PaymentIn)
	 */
	@Override
	public void processGateway(PaymentIn payment) {
		
		TransactionResult tr = null;
		PaymentTransaction paymentTransaction = null;
		try {

			tr = doTransaction(payment);
			if (!payment.getPaymentTransactions().isEmpty()) {
				//prepare to log the result
				paymentTransaction = payment.getPaymentTransactions().get(0);
			}
			StringBuilder resultDetails = new StringBuilder();
			
			if (tr != null) {
				if (paymentTransaction != null) {
					//log the result
					paymentTransaction.setSoapResponse(tr.getMerchantHelpText());
				}
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

				resultDetails.append(" authCode:").append(tr.getAuthCode()).append(", authorized:")
						.append(tr.getAuthorized()).append(", cardHolderHelpText:").append(tr.getCardHolderHelpText())
						.append(", cardHolderName:").append(tr.getCardHolderName())
						.append(", cardHolderResponseDescription:").append(tr.getCardHolderResponseDescription())
						.append(", currencyRate:").append(tr.getCurrencyRate()).append(", currencyType:")
						.append(tr.getCurrencyName()).append(", ourTransactionRef:").append(tr.getTxnRef())
						.append(", responseCode:").append(tr.getReco()).append(", responseText:")
						.append(tr.getResponseText()).append(", retry:").append(tr.getRetry())
						.append(", settlementDate:").append(tr.getDateSettlement()).append(", statusRequired:")
						.append(tr.getStatusRequired()).append(", testMode:").append(tr.getTestMode())
						.append(", transactionRef:").append(tr.getDpsTxnRef());
			} else {
				resultDetails.append("Payment failed with null transaction response");
				payment.setStatusNotes("Payment failed. Null transaction response.");
				if (paymentTransaction != null) {
					//log the result
					paymentTransaction.setSoapResponse(getResponseStringFromContext());
				}
				payment.failPayment();
			}
			
			LOG.debug(resultDetails.toString());

		} catch (Exception e) {
			if (!payment.getPaymentTransactions().isEmpty()) {
				//log the result
				paymentTransaction = payment.getPaymentTransactions().get(0);
				paymentTransaction.setSoapResponse(e.getMessage());
			}
			LOG.error(String.format("PaymentIn id:%s failed with exception.", payment.getId()), e);
			payment.setStatusNotes("PaymentIn failed with exception.");
			payment.failPayment();
		}
		if (paymentTransaction != null) {
			paymentTransaction.setSoapRequest(getRequestString());
		}
	}

	@Override
	public void processGateway(PaymentOut paymentOut) {

		TransactionResult tr;
		PaymentOutTransaction paymentTransaction = null;
		try {
			tr = doTransaction(paymentOut);
			if (!paymentOut.getPaymentOutTransactions().isEmpty()) {
				paymentTransaction = paymentOut.getPaymentOutTransactions().get(0);
			}
			StringBuilder resultDetails = new StringBuilder();
			if (tr != null) {
				if (paymentTransaction != null) {
					//log the result
					paymentTransaction.setSoapResponse(tr.getMerchantHelpText());
				}
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

				resultDetails.append(" authCode:").append(tr.getAuthCode()).append(", authorized:")
						.append(tr.getAuthorized()).append(", cardHolderHelpText:").append(tr.getCardHolderHelpText())
						.append(", cardHolderName:").append(tr.getCardHolderName())
						.append(", cardHolderResponseDescription:").append(tr.getCardHolderResponseDescription())
						.append(", currencyRate:").append(tr.getCurrencyRate()).append(", currencyType:")
						.append(tr.getCurrencyName()).append(", ourTransactionRef:").append(tr.getTxnRef())
						.append(", responseCode:").append(tr.getReco()).append(", responseText:")
						.append(tr.getResponseText()).append(", retry:").append(tr.getRetry())
						.append(", settlementDate:").append(tr.getDateSettlement()).append(", statusRequired:")
						.append(tr.getStatusRequired()).append(", testMode:").append(tr.getTestMode())
						.append(", transactionRef:").append(tr.getDpsTxnRef());
			} else {
				resultDetails.append("PaymentOut failed with null transaction response.");
				paymentOut.setStatusNotes("PaymentOut failed with null transaction response.");
				if (paymentTransaction != null) {
					//log the result
					paymentTransaction.setSoapResponse(getResponseStringFromContext());
				}
				paymentOut.failed();
			}
			
			LOG.debug(resultDetails.toString());
			
		} catch (Exception e) {
			if (!paymentOut.getPaymentOutTransactions().isEmpty()) {
				//log the result
				paymentTransaction = paymentOut.getPaymentOutTransactions().get(0);
				paymentTransaction.setSoapResponse(e.getMessage());
			}
			LOG.error(String.format("PaymentOut id:%s failed with exception.", paymentOut.getId()), e);
			paymentOut.setStatusNotes("PaymentOut failed with exception.");
			paymentOut.failed();
		}
		if (paymentTransaction != null) {
			paymentTransaction.setSoapRequest(getRequestString());
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

		LOG.debug("Submitting payment to paymentexpress, gatewayAccount: " + username + ", gatewayPassword: "
				+ password);
		
		int n = 0;
		boolean shouldRetry = true;
		
		TransactionResult result = null;
		
		while (shouldRetry && n < NUMBER_OF_ATTEMPTS) {
			
			initNewPaymentTransaction(payment);
			
			result = stub.submitTransaction(username, password, transactionDetails);
			
			if (result != null) {
				PaymentTransaction t = payment.getActiveTransaction();
				t.setResponse(result.getResponseText());
				t.setTxnReference(result.getTxnRef());
				// in any case, this transaction is completed
				t.setIsFinalised(true);
				shouldRetry = PaymentExpressUtil.translateFlag(result.getRetry());
				
				if (shouldRetry) {
					Thread.sleep(RETRY_INTERVAL);
				}
			}
			
			n++;
		}
		
		if (result != null) {
			if(PaymentExpressUtil.translateFlag(result.getAuthorized())) {
				payment.setGatewayResponse(result.getResponseText());
				payment.setGatewayReference(result.getDpsTxnRef());
			}
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
		
		LOG.debug("Submitting payment to paymentexpress, gatewayAccount: " + username + ", gatewayPassword: "
				+ password);
		
		int n = 0;
		boolean shouldRetry = true;
		TransactionResult result = null;
		
		while (shouldRetry && n < NUMBER_OF_ATTEMPTS) {
			initNewPaymentOutTransaction(paymentOut);
			result = stub.submitTransaction(username, password, transactionDetails);
			if (result != null) {
				PaymentOutTransaction t = paymentOut.getActiveTransaction();
				t.setResponse(result.getResponseText());
				t.setTxnReference(result.getTxnRef());
				// in any case, this transaction is completed
				t.setIsFinalised(true);
				shouldRetry = PaymentExpressUtil.translateFlag(result.getRetry());
				
				if (shouldRetry) {
					Thread.sleep(RETRY_INTERVAL);
				}
			}
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

	protected void initNewPaymentTransaction(PaymentIn payment) {
		PaymentTransaction paymentTransaction = payment.getObjectContext().newObject(PaymentTransaction.class);
		paymentTransaction.setPayment(payment);
	}

	@Override
	protected void initNewPaymentOutTransaction(PaymentOut paymentOut) {
		PaymentOutTransaction refundTransaction =paymentOut.getObjectContext().newObject(PaymentOutTransaction.class);
		refundTransaction.setPaymentOut(paymentOut);
	}
	
	private PaymentExpressWSSoap12Stub soapClientStub() throws ServiceException {
		PaymentExpressWSLocator serviceLocator = new PaymentExpressWSLocator();
		serviceLocator.setPaymentExpressWSSoapEndpointAddress("https://sec.paymentexpress.com/WSV1/PXWS.asmx");
		PaymentExpressWSSoap12Stub stub = (PaymentExpressWSSoap12Stub) serviceLocator.getPaymentExpressWSSoap12();
		stub.setTimeout(TIMEOUT);
		return stub;
	}
	
	private String getRequestString() {
		try {
			MessageContext context = MessageContext.getCurrentContext(); 
			Message message = context.getRequestMessage();
			return message.getSOAPPartAsString();
		} catch (Exception e) {
			LOG.warn("Can not get soap request.", e);
		} 
		return StringUtils.EMPTY;
	}
	
	private String getResponseStringFromContext() {
		try {
			MessageContext context = MessageContext.getCurrentContext(); 
			Message message = context.getResponseMessage();
			return message.getSOAPPartAsString();
		} catch (Exception e) {
			LOG.warn("Can not get soap request.", e);
		} 
		return StringUtils.EMPTY;
	}
}
