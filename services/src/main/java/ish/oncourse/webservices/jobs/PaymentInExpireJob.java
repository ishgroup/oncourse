package ish.oncourse.webservices.jobs;

import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentStatus;
import ish.common.types.PaymentType;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentInLine;
import ish.oncourse.services.persistence.ICayenneService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.log4j.Logger;

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

	private static final Logger logger = Logger.getLogger(PaymentInExpireJob.class);
	
	private static final int FETCH_LIMIT = 500;

	private final ICayenneService cayenneService; 

	public PaymentInExpireJob(ICayenneService cayenneService) {
		super();
		this.cayenneService = cayenneService;
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
			Set<PaymentIn> expiredPayments = new LinkedHashSet<PaymentIn>();

			List<PaymentIn> notCompletedList = getNotCompletedPaymentsFromDate(newContext, cal.getTime());
			expiredPayments.addAll(notCompletedList);

			logger.debug(String.format("The number of payments to expire:%s.", expiredPayments.size()));

			for (PaymentIn p : expiredPayments) {
				try {

					logger.info(String.format("Canceling paymentIn with id:%s, created:%s and status:%s.", p.getId(), p.getCreated(),
							p.getStatus()));
					// we should check that there is no enrollments with amount owing exist in this payment.
					final List<Enrolment> enrollmentsForKeepInvoice = new ArrayList<Enrolment>();
					if (p.getPaymentInLines() != null) {
						for (PaymentInLine paymentLine: p.getPaymentInLines()) {
							if (paymentLine.getInvoice().getInvoiceLines() != null) {
								for (InvoiceLine invoiceLine : paymentLine.getInvoice().getInvoiceLines()) {
									if (invoiceLine.getEnrolment() != null && EnrolmentStatus.SUCCESS.equals(invoiceLine.getEnrolment().getStatus())) {
										enrollmentsForKeepInvoice.add(invoiceLine.getEnrolment());
									}
								}
							}
						}
					}
					if (enrollmentsForKeepInvoice.isEmpty()) {
						//if all enrollments in transaction we can just fail them
						p.abandonPayment();
					} else {
						//we should not fail enrollments when college allow them to enroll with owing.
						p.abandonPaymentKeepInvoice();
					}
					newContext.commitChanges();
				} catch (CayenneRuntimeException ce) {
					logger.debug(String.format("Unable to cancel payment with id:%s and status:%s.", p.getId(), p.getStatus()), ce);
					newContext.rollbackChanges();
				}
			}

			logger.debug("PaymentInExpireJob finished.");

		} catch (Exception e) {
			logger.error("Error in PaymentInExpireJob.", e);
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
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -PaymentIn.EXPIRE_TIME_WINDOW);
		expr = expr.andExp(ExpressionFactory.greaterExp(PaymentIn.CREATED_PROPERTY, calendar.getTime()));
		
		expr = expr.andExp(ExpressionFactory.matchExp(PaymentIn.TYPE_PROPERTY, PaymentType.CREDIT_CARD));
		expr = expr.andExp(ExpressionFactory.inExp(PaymentIn.STATUS_PROPERTY, PaymentStatus.CARD_DETAILS_REQUIRED,
				PaymentStatus.IN_TRANSACTION, PaymentStatus.NEW));
		

		SelectQuery notCompletedQuery = new SelectQuery(PaymentIn.class, expr);
		notCompletedQuery.addPrefetch(PaymentIn.PAYMENT_IN_LINES_PROPERTY);
		notCompletedQuery.addPrefetch(PaymentIn.PAYMENT_IN_LINES_PROPERTY + "." + PaymentInLine.INVOICE_PROPERTY);
		
		notCompletedQuery.setFetchLimit(FETCH_LIMIT);

		List<PaymentIn> notCompletedPayments = newContext.performQuery(notCompletedQuery);
		logger.info(String.format("<getNotCompletedPaymentsFromDate> the number of expired PaymentIn:%s", notCompletedPayments.size()));
		
		for (PaymentIn p : notCompletedPayments) {
			logger.info(String.format("<getNotCompletedPaymentsFromDate> found expired PaymentIn id:%s status:%s type:%s", p.getId(), p.getStatus(), p.getType()));
		}
		
		return notCompletedPayments;
	}
}
