package ish.oncourse.webservices.jobs;

import ish.common.types.PaymentStatus;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentInLine;
import ish.oncourse.services.persistence.ICayenneService;

import java.util.Calendar;
import java.util.List;

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

	private static final int EXPIRE_INTERVAL = 20;

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
			cal.add(Calendar.MINUTE, -EXPIRE_INTERVAL);

			Expression expr = ExpressionFactory.noMatchExp(PaymentIn.SESSION_ID_PROPERTY, null);
			expr = expr.andExp(ExpressionFactory.lessExp(PaymentIn.MODIFIED_PROPERTY, cal.getTime()));
			expr = expr.andExp(ExpressionFactory.inExp(PaymentIn.STATUS_PROPERTY, PaymentStatus.CARD_DETAILS_REQUIRED,
					PaymentStatus.IN_TRANSACTION));

			SelectQuery q = new SelectQuery(PaymentIn.class, expr);
			q.addPrefetch(PaymentIn.PAYMENT_IN_LINES_PROPERTY);
			q.addPrefetch(PaymentIn.PAYMENT_IN_LINES_PROPERTY + "." + PaymentInLine.INVOICE_PROPERTY);

			ObjectContext newContext = cayenneService.newContext();

			List<PaymentIn> expiredPayments = newContext.performQuery(q);

			for (PaymentIn p : expiredPayments) {
				p.abandonPayment();
			}

			newContext.commitChanges();

			logger.debug("PaymentInExpireJob finished.");

		} catch (Exception e) {
			logger.error("Error in PaymentInExpireJob.", e);
			throw new JobExecutionException("Error in PaymentInExpireJob.", e, false);
		}
	}
}
