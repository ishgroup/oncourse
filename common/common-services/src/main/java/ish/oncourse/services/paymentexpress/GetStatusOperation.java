package ish.oncourse.services.paymentexpress;

import com.paymentexpress.stubs.PaymentExpressWSSoap12Stub;
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

    public TransactionResult getResult() {
        int numberOfAttempts = 0;
		TransactionResult result = new TransactionResult();
        while (numberOfAttempts < NUMBER_OF_ATTEMPTS)
        {
            try {
				result.setResult2(paymentExpressSoapStub.getStatus(this.postUsername, this.postPassword, this.txnRef));
                return result;
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
		result.setStatus(TransactionResult.ResultStatus.UNKNOWN);
		return result;
    }
}

