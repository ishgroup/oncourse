package ish.oncourse.services.jobs;

import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentStatus;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentInLine;
import ish.oncourse.services.persistence.ICayenneService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
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

@DisallowConcurrentExecution
public class PaymentInExpireJob implements Job {

	private static final Logger logger = Logger.getLogger(PaymentInExpireJob.class);

	private final ICayenneService cayenneService;

	public PaymentInExpireJob(ICayenneService cayenneService) {
		super();
		this.cayenneService = cayenneService;
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		logger.debug("PaymentInExpireJob started.");

		try {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, -PaymentIn.EXPIRE_INTERVAL);

			ObjectContext newContext = cayenneService.newContext();
			List<PaymentIn> expiredPayments = new ArrayList<PaymentIn>();

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

	private List<PaymentIn> getNotCompletedPaymentsFromDate(ObjectContext newContext, Date date) {
		// Not completed means older than EXPIRE_INTERVAL and with statuses
		// CARD_DETAILS_REQUIRED and IN_TRANSACTION, regardless of sessionId.
		Expression expr = ExpressionFactory.lessExp(PaymentIn.MODIFIED_PROPERTY, date);
		expr = expr.andExp(ExpressionFactory.inExp(PaymentIn.STATUS_PROPERTY, PaymentStatus.CARD_DETAILS_REQUIRED,
				PaymentStatus.IN_TRANSACTION, PaymentStatus.NEW));

		SelectQuery notCompletedQuery = new SelectQuery(PaymentIn.class, expr);
		notCompletedQuery.addPrefetch(PaymentIn.PAYMENT_IN_LINES_PROPERTY);
		notCompletedQuery.addPrefetch(PaymentIn.PAYMENT_IN_LINES_PROPERTY + "." + PaymentInLine.INVOICE_PROPERTY);

		return newContext.performQuery(notCompletedQuery);
	}

	/**
	 * Very specific case, but in occasionally it happens. User has made one payment it's failed but later user closed the browser window 
	 * and do not press 'Abandon', 'Cancel' or 'Abandon, keep invoice' and 
	 * left PaymentIn with state FAILED and Enrolments with state IN_TRANSACTION.
	 * 
	 * @param newContext
	 * @param date
	 * @return
	 */
	private Set<PaymentIn> getOnceFailedPaymentsFromDate(ObjectContext newContext, Date date) {
		
		Set<PaymentIn> failedOncePayments = new HashSet<PaymentIn>();

		Expression notCompletedExpr = ExpressionFactory.lessExp(Enrolment.MODIFIED_PROPERTY, date);
		notCompletedExpr = notCompletedExpr.andExp(ExpressionFactory.matchExp(Enrolment.STATUS_PROPERTY, EnrolmentStatus.IN_TRANSACTION));

		SelectQuery notCompletedQuery = new SelectQuery(Enrolment.class, notCompletedExpr);
		List<Enrolment> notCompletedEnrolments = newContext.performQuery(notCompletedQuery);

		for (Enrolment enrl : notCompletedEnrolments) {
			InvoiceLine invLine = enrl.getInvoiceLine();
			if (invLine != null) {
				Invoice invoice = invLine.getInvoice();
				for (PaymentInLine line : invoice.getPaymentInLines()) {
					PaymentIn paymentIn = line.getPaymentIn();
					if (PaymentStatus.FAILED == paymentIn.getStatus()) {
						failedOncePayments.add(paymentIn);
					}
				}
			}
		}

		return failedOncePayments;
	}
}
