package ish.oncourse.services.paymentexpress;

import com.paymentexpress.stubs.TransactionDetails;
import com.paymentexpress.stubs.TransactionResult2;
import ish.oncourse.model.College;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentTransaction;
import ish.oncourse.services.persistence.ICayenneService;
import ish.util.CreditCardUtil;
import org.apache.cayenne.ObjectContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PaymentInSupport implements IPaymentSupport<PaymentIn, PaymentTransaction>{

    private static final Logger logger = LogManager.getLogger();

    private PaymentIn paymentIn;
    private ICayenneService cayenneService;

    private PaymentTransaction currentTransaction;

    public PaymentInSupport(PaymentIn paymentIn, ICayenneService cayenneService) {
        this.paymentIn = paymentIn;
        this.cayenneService = cayenneService;
    }

    @Override
    public College getCollege() {
        return paymentIn.getCollege();
    }

    @Override
    public TransactionDetails getTransactionDetails() {
        TransactionDetails details = new TransactionDetails();

        StringBuilder transactionDetails = new StringBuilder("Preparing payment transaction data. ");

        details.setAmount(PaymentExpressUtil.translateInputAmountAsDecimalString(paymentIn.getAmount().toBigDecimal()));
        transactionDetails.append("amount: ").append(details.getAmount());

        details.setCardHolderName(paymentIn.getCreditCardName());
        transactionDetails.append(", cardHolderName: ").append(details.getCardHolderName());

        details.setCardNumber(paymentIn.getCreditCardNumber());
        transactionDetails.append(", cardNumber: ").append(CreditCardUtil.obfuscateCCNumber(details.getCardNumber()));

        details.setCvc2(paymentIn.getCreditCardCVV());
        transactionDetails.append(", cardCVV: ").append(CreditCardUtil.obfuscateCVVNumber(details.getCvc2()));

        details.setDateExpiry(PaymentExpressUtil.translateInputExpiryDate(paymentIn.getCreditCardExpiry()));
        transactionDetails.append(", cardExpiry: ").append(details.getDateExpiry());

        // TODO use other currencies
        details.setInputCurrency("AUD");
        transactionDetails.append(", currency: ").append(details.getInputCurrency());

        String ref = paymentIn.getClientReference();

        details.setMerchantReference(ref);
        transactionDetails.append(", merchantReference: ").append(details.getMerchantReference());

        details.setTxnRef(ref);
        transactionDetails.append(", transactionReference: ").append(details.getTxnRef());

        details.setTxnType(PaymentExpressUtil.PAYMENT_EXPRESS_TXN_TYPE);
        transactionDetails.append(", transaction type: ").append(details.getTxnType());

		details.setEnableAddBillCard("1");
        logger.debug(transactionDetails);

        return details;
    }

	@Override
	public TransactionDetails getTransactionDetails(String billingId) {
		TransactionDetails details = new TransactionDetails();

		StringBuilder transactionDetails = new StringBuilder("Preparing payment transaction data. ");

		details.setAmount(PaymentExpressUtil.translateInputAmountAsDecimalString(paymentIn.getAmount().toBigDecimal()));
		transactionDetails.append("amount: ").append(details.getAmount());

		details.setDpsBillingId(billingId);

		// TODO use other currencies
		details.setInputCurrency("AUD");
		transactionDetails.append(", currency: ").append(details.getInputCurrency());

		String ref = paymentIn.getClientReference();

		details.setMerchantReference(ref);
		transactionDetails.append(", merchantReference: ").append(details.getMerchantReference());

		details.setTxnRef(ref);
		transactionDetails.append(", transactionReference: ").append(details.getTxnRef());

		details.setTxnType(PaymentExpressUtil.PAYMENT_EXPRESS_TXN_TYPE);
		transactionDetails.append(", transaction type: ").append(details.getTxnType());

		logger.debug(transactionDetails);

		return details;
	}

	@Override
    public PaymentTransaction createTransaction() {
        ObjectContext newObjectContext = cayenneService.newNonReplicatingContext();
        currentTransaction = newObjectContext.newObject(PaymentTransaction.class);
        PaymentIn local = newObjectContext.localObject(paymentIn);
        currentTransaction.setPayment(local);
        return currentTransaction;
    }


    @Override
    public void adjustTransaction(TransactionResult result) {
		if (result.getResult2() != null) {
			currentTransaction.setSoapResponse(result.getResult2().getMerchantHelpText());
			currentTransaction.setResponse(result.getResult2().getResponseText());
			currentTransaction.setTxnReference(result.getResult2().getTxnRef());

			if (PaymentExpressUtil.isValidResult(result)) {
				currentTransaction.setIsFinalised(true);
			} else {
				currentTransaction.setIsFinalised(false);
			}
		} else if (TransactionResult.ResultStatus.UNKNOWN.equals(result.getStatus())) {
			currentTransaction.setIsFinalised(false);
		}
    }

    @Override
    public void commitTransaction() {
        if (currentTransaction != null)
            currentTransaction.getObjectContext().commitChanges();
    }

    @Override
    public PaymentIn getPayment() {
        return paymentIn;
    }

    @Override
    public void adjustPayment(TransactionResult result) {
		if (result.getResult2() != null) {
			paymentIn.setGatewayResponse(result.getResult2().getResponseText());
			paymentIn.setGatewayReference(result.getResult2().getDpsTxnRef());
			paymentIn.setBillingId(result.getResult2().getDpsBillingId());
		}
    }
}
