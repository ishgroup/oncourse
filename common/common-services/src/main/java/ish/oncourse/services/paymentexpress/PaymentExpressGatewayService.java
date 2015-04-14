package ish.oncourse.services.paymentexpress;

import com.paymentexpress.stubs.PaymentExpressWSLocator;
import com.paymentexpress.stubs.PaymentExpressWSSoap12Stub;
import com.paymentexpress.stubs.TransactionDetails;
import com.paymentexpress.stubs.TransactionResult2;
import ish.common.types.PaymentStatus;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentOut;
import ish.oncourse.paymentexpress.customization.PaymentExpressWSLocatorWithSoapResponseHandle;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Scope;

import javax.xml.rpc.ServiceException;
import java.util.Date;

/**
 * Payment Express gateway processing. {@inheritDoc} <br/>
 * Uses the external web service. See
 * http://www.paymentexpress.com/technical_resources
 * /ecommerce_nonhosted/webservice.html
 *
 * @author ksenia
 */
@Scope("perthread")
public class PaymentExpressGatewayService extends AbstractPaymentGatewayService {

    /**
     * The number of retry attempts.
     */
    static final int NUMBER_OF_ATTEMPTS = 5;

    /**
     * Retry interval.
     */
    static final int RETRY_INTERVAL = 2000;

    /**
     * Webservices calls timeout.
     */
    static final int TIMEOUT = 1000 * 120;

    /**
     * Logger for service.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Cayenne service
     */
    private ICayenneService cayenneService;

    @Inject
    public PaymentExpressGatewayService(ICayenneService cayenneService) {
        super();
        this.cayenneService = cayenneService;
    }

    /**
     * {@inheritDoc} Performs Payment Express gateway.
     *
     * @see AbstractPaymentGatewayService#processGateway(ish.oncourse.model.PaymentIn)
     */
    @Override
    protected void processGateway(PaymentIn payment) {

        try {
            TransactionResult2 tr = doTransaction(payment);

            StringBuilder resultDetails = new StringBuilder();

            if (PaymentExpressUtil.translateFlag(tr.getAuthorized())) {
                resultDetails.append(SUCCESS_PAYMENT_IN);
                payment.setStatusNotes(SUCCESS_PAYMENT_IN);
                payment.succeed();
                payment.setDateBanked(PaymentExpressUtil.translateSettlementDate(tr.getDateSettlement()));
            } else {
                resultDetails.append(FAILED_PAYMENT_IN);
                payment.setStatusNotes(FAILED_PAYMENT_IN);
                payment.setStatus(PaymentStatus.FAILED_CARD_DECLINED);
                payment.failPayment();
            }

            resultDetails.append(" authCode:").append(tr.getAuthCode()).append(", authorized:").append(tr.getAuthorized())
                    .append(", cardHolderHelpText:").append(tr.getCardHolderHelpText()).append(", cardHolderName:")
                    .append(tr.getCardHolderName()).append(", cardHolderResponseDescription:")
                    .append(tr.getCardHolderResponseDescription()).append(", currencyRate:").append(tr.getCurrencyRate())
                    .append(", currencyType:").append(tr.getCurrencyName()).append(", ourTransactionRef:").append(tr.getTxnRef())
                    .append(", responseCode:").append(tr.getReco()).append(", responseText:").append(tr.getResponseText())
                    .append(", retry:").append(tr.getRetry()).append(", settlementDate:").append(tr.getDateSettlement())
                    .append(", statusRequired:").append(tr.getStatusRequired()).append(", testMode:").append(tr.getTestMode())
                    .append(", transactionRef:").append(tr.getDpsTxnRef());
            logger.debug(resultDetails);
        } catch (Exception e) {
            logger.error("PaymentIn id: {} failed with exception.", payment.getId(), e);
            payment.setStatusNotes("PaymentIn failed with exception.");
            payment.failPayment();
        }
    }

