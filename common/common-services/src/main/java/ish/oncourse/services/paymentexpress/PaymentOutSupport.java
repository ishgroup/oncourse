package ish.oncourse.services.paymentexpress;

import com.paymentexpress.stubs.TransactionDetails;
import com.paymentexpress.stubs.TransactionResult2;
import ish.oncourse.model.College;
import ish.oncourse.model.PaymentOut;
import ish.oncourse.model.PaymentOutTransaction;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.cayenne.ObjectContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;

public class PaymentOutSupport implements IPaymentSupport<PaymentOut, PaymentOutTransaction> {

    /**
     * Logger for service.
     */
    private static final Logger logger = LogManager.getLogger();

    private PaymentOut paymentOut;
    private ICayenneService cayenneService;

    private PaymentOutTransaction currentTransaction;


    public PaymentOutSupport(PaymentOut paymentOut, ICayenneService cayenneService) {
        this.paymentOut = paymentOut;
        this.cayenneService = cayenneService;
    }

    @Override
    public College getCollege() {
        return paymentOut.getCollege();
    }

    @Override
    public TransactionDetails getTransactionDetails() {
        TransactionDetails details = new TransactionDetails();

        StringBuilder transactionDetails = new StringBuilder("Preparing payment transaction data. ");

        details.setAmount(PaymentExpressUtil.translateInputAmountAsDecimalString(paymentOut.getTotalAmount().toBigDecimal()));
        transactionDetails.append("amount: ").append(details.getAmount());

        details.setDpsTxnRef(paymentOut.getPaymentInTxnReference());

        details.setInputCurrency("AUD");
        transactionDetails.append(", currency: ").append(details.getInputCurrency());

        String ref = paymentOut.getClientReference();
        details.setMerchantReference(ref);
        details.setTxnRef(ref);
        details.setTxnType(PaymentExpressUtil.PAYMENT_EXPRESS_TXN_TYPE_REFUND);

        logger.debug(transactionDetails);

        return details;
    }

    @Override
    public PaymentOutTransaction createTransaction() {
        ObjectContext newObjectContext = cayenneService.newNonReplicatingContext();
        currentTransaction = newObjectContext.newObject(PaymentOutTransaction.class);
		currentTransaction.setCreated(new Date());
		currentTransaction.setModified(new Date());
        PaymentOut local = newObjectContext.localObject(paymentOut);
        currentTransaction.setPaymentOut(local);
        return currentTransaction;
    }

    @Override
    public void adjustTransaction(TransactionResult2 result) {
        currentTransaction.setSoapResponse(result.getMerchantHelpText());
        currentTransaction.setResponse(result.getResponseText());
        currentTransaction.setTxnReference(result.getTxnRef());
        currentTransaction.setIsFinalised(true);// in any case, this transaction is completed
    }

    @Override
    public void commitTransaction() {
        if (currentTransaction != null)
            currentTransaction.getObjectContext().commitChanges();
    }

    @Override
    public PaymentOut getPayment() {
        return paymentOut;
    }

    @Override
    public void adjustPayment(TransactionResult2 result) {
    }
}
