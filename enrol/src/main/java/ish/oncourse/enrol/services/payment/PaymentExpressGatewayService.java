package ish.oncourse.enrol.services.payment;

import ish.oncourse.enrol.utils.PaymentExpressUtil;
import ish.oncourse.model.PaymentIn;

import java.rmi.RemoteException;

import org.apache.log4j.Logger;

import com.paymentexpress.stubs.PaymentExpressWSLocator;
import com.paymentexpress.stubs.PaymentExpressWSSoap;
import com.paymentexpress.stubs.TransactionDetails;
import com.paymentexpress.stubs.TransactionResult;

/**
 * Payment Express gateway processing. {@inheritDoc}
 * 
 * @author ksenia
 * 
 */
public class PaymentExpressGatewayService implements IPaymentGatewayService {
	/**
	 * Logger for service.
	 */
	private static final Logger LOG = Logger.getLogger(PaymentExpressGatewayService.class);

	/**
	 * Performs Payment Express gateway. {@inheritDoc}
	 * 
	 * @see ish.oncourse.enrol.services.payment.IPaymentGatewayService#performGatewayOperation(ish.oncourse.model.PaymentIn)
	 */
	@Override
	public void performGatewayOperation(PaymentIn payment) {
		TransactionResult tr;
		try {
			tr = doTransaction(payment);
			StringBuilder resultDetails = new StringBuilder();
			if (tr != null) {
				if (PaymentExpressUtil.translateFlag(tr.getAuthorized())) {
					resultDetails.append("Payment succeed.");
					payment.succeed();
					
				} else {
					// TODO set statusNotes="cardDeclined" to payment here
					resultDetails.append("Payment failed.");
					payment.failed();
				}

				resultDetails.append(" authCode:").append(tr.getAuthCode())
						.append(" authorized:").append(tr.getAuthorized())
						.append(" cardHolderHelpText:").append(tr.getCardHolderHelpText())
						.append(" cardHolderName:").append(tr.getCardHolderName())
						.append(" cardHolderResponseDescription:").append(tr.getCardHolderResponseDescription())
						.append(" currencyRate:").append(tr.getCurrencyRate())
						.append(" currencyType:").append(tr.getCurrencyName())
						.append(" ourTransactionRef:").append(tr.getTxnRef())
						.append(" responseCode:").append(tr.getReco())
						.append(" responseText:").append(tr.getResponseText())
						.append(" retry:").append(tr.getRetry())
						.append(" settlementDate:").append(tr.getDateSettlement())
						.append(" statusRequired:").append(tr.getStatusRequired())
						.append(" testMode:").append(tr.getTestMode())
						.append(" transactionRef:").append(tr.getDpsTxnRef());
			}else{
				resultDetails.append("Payment failed with null transaction response");
				payment.failed();
			}
			LOG.debug(resultDetails.toString());
		} catch (RemoteException e) {
			LOG.error("RemoteException submitting to paymentexpress", e);
			payment.failed();
		} catch (Exception e) {
			payment.failed();
			LOG.error("Failed to obtain a status for transaction", e);
		}
	}

	/**
	 * Performs the payment transaction trought the payment express gateway.
	 * @param payment the paymment to be processed.
	 * @return the result of submitted transaction.
	 * @throws Exception
	 */
	public TransactionResult doTransaction(PaymentIn payment) throws Exception {
		PaymentExpressWSSoap stub = new PaymentExpressWSLocator().getPaymentExpressWSSoap12();
		// "ISHPaymentExpress.testingGatewayAccount", "ishGroup_Dev"
		String username = payment.getCollege().getPaymentGatewayAccount();
		// "ISHPaymentExpress.testingGatewayPass", "test1234"
		String password = payment.getCollege().getPaymentGatewayPass();
		TransactionDetails transactionDetails = getTransactionDetails(payment);

		LOG.debug("Submitting payment to paymentexpress, gatewayAccount: " + username
				+ ", gatewayPassword: " + password);
		TransactionResult result = stub.submitTransaction(username, password, transactionDetails);
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
		
		details.setAmount(PaymentExpressUtil.translateInputAmountAsDecimalString(payment
				.getAmount()));
		transactionDetails.append("amount: ").append(details.getAmount());
		
		details.setCardHolderName(payment.getCreditCardName());
		transactionDetails.append(", cardHolderName: ").append(details.getCardHolderName());
		
		details.setCardNumber(payment.getCreditCardNumber());
		transactionDetails.append(", cardNumber: ").append(details.getCardNumber());
		
		details.setCvc2(payment.getCreditCardCVV());
		transactionDetails.append(", cardCVV: ").append(details.getCvc2());
		
		details.setDateExpiry(PaymentExpressUtil.translateInputExpiryDate(payment
				.getCreditCardExpiry()));
		transactionDetails.append(", cardExpiry: ").append(details.getDateExpiry());
		
		// TODO use other currencies
		details.setInputCurrency("Australian Dollar");
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

}
