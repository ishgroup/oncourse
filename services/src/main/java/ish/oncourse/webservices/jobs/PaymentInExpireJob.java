package ish.oncourse.webservices.jobs;

import com.paymentexpress.stubs.PaymentExpressWSLocator;
import com.paymentexpress.stubs.PaymentExpressWSSoap12Stub;
import ish.common.types.PaymentSource;
import ish.common.types.PaymentStatus;
import ish.common.types.PaymentType;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentInLine;
import ish.oncourse.model.PaymentTransaction;
import ish.oncourse.paymentexpress.customization.PaymentExpressWSLocatorWithSoapResponseHandle;
import ish.oncourse.services.payment.IPaymentService;
import ish.oncourse.services.paymentexpress.GetStatusOperation;
import ish.oncourse.services.paymentexpress.IPaymentGatewayService;
import ish.oncourse.services.paymentexpress.IPaymentGatewayServiceBuilder;
import ish.oncourse.services.paymentexpress.PaymentExpressGatewayService;
import ish.oncourse.services.paymentexpress.PaymentExpressUtil;
import ish.oncourse.services.paymentexpress.PaymentGatewayServiceBuilder;
import ish.oncourse.services.paymentexpress.PaymentInSupport;
import ish.oncourse.services.paymentexpress.TransactionResult;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.preference.PreferenceControllerFactory;
import ish.oncourse.util.payment.PaymentInAbandon;
import ish.oncourse.util.payment.PaymentInModel;
import ish.oncourse.util.payment.PaymentInModelFromPaymentInBuilder;
import ish.oncourse.util.payment.PaymentInModelFromSessionIdBuilder;
import ish.oncourse.util.payment.PaymentInSucceed;
import ish.oncourse.utils.PaymentInUtil;
import ish.persistence.CommonPreferenceController;
import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.PrefetchTreeNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Job to abandon (means fail enrolments and creating refunds) not
 * completed paymentIn. Not completed paymentIn can be two types, which were in
 * state CARD_DETAILS_REQUIRED,IN_TRANSACTION, NEW for more then
 * PaymentIn.EXPIRE_INTERVAL minutes or PamentIn with status FAILED which has
 * linked enrolments with status IN_TRANSACTION older then
 * PaymentIn.EXPIRE_INTERVAL.
 * 
 * @author anton
 * 
 */

public class PaymentInExpireJob implements Job {

	private static final Logger logger = LogManager.getLogger();
	
	private static final int FETCH_LIMIT = 500;

	private final ICayenneService cayenneService; 
	
	private IPaymentService paymentService;

	private final PreferenceControllerFactory prefFactory;

	public PaymentInExpireJob(ICayenneService cayenneService, IPaymentService paymentService, PreferenceControllerFactory prefFactory) {
		super();
		this.cayenneService = cayenneService;
		this.paymentService = paymentService;
		this.prefFactory = prefFactory;
	}

