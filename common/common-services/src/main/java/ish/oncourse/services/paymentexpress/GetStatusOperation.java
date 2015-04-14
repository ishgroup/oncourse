package ish.oncourse.services.paymentexpress;

import com.paymentexpress.stubs.PaymentExpressWSSoap12Stub;
import com.paymentexpress.stubs.TransactionResult2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.rmi.RemoteException;

import static ish.oncourse.services.paymentexpress.PaymentExpressGatewayService.NUMBER_OF_ATTEMPTS;
import static ish.oncourse.services.paymentexpress.PaymentExpressGatewayService.RETRY_INTERVAL;

public class GetStatusOperation {
    private static final Logger logger = LogManager.getLogger();

    private final String postUsername;
    private final String postPassword;
    private final String txnRef;
    private PaymentExpressWSSoap12Stub paymentExpressSoapStub;
    public GetStatusOperation(String postUsername, String postPassword, String txnRef, PaymentExpressWSSoap12Stub paymentExpressSoapStub) {
        this.postUsername = postUsername;
        this.postPassword = postPassword;
        this.txnRef = txnRef;
        this.paymentExpressSoapStub = paymentExpressSoapStub;
    }

    public TransactionResult2 getResult() {
        int numberOfAttempts = 0;
        while (numberOfAttempts < NUMBER_OF_ATTEMPTS)
        {
            try {
                return paymentExpressSoapStub.getStatus(this.postUsername, this.postPassword, this.txnRef);
            } catch (RemoteException e) {
                logger.warn("Cannot get status for transaction with txnRef: {}", this.txnRef, e);
                try {
                    Thread.sleep(RETRY_INTERVAL);
                } catch (InterruptedException e1) {
                    logger.warn("InterruptedException is thrown on getting status for transaction with txnRef: {}", this.txnRef, e1);
                }
            }
            numberOfAttempts++;
        }
        throw new IllegalStateException(String.format("Status is not get after %d for transaction with txnRef: %s", numberOfAttempts, this.txnRef));
    }
}

