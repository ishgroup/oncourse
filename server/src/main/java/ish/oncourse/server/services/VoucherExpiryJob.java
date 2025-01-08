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
import ish.common.types.ProductStatus;
import ish.oncourse.server.ICayenneService;
import ish.oncourse.server.accounting.AccountTransactionService;
import ish.oncourse.server.accounting.builder.VoucherExpiryTransactionsBuilder;
import ish.oncourse.server.cayenne.Account;
import ish.oncourse.server.cayenne.Voucher;
import ish.persistence.CommonExpressionFactory;
import ish.util.AccountUtil;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.time.LocalDate;
import java.util.Date;

@DisallowConcurrentExecution
public class VoucherExpiryJob implements Job {

	private static final Logger logger = LogManager.getLogger();

	private ICayenneService cayenneService;
	private AccountTransactionService accountTransactionService;

	@Inject
	public VoucherExpiryJob(ICayenneService cayenneService, AccountTransactionService accountTransactionService) {
		this.cayenneService = cayenneService;
		this.accountTransactionService = accountTransactionService;
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		executeWithDate(new Date());
	}

	public void executeWithDate(Date date) {
		ObjectContext context = cayenneService.getNewContext();

		var vouchersExpiredAccount = AccountUtil.getDefaultVouchersExpiredAccount(context, Account.class);

		ObjectSelect.query(Voucher.class)
				.where(Voucher.STATUS.eq(ProductStatus.ACTIVE))
				.and(Voucher.EXPIRY_DATE.lte(CommonExpressionFactory.previousMidnight(date)))
				.prefetch(Voucher.PRODUCT.joint())
				.select(context)
				.forEach(voucher -> {
					try {
						voucher.setStatus(ProductStatus.EXPIRED);

						accountTransactionService.createTransactions(VoucherExpiryTransactionsBuilder.valueOf(voucher, vouchersExpiredAccount, LocalDate.now()));

						context.commitChanges();
					} catch (Exception e) {
						logger.error("Voucher expiration failed.", e);
						context.rollbackChangesLocally();
					}
				});
	}
}