	/**
	 * Main job method, fetches expired paymentIn and abandons them.
	 */
	@Override
	public void execute() {

		logger.debug("PaymentInExpireJob started.");

		try {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, -PaymentIn.EXPIRE_INTERVAL);

			ObjectContext newContext = cayenneService.newContext();
			Set<PaymentIn> expiredPayments = new LinkedHashSet<>();

			List<PaymentIn> notCompletedList = getNotCompletedPaymentsFromDate(newContext, cal.getTime());
			expiredPayments.addAll(notCompletedList);

			logger.debug("The number of payments to expire: {}.", expiredPayments.size());

			for (PaymentIn p : expiredPayments) {
                processPayment(p);
            }
			logger.debug("PaymentInExpireJob finished.");

		} catch (Exception e) {
			logger.catching(e);
		}
	}

    private void processPayment(PaymentIn p) {
		//we need the try-catch block to continue processing other payments even if we get an excption
		//in the method.
		try {
			// do not fail payments for which we haven't got final transaction response from gateway
			if (paymentService.isProcessedByGateway(p)) {
				p.setStatusNotes(PaymentStatus.PAYMENT_EXPIRED_BY_TIMEOUT_MESSAGE);
				abandonPayment(p);
			} else if (PaymentExpressGatewayService.UNKNOW_RESULT_PAYMENT_IN.equals(p.getStatusNotes())) {
				if (p.getPaymentTransactions().isEmpty()) {
					logger.warn("PaymentIn with id:{} has no related not finished transactions", p.getId());
					return;
				}

				PreferenceController pref = prefFactory.getPreferenceController(p.getCollege());
				IPaymentGatewayService gatewayService = new PaymentGatewayServiceBuilder(pref, cayenneService).buildService();
				TransactionResult result = gatewayService.checkPaymentTransaction(p);

				if (PaymentExpressUtil.isValidResult(result)) {
					if (TransactionResult.ResultStatus.SUCCESS.equals(result.getStatus())) {
						p.setStatusNotes(PaymentExpressGatewayService.SUCCESS_PAYMENT_IN);

						PaymentInModel model;
						if (PaymentSource.SOURCE_ONCOURSE.equals(p.getSource())) {
							model = PaymentInModelFromSessionIdBuilder.valueOf(p.getSessionId(), p.getObjectContext()).build().getModel();
						} else {
							model = PaymentInModelFromPaymentInBuilder.valueOf(p).build().getModel();
						}

						PaymentInSucceed.valueOf(model).perform();

					} else {
						p.setStatusNotes(PaymentExpressGatewayService.FAILED_PAYMENT_IN);
						abandonPayment(p);
					}

					p.setGatewayResponse(result.getResult2().getResponseText());
					p.setGatewayReference(result.getResult2().getDpsTxnRef());
					p.setBillingId(result.getResult2().getDpsBillingId());

					PaymentTransaction transaction = p.getPaymentTransactions().get(0);
					transaction.setIsFinalised(true);
					transaction.setSoapResponse(result.getResult2().getMerchantHelpText());
					transaction.setSoapResponse(result.getResult2().getResponseText());
					transaction.setSoapResponse(result.getResult2().getTxnRef());
				} else {
					return;
				}
			}
			
			try {
				p.getObjectContext().commitChanges();
			} catch (final CayenneRuntimeException ce) {
				logger.debug("Unable to cancel payment with id:{} and status:{}.", p.getId(), p.getStatus(), ce);
				p.getObjectContext().rollbackChanges();
			}
		}
        catch (Exception e) {
	        logger.catching(e);
        }
    }
	
	private void abandonPayment(PaymentIn p) {

		//web enrollments need to be abandoned with reverse invoice, oncourse invoices preferable to keep the invoice.
		boolean isWebSourse = PaymentSource.SOURCE_WEB.equals(p.getSource());
		PaymentInModel model;
		if (isWebSourse) {
			model = PaymentInModelFromPaymentInBuilder.valueOf(p).build().getModel();
			PaymentInAbandon.valueOf(model,  PaymentInUtil.hasSuccessEnrolments(p) || PaymentInUtil.hasSuccessProductItems(p)).perform();
		} else {
			model = PaymentInModelFromSessionIdBuilder.valueOf(p.getSessionId(), p.getObjectContext()).build().getModel();
			PaymentInAbandon.valueOf(model, true).perform();
		}
	}
	
    /**
	 * Fetch payments which were not completed (not success nor failed, expired
	 * they were in not completed state for more than PaymentIn.EXPIRE_INTERVAL)
	 * 
	 * @param newContext
	 * @param date
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<PaymentIn> getNotCompletedPaymentsFromDate(ObjectContext newContext, Date date) {
		// Not completed means older than EXPIRE_INTERVAL and with statuses
		// CARD_DETAILS_REQUIRED and IN_TRANSACTION, regardless of sessionId.
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, - PaymentIn.EXPIRE_TIME_WINDOW);

		List<PaymentIn> notCompletedPayments = ObjectSelect.query(PaymentIn.class)
				.where(PaymentIn.MODIFIED.lt(date))
				.and(PaymentIn.CREATED.gt(calendar.getTime()))
				.and(PaymentIn.TYPE.eq(PaymentType.CREDIT_CARD))
				.and(PaymentIn.STATUS.in(PaymentStatus.CARD_DETAILS_REQUIRED, PaymentStatus.IN_TRANSACTION, PaymentStatus.NEW))
				.prefetch(PaymentIn.PAYMENT_IN_LINES.getName(), PrefetchTreeNode.UNDEFINED_SEMANTICS)
				.prefetch(PaymentIn.PAYMENT_IN_LINES.dot(PaymentInLine.INVOICE).getName(), PrefetchTreeNode.UNDEFINED_SEMANTICS)
				.limit(FETCH_LIMIT)
				.select(newContext);

		logger.info("<getNotCompletedPaymentsFromDate> the number of expired PaymentIn: {}", notCompletedPayments.size());
		
		for (PaymentIn p : notCompletedPayments) {
			logger.info("<getNotCompletedPaymentsFromDate> found expired PaymentIn id: {} status: {} type: {}", p.getId(), p.getStatus(), p.getType());
		}
		
		return notCompletedPayments;
	}
}
