package ish.oncourse.webservices.jobs;

import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentSource;
import ish.common.types.PaymentStatus;
import ish.common.types.PaymentType;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentInLine;
import ish.oncourse.services.persistence.ICayenneService;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Quartz job to abandon (means fail enrolments and creating refunds) not
 * completed paymentIn. Not completed paymentIn can be two types, which were in
 * state CARD_DETAILS_REQUIRED,IN_TRANSACTION, NEW for more then
 * PaymentIn.EXPIRE_INTERVAL minutes or PamentIn with status FAILED which has
 * linked enrolements with status IN_TRANSACTION older then
 * PaymentIn.EXPIRE_INTERVAL.
 * 
 * @author anton
 * 
 */
@DisallowConcurrentExecution
public class PaymentInExpireJob implements Job {

	private static final Logger logger = Logger.getLogger(PaymentInExpireJob.class);

	private final ICayenneService cayenneService;

	public PaymentInExpireJob(ICayenneService cayenneService) {
		super();
		this.cayenneService = cayenneService;
	}

	/**
	 * Main job method, fetches expired paymentIn and abandons them.
	 */
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		logger.debug("PaymentInExpireJob started.");

		try {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, -PaymentIn.EXPIRE_INTERVAL);

			ObjectContext newContext = cayenneService.newContext();
			Set<PaymentIn> expiredPayments = new LinkedHashSet<PaymentIn>();

			List<PaymentIn> notCompletedList = getNotCompletedPaymentsFromDate(newContext, cal.getTime());
			expiredPayments.addAll(notCompletedList);

			Set<PaymentIn> failedOnceList = getOnceFailedPaymentsFromDate(newContext, cal.getTime());
			expiredPayments.addAll(failedOnceList);

			logger.debug(String.format("The number of payments to expire:%s.", expiredPayments.size()));

			for (PaymentIn p : expiredPayments) {
				try {

					logger.info(String.format("Canceling paymentIn with id:%s, created:%s and status:%s.", p.getId(), p.getCreated(),
							p.getStatus()));

					p.abandonPayment();

					newContext.commitChanges();
				} catch (CayenneRuntimeException ce) {
					logger.debug(String.format("Unable to cancel payment with id:%s and status:%s.", p.getId(), p.getStatus()), ce);
					newContext.rollbackChanges();
				}
			}

			logger.debug("PaymentInExpireJob finished.");

		} catch (Exception e) {
			logger.error("Error in PaymentInExpireJob.", e);
			throw new JobExecutionException("Error in PaymentInExpireJob.", e, false);
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
		Expression expr = ExpressionFactory.lessExp(PaymentIn.MODIFIED_PROPERTY, date);
		expr = expr.andExp(ExpressionFactory.matchExp(PaymentIn.TYPE_PROPERTY, PaymentType.CREDIT_CARD));
		expr = expr.andExp(ExpressionFactory.inExp(PaymentIn.STATUS_PROPERTY, PaymentStatus.CARD_DETAILS_REQUIRED,
				PaymentStatus.IN_TRANSACTION, PaymentStatus.NEW));
		

		SelectQuery notCompletedQuery = new SelectQuery(PaymentIn.class, expr);
		notCompletedQuery.addPrefetch(PaymentIn.PAYMENT_IN_LINES_PROPERTY);
		notCompletedQuery.addPrefetch(PaymentIn.PAYMENT_IN_LINES_PROPERTY + "." + PaymentInLine.INVOICE_PROPERTY);

		List<PaymentIn> notCompletedPayments = newContext.performQuery(notCompletedQuery);
		logger.info(String.format("<getNotCompletedPaymentsFromDate> the number of expired PaymentIn:%s", notCompletedPayments.size()));
		
		for (PaymentIn p : notCompletedPayments) {
			logger.info(String.format("<getNotCompletedPaymentsFromDate> found expired PaymentIn id:%s status:%s type:%s", p.getId(), p.getStatus(), p.getType()));
		}
		
		return notCompletedPayments;
	}

	/**
	 * Very specific case, but in occasionally it happens. User has made one
	 * payment it's failed but later user closed the browser window and do not
	 * press 'Abandon', 'Cancel' or 'Abandon, keep invoice' and left PaymentIn
	 * with state FAILED and Enrolments with state IN_TRANSACTION.
	 * 
	 * @param newContext
	 * @param date
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Set<PaymentIn> getOnceFailedPaymentsFromDate(ObjectContext newContext, Date date) {

		Set<PaymentIn> failedOncePayments = new HashSet<PaymentIn>();

		Expression notCompletedExpr = ExpressionFactory.lessExp(PaymentIn.MODIFIED_PROPERTY, date);
		notCompletedExpr = notCompletedExpr.andExp(ExpressionFactory.matchExp(PaymentIn.SOURCE_PROPERTY, PaymentSource.SOURCE_WEB));
		notCompletedExpr = notCompletedExpr.andExp(ExpressionFactory.matchExp(PaymentIn.TYPE_PROPERTY, PaymentType.CREDIT_CARD));
		notCompletedExpr = notCompletedExpr.andExp(ExpressionFactory.inExp(PaymentIn.STATUS_PROPERTY, PaymentStatus.FAILED, PaymentStatus.FAILED_CARD_DECLINED));
		notCompletedExpr = notCompletedExpr.andExp(ExpressionFactory.matchExp(PaymentIn.PAYMENT_IN_LINES_PROPERTY + "."
				+ PaymentInLine.INVOICE_PROPERTY + "." + Invoice.INVOICE_LINES_PROPERTY + "." + InvoiceLine.ENROLMENT_PROPERTY + "."
				+ Enrolment.STATUS_PROPERTY, EnrolmentStatus.IN_TRANSACTION));

		SelectQuery notCompletedQuery = new SelectQuery(PaymentIn.class, notCompletedExpr);
		List<PaymentIn> notCompletedPayments = newContext.performQuery(notCompletedQuery);
		
		logger.info(String.format("<getOnceFailedPaymentsFromDate> the number of expired PaymentIn:%s", notCompletedPayments.size()));
		
		for (PaymentIn p : notCompletedPayments) {
			logger.info(String.format("<getOnceFailedPaymentsFromDate> found expired PaymentIn id:%s status:%s type:%s", p.getId(), p.getStatus(), p.getType()));
			failedOncePayments.add(p);
		}


		return failedOncePayments;
	}
}
