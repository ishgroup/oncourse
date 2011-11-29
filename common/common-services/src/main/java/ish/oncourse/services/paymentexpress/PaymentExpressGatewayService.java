package ish.oncourse.services.paymentexpress;

import ish.common.types.PaymentStatus;
import ish.common.types.PaymentType;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentOut;
import ish.oncourse.model.PaymentOutTransaction;
import ish.oncourse.model.PaymentTransaction;
import ish.util.CreditCardUtil;

import java.rmi.RemoteException;
import java.util.Date;

import javax.xml.rpc.ServiceException;

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

		try {

			tr = doTransaction(payment);

			StringBuilder resultDetails = new StringBuilder();
			
			if (tr != null) {

				if (PaymentExpressUtil.translateFlag(tr.getAuthorized())) {
					resultDetails.append("Payment succeed.");
					payment.succeed();
					payment.setDateBanked(PaymentExpressUtil.translateSettlementDate(tr.getDateSettlement()));
				} else {
					resultDetails.append("Payment failed.");
					payment.setStatusNotes("Card declined");
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
				payment.setStatusNotes("Null transaction response");
				payment.failPayment();
			}
			LOG.debug(resultDetails.toString());
		} catch (RemoteException e) {
			if (PaymentStatus.SUCCESS.equals(payment.getStatus()) && PaymentType.CREDIT_CARD.equals(payment.getType())) {
				payment.succeed();
			} else {
				payment.failPayment();
				payment.setStatusNotes("Failed to obtain a status for transaction");
				LOG.error("RemoteException submitting to paymentexpress", e);
			}
		} catch (Exception e) {
			if (PaymentStatus.SUCCESS.equals(payment.getStatus()) && PaymentType.CREDIT_CARD.equals(payment.getType())) {
				payment.succeed();
			} else {
				payment.failPayment();
				payment.setStatusNotes("Failed to obtain a status for transaction");
				LOG.error("Failed to obtain a status for transaction", e);
			}
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
					resultDetails.append("Refund succeed.");
					paymentOut.succeed();
					paymentOut.setDateBanked(PaymentExpressUtil.translateSettlementDate(tr.getDateSettlement()));
					paymentOut.setDatePaid(new Date());
				} else {
					// TODO set statusNotes="cardDeclined" to payment here
					resultDetails.append("Refund failed.");
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
				resultDetails.append("Payment failed with null transaction response");
				paymentOut.failed();
			}
			LOG.debug(resultDetails.toString());
		} catch (RemoteException e) {
			LOG.error("RemoteException submitting to paymentexpress", e);
			paymentOut.failed();
		} catch (Exception e) {
			paymentOut.failed();
			LOG.error("Failed to obtain a status for transaction", e);
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
}
