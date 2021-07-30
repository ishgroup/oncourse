/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */
package ish.oncourse.server.services;

import com.google.inject.Inject;
import ish.common.types.AccountTransactionType;
import ish.common.types.EnrolmentStatus;
import ish.common.types.InvoiceType;
import ish.math.Money;
import ish.oncourse.server.ICayenneService;
import ish.oncourse.server.PreferenceController;
import ish.oncourse.server.accounting.AccountTransactionService;
import ish.oncourse.server.accounting.builder.DelayedIncomeTransactionsBuilder;
import ish.oncourse.server.cayenne.AccountTransaction;
import ish.oncourse.server.cayenne.Invoice;
import ish.oncourse.server.cayenne.InvoiceLine;
import ish.persistence.Preferences;
import ish.util.LocalDateUtils;
import org.apache.cayenne.query.SelectQuery;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * this job runs nightly and processes invoice lines, posting the delayed income between liability and income accounts.
 *
 */
@DisallowConcurrentExecution
public class DelayedEnrolmentIncomePostingJob implements Job {

	private static final Logger logger = LogManager.getLogger();

	private ICayenneService cayenneService;
	private AccountTransactionService accountTransactionService;

	private String delayedIncomePreference;

	@Inject
	public DelayedEnrolmentIncomePostingJob(ICayenneService cayenneService, AccountTransactionService accountTransactionService) {
		super();
		this.cayenneService = cayenneService;
		this.accountTransactionService = accountTransactionService;
		logger.info("cayenneService {}", cayenneService);
	}

