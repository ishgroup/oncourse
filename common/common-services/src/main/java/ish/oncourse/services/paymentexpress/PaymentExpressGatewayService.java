package ish.oncourse.services.paymentexpress;

import com.paymentexpress.stubs.*;
import ish.common.types.PaymentStatus;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentOut;
import ish.oncourse.paymentexpress.customization.PaymentExpressWSLocatorWithSoapResponseHandle;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.log4j.Logger;
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
    private static final Logger LOGGER = Logger.getLogger(PaymentExpressGatewayService.class);

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
    public void processGateway(PaymentIn payment) {

        try {
            TransactionResult2 tr = doTransaction(payment);

            StringBuilder resultDetails = new StringBuilder();

            if (PaymentExpressUtil.translateFlag(tr.getAuthorized())) {
                resultDetails.append("Payment succeed.");
                payment.setStatusNotes("Payment succeed.");
                payment.succeed();
                payment.setDateBanked(PaymentExpressUtil.translateSettlementDate(tr.getDateSettlement()));
            } else {
                resultDetails.append("Payment failed. Card declined.");
                payment.setStatusNotes("Payment failed. Card declined");
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
            LOGGER.debug(resultDetails.toString());
        } catch (Exception e) {
            LOGGER.error(String.format("PaymentIn id:%s failed with exception.", payment.getId()), e);
            payment.setStatusNotes("PaymentIn failed with exception.");
            payment.failPayment();
        }
    }

    @Override
    public void processGateway(PaymentOut paymentOut) {

        TransactionResult2 tr;

        try {
            tr = doTransaction(paymentOut);

            StringBuilder resultDetails = new StringBuilder();
            if (tr != null) {
                if (PaymentExpressUtil.translateFlag(tr.getAuthorized())) {
                    resultDetails.append("PaymentOut succeed.");
                    paymentOut.setStatusNotes("PaymentOut succeed.");
                    paymentOut.succeed();
                    paymentOut.setDateBanked(PaymentExpressUtil.translateSettlementDate(tr.getDateSettlement()));
                    paymentOut.setDatePaid(new Date());
                } else {
                    // TODO set statusNotes="cardDeclined" to payment here
                    resultDetails.append("PaymentOut failed. Declined by paymentExpress.");
                    paymentOut.setStatusNotes("PaymentOut failed. Declined by paymentExpress.");
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
                resultDetails.append("PaymentOut failed with null transaction response.");
                paymentOut.setStatusNotes("PaymentOut failed with null transaction response.");
                paymentOut.failed();
            }

            LOGGER.debug(resultDetails.toString());

        } catch (Exception e) {
            LOGGER.error(String.format("PaymentOut id:%s failed with exception.", paymentOut.getId()), e);
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


        LOGGER.debug(String.format("Submitting payment to paymentexpress, gatewayAccount: %s gatewayPassword: %s", username, password));

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
                        LOGGER.warn(String.format("InterruptedException is thrown on SubmitTransaction for payment with id: %d", paymentSupport.getPayment().getId()), e);
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
