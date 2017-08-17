package ish.oncourse.services.paymentexpress;

import com.paymentexpress.stubs.PaymentExpressWSLocator;
import com.paymentexpress.stubs.PaymentExpressWSSoap12Stub;
import com.paymentexpress.stubs.TransactionDetails;
import ish.common.types.PaymentStatus;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentOut;
import ish.oncourse.paymentexpress.customization.PaymentExpressWSLocatorWithSoapResponseHandle;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.util.payment.PaymentInFail;
import ish.oncourse.util.payment.PaymentInModel;
import ish.oncourse.util.payment.PaymentInSucceed;
import org.apache.commons.lang3.StringUtils;
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
	public static String URL = "https://sec.paymentexpress.com/WSV1/PXWS.asmx";

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
	 * @see AbstractPaymentGatewayService#processGateway(ish.oncourse.util.payment.PaymentInModel, java.lang.String)
	 */
	@Override
	public void processGateway(PaymentInModel model, String billingId) {
		PaymentIn payment = model.getPaymentIn();
		try {
			TransactionResult tr = doTransaction(payment, billingId);

            StringBuilder resultDetails = new StringBuilder();

            if (PaymentExpressUtil.isValidResult(tr)) {
                if (TransactionResult.ResultStatus.SUCCESS.equals(tr.getStatus())) {
                    resultDetails.append(SUCCESS_PAYMENT_IN);
                    payment.setStatusNotes(SUCCESS_PAYMENT_IN);
                    PaymentInSucceed.valueOf(model).perform();
                    payment.setDateBanked(PaymentExpressUtil.translateSettlementDate(tr.getResult2().getDateSettlement()));
                } else {
                    resultDetails.append(FAILED_PAYMENT_IN);
                    payment.setStatusNotes(FAILED_PAYMENT_IN);
                    payment.setStatus(PaymentStatus.FAILED_CARD_DECLINED);
                    PaymentInFail.valueOf(model).perform();
                }
            } else {
				payment.setStatus(PaymentStatus.IN_TRANSACTION);
				payment.setStatusNotes(UNKNOW_RESULT_PAYMENT_IN);
			}

			if (tr.getResult2() != null) {
				resultDetails.append(" authCode:").append(tr.getResult2().getAuthCode()).append(", authorized:").append(tr.getResult2().getAuthorized())
						.append(", cardHolderHelpText:").append(tr.getResult2().getCardHolderHelpText()).append(", cardHolderName:")
						.append(tr.getResult2().getCardHolderName()).append(", cardHolderResponseDescription:")
						.append(tr.getResult2().getCardHolderResponseDescription()).append(", currencyRate:").append(tr.getResult2().getCurrencyRate())
						.append(", currencyType:").append(tr.getResult2().getCurrencyName()).append(", ourTransactionRef:").append(tr.getResult2().getTxnRef())
						.append(", responseCode:").append(tr.getResult2().getReco()).append(", responseText:").append(tr.getResult2().getResponseText())
						.append(", retry:").append(tr.getResult2().getRetry()).append(", settlementDate:").append(tr.getResult2().getDateSettlement())
						.append(", statusRequired:").append(tr.getResult2().getStatusRequired()).append(", testMode:").append(tr.getResult2().getTestMode())
						.append(", transactionRef:").append(tr.getResult2().getDpsTxnRef());
				logger.debug(resultDetails);
			}
        } catch (Exception e) {
            logger.error("PaymentIn id: {} failed with exception.", payment.getId(), e);
        }
    }

    @Override
    protected void processGateway(PaymentOut paymentOut) {

        TransactionResult tr;

        try {
            tr = doTransaction(paymentOut);

            StringBuilder resultDetails = new StringBuilder();
            if (tr != null && tr.getResult2() != null) {
                if (TransactionResult.ResultStatus.SUCCESS.equals(tr.getStatus())) {
                    resultDetails.append(SUCCESS_PAYMENT_OUT);
                    paymentOut.setStatusNotes(SUCCESS_PAYMENT_OUT);
                    paymentOut.succeed();
                    paymentOut.setDateBanked(PaymentExpressUtil.translateSettlementDate(tr.getResult2().getDateSettlement()));
                    paymentOut.setDatePaid(new Date());
                } else {
                    // TODO set statusNotes="cardDeclined" to payment here
                    resultDetails.append(FAILED_PAYMENT_OUT);
                    paymentOut.setStatusNotes(FAILED_PAYMENT_OUT);
                    paymentOut.setStatus(PaymentStatus.FAILED_CARD_DECLINED);
                    paymentOut.failed();
                }

				resultDetails.append(" authCode:").append(tr.getResult2().getAuthCode()).append(", authorized:").append(tr.getResult2().getAuthorized())
						.append(", cardHolderHelpText:").append(tr.getResult2().getCardHolderHelpText()).append(", cardHolderName:")
						.append(tr.getResult2().getCardHolderName()).append(", cardHolderResponseDescription:")
						.append(tr.getResult2().getCardHolderResponseDescription()).append(", currencyRate:").append(tr.getResult2().getCurrencyRate())
						.append(", currencyType:").append(tr.getResult2().getCurrencyName()).append(", ourTransactionRef:").append(tr.getResult2().getTxnRef())
						.append(", responseCode:").append(tr.getResult2().getReco()).append(", responseText:").append(tr.getResult2().getResponseText())
						.append(", retry:").append(tr.getResult2().getRetry()).append(", settlementDate:").append(tr.getResult2().getDateSettlement())
						.append(", statusRequired:").append(tr.getResult2().getStatusRequired()).append(", testMode:").append(tr.getResult2().getTestMode())
						.append(", transactionRef:").append(tr.getResult2().getDpsTxnRef());
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
    TransactionResult doTransaction(@SuppressWarnings("rawtypes") IPaymentSupport paymentSupport, String billingId) throws ServiceException {

        PaymentExpressWSSoap12Stub stub = soapClientStub();

        TransactionDetails transactionDetails;
		
		if (StringUtils.trimToNull(billingId) != null) {
			transactionDetails = paymentSupport.getTransactionDetails(billingId);
		} else {
			transactionDetails =  paymentSupport.getTransactionDetails();
		}
		
        String username = paymentSupport.getCollege().getPaymentGatewayAccount();
        String password = paymentSupport.getCollege().getPaymentGatewayPass();


        logger.debug("Submitting payment to paymentexpress, gatewayAccount: {} gatewayPassword: {}", username, password);

        int n = 0;

        TransactionResult result;

        while (n < NUMBER_OF_ATTEMPTS) {

            try {

                paymentSupport.createTransaction();
                
                paymentSupport.commitTransaction();

                SubmitTransactionOperation submitTransactionOperation = new SubmitTransactionOperation(username,
                        password,
                        transactionDetails, stub);

                result = submitTransactionOperation.getResult();
                paymentSupport.adjustTransaction(result);

				if (result.getResult2() == null && TransactionResult.ResultStatus.UNKNOWN.equals(result.getStatus())) {
					return result;
				} else if (result.getResult2() == null) {
					throw new IllegalStateException(String.format("Cannot SubmitTransaction for payment with id: %d", paymentSupport.getPayment().getId()));
				}

                boolean shouldRetry = PaymentExpressUtil.translateFlag(result.getResult2().getRetry());

                if (shouldRetry) {
                    n++;
                    try {
                        Thread.sleep(RETRY_INTERVAL);
                    } catch (InterruptedException e) {
                        logger.warn("InterruptedException is thrown on SubmitTransaction for payment with id: {}", paymentSupport.getPayment().getId(), e);
                    }
                } else {
                    if (PaymentExpressUtil.translateFlag(result.getResult2().getAuthorized())) {
						result.setStatus(TransactionResult.ResultStatus.SUCCESS);
                        paymentSupport.adjustPayment(result);
                    } else {
						result.setStatus(TransactionResult.ResultStatus.FAILED);
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
    public TransactionResult doTransaction(PaymentIn payment, String billingId) throws ServiceException {
        return this.doTransaction(new PaymentInSupport(payment, cayenneService), billingId);
    }

    /**
     * Performs the payment transaction trought the payment express gateway.
     *
     * @param paymentOut the paymment to be processed.
     * @return the result of submitted transaction.
     * @throws Exception
     */
    public TransactionResult doTransaction(PaymentOut paymentOut) throws Exception {
        return this.doTransaction(new PaymentOutSupport(paymentOut, cayenneService), null);
    }

    /**
     * Initializes soap client.
     *
     * @return
     * @throws ServiceException
     */
    PaymentExpressWSSoap12Stub soapClientStub() throws ServiceException {
        PaymentExpressWSLocator serviceLocator = new PaymentExpressWSLocatorWithSoapResponseHandle();
		serviceLocator.setPaymentExpressWSSoapEndpointAddress(URL);
		PaymentExpressWSSoap12Stub stub = (PaymentExpressWSSoap12Stub) serviceLocator.getPaymentExpressWSSoap12();
        stub.setTimeout(TIMEOUT);
        return stub;
    }
	
	public TransactionResult checkPaymentTransaction(PaymentIn p) throws ServiceException {

		PaymentExpressWSSoap12Stub stub = soapClientStub();

		GetStatusOperation operation = new GetStatusOperation(p.getCollege().getPaymentGatewayAccount(),
				p.getCollege().getPaymentGatewayPass(),
				p.getClientReference(),
				stub);

		int n = 0;
		TransactionResult result = null;

		while (n < NUMBER_OF_ATTEMPTS) {
			result = operation.getResult();

			if (result.getResult2() == null && TransactionResult.ResultStatus.UNKNOWN.equals(result.getStatus())) {
				return result;
			}

			if (PaymentExpressUtil.translateFlag(result.getResult2().getRetry())) {
				n++;
				try {
					Thread.sleep(RETRY_INTERVAL);
				} catch (InterruptedException e) {
					logger.warn("InterruptedException is thrown on SubmitTransaction for payment with id: {}", p.getId(), e);
				}
			} else {
				if (PaymentExpressUtil.translateFlag(result.getResult2().getAuthorized())) {
					result.setStatus(TransactionResult.ResultStatus.SUCCESS);
				} else {
					result.setStatus(TransactionResult.ResultStatus.FAILED);
				}
				return result;
			}
		}

		result = new TransactionResult();
		result.setStatus(TransactionResult.ResultStatus.UNKNOWN);
		return result;
	}
}
