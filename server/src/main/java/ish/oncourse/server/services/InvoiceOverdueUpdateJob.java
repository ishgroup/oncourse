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


import javax.inject.Inject;
import ish.math.Money;
import ish.oncourse.server.ICayenneService;
import ish.oncourse.server.cayenne.Invoice;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.time.LocalDate;
import java.util.Date;

/**
 * this job runs nightly and processes invoice lines, posting the delayed income between liability and income accounts.
 *
 */
@DisallowConcurrentExecution
public class InvoiceOverdueUpdateJob implements Job {

	private static final Logger logger = LogManager.getLogger();
	public static final String INVOICE_OVERDUE_UPDATE_JOB = "invoiceOverdueUpdateJob" ;

	private ICayenneService cayenneService;

	@Inject
	public InvoiceOverdueUpdateJob(ICayenneService cayenneService) {
		super();
		this.cayenneService = cayenneService;
		logger.info("cayenneService {}", cayenneService);
	}


	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		var date = new Date();
		var dataContext = cayenneService.getNewNonReplicatingContext();
		dataContext.setUserProperty(INVOICE_OVERDUE_UPDATE_JOB, "true");

		var invoices = ObjectSelect.query(Invoice.class)
				.where(Invoice.AMOUNT_OWING.gt(Money.ZERO)).and(Invoice.DATE_DUE.lte(LocalDate.now())).select(dataContext);

		logger.warn("Updating of overdue amounts started, found {} invoices as candidates for processing.", invoices.size());

		var count = 0;

		for (var invoice : invoices) {
			invoice.updateAmountOwing();
			invoice.updateDateDue();
			invoice.updateOverdue();
			count++;
			if (count == 50) {
				dataContext.commitChanges();
				count = 0;
			}
		}
		dataContext.commitChanges();


	}
}
