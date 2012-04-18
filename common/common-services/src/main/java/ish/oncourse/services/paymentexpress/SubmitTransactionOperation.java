package ish.oncourse.services.paymentexpress;

import com.paymentexpress.stubs.PaymentExpressWSSoap12Stub;
import com.paymentexpress.stubs.TransactionDetails;
import com.paymentexpress.stubs.TransactionResult2;
import org.apache.log4j.Logger;

import java.rmi.RemoteException;

public class SubmitTransactionOperation {

    private static final Logger LOGGER = Logger.getLogger(SubmitTransactionOperation.class);

    private final String postUsername;
    private final String postPassword;
    private final PaymentExpressWSSoap12Stub paymentExpressSoapStub;
    private TransactionDetails transactionDetails;

    public SubmitTransactionOperation(String postUsername, String postPassword, TransactionDetails transactionDetails, PaymentExpressWSSoap12Stub paymentExpressSoapStub) {
        this.postUsername = postUsername;
        this.postPassword = postPassword;
        this.transactionDetails = transactionDetails;
        this.paymentExpressSoapStub = paymentExpressSoapStub;
    }

    public TransactionResult2 getResult()
    {
        try {
            return paymentExpressSoapStub.submitTransaction(postUsername, postPassword, transactionDetails);
        } catch (RemoteException e) {
            LOGGER.warn(String.format("Cannot submitTransaction for payment with txnRef: %s", transactionDetails.getTxnRef()),e);
            /**
             * Define status of the transaction:
             */
            GetStatusOperation operation = new GetStatusOperation(this.postUsername,
                    this.postPassword,
                    this.transactionDetails.getTxnRef(),
                    this.paymentExpressSoapStub);
            return operation.getResult();
        }
    }
}