	/**
	 * @see Job#execute(JobExecutionContext)
	 */
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		executeWithDate(new Date());
	}

	/**
	 * fetches a list of invoices with prepaid fees remaining, for the previous midnight (from the given date). Then the invoices are checked whether they
	 * should be processed individually.
	 *
	 * @param date
	 * @throws JobExecutionException
	 */
	public void executeWithDate(Date date) throws JobExecutionException {
		var lastMidnight = DateUtils.truncate(date, Calendar.DATE);
		var transactionTimestamp = DateUtils.addSeconds(lastMidnight, -1);

		setDelayedIncomePreference(PreferenceController.getController().getAccountPrepaidFeesPostAt());

		var list = getListOfInvoiceLinesToProcess();

		logger.warn("Processing prepaid fees for {}, found {} invoice lines as candidates for processing.", date, list.size());
		if (list.size() > 0) {
			for (var invoiceLine : list) {
				processInvoiceLine(invoiceLine, transactionTimestamp);
			}
		}
	}

	/**
	 * @param delayedIncomePreference the delayedIncomePreference to set
	 */
	protected void setDelayedIncomePreference(String delayedIncomePreference) {
		this.delayedIncomePreference = delayedIncomePreference;
	}

	protected String getDelayedIncomePreference() {
		return this.delayedIncomePreference;
	}

	/**
	 * queries for the list of invoice lines which might need GL posting (prepaid fees remaining <> 0, at least one session has started)
	 *
	 * @return list of invoice lines
	 */
	protected List<InvoiceLine> getListOfInvoiceLinesToProcess() {
		var expr = InvoiceLine.INVOICE.dot(Invoice.TYPE).eq(InvoiceType.INVOICE)
				.andExp(InvoiceLine.PREPAID_FEES_REMAINING.ne(Money.ZERO));
		return this.cayenneService.getNewContext().select(SelectQuery.query(InvoiceLine.class, expr));
	}

	/**
	 * processes a single InvoiceLine
	 *
	 * @param invoiceLine
	 * @param date at which to process the invoice line
	 */
	protected void processInvoiceLine(InvoiceLine invoiceLine, Date date) {
		var amountToPost = getAmountToPostToIncomeAccountForInvoiceLine(invoiceLine, date);

		if (! amountToPost.isZero()) {
			var transactionDate = LocalDateUtils.dateToValue(date);

			accountTransactionService.createTransactions(DelayedIncomeTransactionsBuilder.valueOf(invoiceLine, amountToPost, transactionDate));

			var ctx = cayenneService.getNewContext();
			var copy = ctx.localObject(invoiceLine);
			copy.setPrepaidFeesRemaining(copy.getPrepaidFeesRemaining().subtract(amountToPost));
			ctx.commitChanges();

			// recalculate prepaid fees remaining for the invoice line basing on actual transactions in the db
			// this is a temporary solution for fixing accounting issues per #22446 until we stop using
			// prepaidFeesRemaining field value and will rely only on the actual transactions linked to invoice line
			copy.setPrepaidFeesRemaining(calculatePrepaidFeesRemaining(invoiceLine));
			ctx.commitChanges();
		}
	}

	/**
	 * calculates the amount to post between the liability and income accounts
	 *
	 * @param invoiceLine
	 * @param date at which to process the invoice line
	 * @return amount of Money to post
	 */
	protected Money getAmountToPostToIncomeAccountForInvoiceLine(InvoiceLine invoiceLine, Date date) {

		if (invoiceLine.getPrepaidFeesRemaining().isZero()) {
			// how did we get here? Nevermind, just exit.
			return Money.ZERO;
		}

		if (invoiceLine.getEnrolment() == null && invoiceLine.getCourseClass() == null) {
			// this really shouldn't happen
			return Money.ZERO;
		}

		if (invoiceLine.getCreatedOn() != null && invoiceLine.getCreatedOn().after(date)) {
			// invoiceline has been created after the given date, do not process it.
			return Money.ZERO;
		}

		if (invoiceLine.getEnrolment() != null) {

			if (!EnrolmentStatus.STATUSES_LEGIT.contains(invoiceLine.getEnrolment().getStatus())) {
				// enrolment refunded/cancelled/failed/corrupted, post all the income
				return invoiceLine.getPrepaidFeesRemaining();
			}
		}

		if (invoiceLine.getCourseClass() != null) {
			if (invoiceLine.getCourseClass().getIsCancelled()) {
				// class was cancelled, post all manually linked invoice lines to income
				return invoiceLine.getPrepaidFeesRemaining();
			}
		}

		var courseClass = invoiceLine.getEnrolment() != null ?
				invoiceLine.getEnrolment().getCourseClass() : invoiceLine.getCourseClass();

		if (courseClass.getIsDistantLearningCourse() || courseClass.getFirstSession() == null ) {
			// self paced class, post all the income
			return invoiceLine.getPrepaidFeesRemaining();
		}


		var firstStart = courseClass.getFirstSession().getStartDatetime();

        if (firstStart.after(date)) {
			// post nothing
			return Money.ZERO; // shortcut to avoid more calculations
		}

		if (Preferences.ACCOUNT_PREPAID_FEES_POST_AT_EVERY_SESSION.equals(delayedIncomePreference)) {
			var targetAmount = invoiceLine.getFinalPriceToPayExTax().multiply(
					courseClass.getPercentageOfDeliveredScheduledHoursBeforeDate(date));

			var amountPostedAlready = invoiceLine.getFinalPriceToPayExTax().subtract(invoiceLine.getPrepaidFeesRemaining());

			// post the difference
			return targetAmount.subtract(amountPostedAlready);
		}

		// post all the income
		return invoiceLine.getPrepaidFeesRemaining();

	}

	private Money calculatePrepaidFeesRemaining(InvoiceLine invoiceLine) {
		var context = invoiceLine.getObjectContext();

		var query = SelectQuery.query(AccountTransaction.class);

		query.andQualifier(AccountTransaction.TABLE_NAME.eq(AccountTransactionType.INVOICE_LINE));
		query.andQualifier(AccountTransaction.FOREIGN_RECORD_ID.eq(invoiceLine.getId()));
		query.andQualifier(AccountTransaction.ACCOUNT.eq(invoiceLine.getPrepaidFeesAccount()));

		var transactions = context.select(query);

		// if there are no transactions exist for the invoice line - full price ex tax should be returned
		if (transactions.isEmpty()) {
			return invoiceLine.getFinalPriceToPayExTax();
		}

		var prepaidFeesRemaining = Money.ZERO;

		for (var transaction : transactions) {
			prepaidFeesRemaining = prepaidFeesRemaining.add(transaction.getAmount());
		}

		return prepaidFeesRemaining;
	}
}
