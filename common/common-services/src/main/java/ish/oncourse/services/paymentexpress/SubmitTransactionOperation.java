package ish.oncourse.services.paymentexpress;

import com.paymentexpress.stubs.PaymentExpressWSSoap12Stub;
import com.paymentexpress.stubs.TransactionDetails;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.rmi.RemoteException;

public class SubmitTransactionOperation {

    private static final Logger logger = LogManager.getLogger();

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

    public TransactionResult getResult()
    {
        try {
			TransactionResult result = new TransactionResult();
            result.setResult2(paymentExpressSoapStub.submitTransaction(postUsername, postPassword, transactionDetails));
			return result;
        } catch (RemoteException e) {
            logger.warn("Cannot submitTransaction for payment with txnRef: {}", transactionDetails.getTxnRef(), e);
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
