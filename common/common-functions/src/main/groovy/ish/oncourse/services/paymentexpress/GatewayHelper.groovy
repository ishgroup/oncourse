package ish.oncourse.services.paymentexpress

import com.paymentexpress.stubs.PaymentExpressWSSoap12Stub
import groovy.transform.CompileStatic
import ish.oncourse.paymentexpress.customization.PaymentExpressWSLocatorWithSoapResponseHandle
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.xml.rpc.ServiceException
import java.rmi.RemoteException

import static ish.oncourse.services.paymentexpress.DPSResponse.ResultStatus.RETRY
import static ish.oncourse.services.paymentexpress.DPSResponse.ResultStatus.UNKNOWN

@CompileStatic
class GatewayHelper {

    static final int TIMEOUT = 1000 * 120
    static final int RETRY_INTERVAL = 2000
    static final int NUMBER_OF_ATTEMPTS = 5
    static final Logger logger = LogManager.getLogger()

    DPSResponse submitRequest(DPSRequest request) {
        DPSResponse response
        retry(
                { response = processDPSRequest(request) },
                { (RETRY == response.status) },
                { Exception e -> logger.error("Attempt to submit payment with txnRef:{} transaction was failed", request.transactionDetails.txnRef, e) })
        return response
    }

    protected DPSResponse processDPSRequest(DPSRequest dpsRequest) {
        def stub = soapClientStub()
        def result
        try {
            result = DPSResponse.valueOf(stub.submitTransaction(dpsRequest.paymentGatewayAccount, dpsRequest.paymentGatewayPass, dpsRequest.transactionDetails))
            return UNKNOWN == result.status ? getStatus(stub, dpsRequest) : result
        } catch (RemoteException e) {
            logger.warn("Cannot submitTransaction for payment with txnRef: {}", dpsRequest.transactionDetails.txnRef, e)
            return getStatus(stub, dpsRequest)
        }
    }

    static DPSResponse getStatus(PaymentExpressWSSoap12Stub stub, DPSRequest dpsRequest) {
        DPSResponse result
        retry({
            result = DPSResponse.valueOf(stub.getStatus(dpsRequest.paymentGatewayAccount, dpsRequest.paymentGatewayPass, dpsRequest.transactionDetails.txnRef))
        },
                { (UNKNOWN == result.status) },
                { Exception e -> logger.error("Attempt to  verify payment status was failed, payment reference:{}", dpsRequest.transactionDetails.txnRef, e) })
        return result
    }

    static void retry(Closure body, Closure shouldRetry = { false }, Closure errorHandler, int times = NUMBER_OF_ATTEMPTS) {
        int retries = 0
        while (retries++ < times) {
            try {
                body.call();
                if (!shouldRetry.call()) {
                    return
                } else {
                    Thread.sleep(RETRY_INTERVAL)
                }
            } catch (e) {
                errorHandler.call(e)
            }
        }
    }

    /**
     * Initializes soap client.
     *
     * @return
     * @throws ServiceException
     */
    PaymentExpressWSSoap12Stub soapClientStub() throws ServiceException {
        def serviceLocator = new PaymentExpressWSLocatorWithSoapResponseHandle()
        serviceLocator.setPaymentExpressWSSoapEndpointAddress("https://sec.paymentexpress.com/WSV1/PXWS.asmx")
        PaymentExpressWSSoap12Stub stub = (PaymentExpressWSSoap12Stub) serviceLocator.getPaymentExpressWSSoap12()
        stub.setTimeout(TIMEOUT)
        return stub
    }
}