    @Override
    protected void processGateway(PaymentOut paymentOut) {

        TransactionResult2 tr;

        try {
            tr = doTransaction(paymentOut);

            StringBuilder resultDetails = new StringBuilder();
            if (tr != null) {
                if (PaymentExpressUtil.translateFlag(tr.getAuthorized())) {
                    resultDetails.append(SUCCESS_PAYMENT_OUT);
                    paymentOut.setStatusNotes(SUCCESS_PAYMENT_OUT);
                    paymentOut.succeed();
                    paymentOut.setDateBanked(PaymentExpressUtil.translateSettlementDate(tr.getDateSettlement()));
                    paymentOut.setDatePaid(new Date());
                } else {
                    // TODO set statusNotes="cardDeclined" to payment here
                    resultDetails.append(FAILED_PAYMENT_OUT);
                    paymentOut.setStatusNotes(FAILED_PAYMENT_OUT);
                    paymentOut.setStatus(PaymentStatus.FAILED_CARD_DECLINED);
                    paymentOut.failed();
                }

                resultDetails.append(" authCode:").append(tr.getAuthCode()).append(", authorized:").append(tr.getAuthorized())
                        .append(", cardHolderHelpText:").append(tr.getCardHolderHelpText()).append(", cardHolderName:")
                        .append(tr.getCardHolderName()).append(", cardHolderResponseDescription:")
                        .append(tr.getCardHolderResponseDescription()).append(", currencyRate:").append(tr.getCurrencyRate())
                        .append(", currencyType:").append(tr.getCurrencyName()).append(", ourTransactionRef:").append(tr.getTxnRef())
                        .append(", responseCode:").append(tr.getReco()).append(", responseText:").append(tr.getResponseText())
                        .append(", retry:").append(tr.getRetry()).append(", settlementDate:").append(tr.getDateSettlement())
                        .append(", statusRequired:").append(tr.getStatusRequired()).append(", testMode:").append(tr.getTestMode())
                        .append(", transactionRef:").append(tr.getDpsTxnRef());
            } else {
                resultDetails.append(FAILED_PAYMENT_OUT_NULL_RESPONSE);
                paymentOut.setStatusNotes(FAILED_PAYMENT_OUT_NULL_RESPONSE);
                paymentOut.failed();
            }

            logger.debug(resultDetails);

        } catch (Exception e) {
            logger.error("PaymentOut id: {} failed with exception.", paymentOut.getId(), e);
            paymentOut.setStatusNotes("PaymentOut failed with exception.");
            paymentOut.failed();
        }
    }


    /**
     * Performs the payment(in,out) transaction trought the payment express gateway.
     *
     * @param paymentSupport the paymment to be processed.
     * @return the result of submitted transaction.
     * @throws Exception
     */
    TransactionResult2 doTransaction(@SuppressWarnings("rawtypes") IPaymentSupport paymentSupport) throws ServiceException {

        PaymentExpressWSSoap12Stub stub = soapClientStub();

        TransactionDetails transactionDetails = paymentSupport.getTransactionDetails();
        String username = paymentSupport.getCollege().getPaymentGatewayAccount();
        String password = paymentSupport.getCollege().getPaymentGatewayPass();


        logger.debug("Submitting payment to paymentexpress, gatewayAccount: {} gatewayPassword: {}", username, password);

        int n = 0;

        TransactionResult2 result;

        while (n < NUMBER_OF_ATTEMPTS) {

            try {

                paymentSupport.createTransaction();
                
                paymentSupport.commitTransaction();

                SubmitTransactionOperation submitTransactionOperation = new SubmitTransactionOperation(username,
                        password,
                        transactionDetails, stub);

                result = submitTransactionOperation.getResult();
                paymentSupport.adjustTransaction(result);

                boolean shouldRetry = PaymentExpressUtil.translateFlag(result.getRetry());

                if (shouldRetry) {
                    n++;
                    try {
                        Thread.sleep(RETRY_INTERVAL);
                    } catch (InterruptedException e) {
                        logger.warn("InterruptedException is thrown on SubmitTransaction for payment with id: {}", paymentSupport.getPayment().getId(), e);
                    }
                } else {
                    if (PaymentExpressUtil.translateFlag(result.getAuthorized())) {
                        paymentSupport.adjustPayment(result);
                    }
                    return result;
                }
            } finally {
                paymentSupport.commitTransaction();
            }
        }

        throw new IllegalStateException(String.format("Cannot SubmitTransaction for payment with id: %d", paymentSupport.getPayment().getId()));
    }


    /**
     * Performs the payment transaction trought the payment express gateway.
     *
     * @param payment the paymment to be processed.
     * @return the result of submitted transaction.
     * @throws Exception
     */
    public TransactionResult2 doTransaction(PaymentIn payment) throws ServiceException {
        return this.doTransaction(new PaymentInSupport(payment, cayenneService));
    }

    /**
     * Performs the payment transaction trought the payment express gateway.
     *
     * @param paymentOut the paymment to be processed.
     * @return the result of submitted transaction.
     * @throws Exception
     */
    public TransactionResult2 doTransaction(PaymentOut paymentOut) throws Exception {
        return this.doTransaction(new PaymentOutSupport(paymentOut, cayenneService));
    }

    /**
     * Initializes soap client.
     *
     * @return
     * @throws ServiceException
     */
    PaymentExpressWSSoap12Stub soapClientStub() throws ServiceException {
        PaymentExpressWSLocator serviceLocator = new PaymentExpressWSLocatorWithSoapResponseHandle();
        serviceLocator.setPaymentExpressWSSoapEndpointAddress("https://sec.paymentexpress.com/WSV1/PXWS.asmx");
        PaymentExpressWSSoap12Stub stub = (PaymentExpressWSSoap12Stub) serviceLocator.getPaymentExpressWSSoap12();
        stub.setTimeout(TIMEOUT);
        return stub;
    }
}
