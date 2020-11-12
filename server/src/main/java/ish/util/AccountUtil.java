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
package ish.util;

import ish.oncourse.DefaultAccount;
import ish.oncourse.cayenne.AccountInterface;
import ish.persistence.RecordNotFoundException;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.ObjectSelect;

import java.util.List;

/**
 * A class shared between client and server allowing easy fetching of default Accounts.<br/>
 * <br/>
 * The implementation is temporary, as we are going to change the way account defaults are set, by going away from storing the codes in preferences.
 *
 */
public class AccountUtil {

    private AccountUtil() {}

	/**
	 * returns an account with given id. FIXME: the code does not respect 'isActive' flag!
	 * @param accountId
	 * @param context
	 * @param accountClass
	 * @param <T>
	 * @return
	 * @throws RecordNotFoundException
	 */
	public static <T extends AccountInterface> T getAccountWithId(final Long accountId, final ObjectContext context, Class<T> accountClass) throws RecordNotFoundException {
		final Expression expr = ExpressionFactory.matchExp(AccountInterface.ID_PROPERTY, accountId);
		final List<T> accounts = ObjectSelect.query(accountClass)
				.where(expr)
				.select(context);

		if (accounts.size() != 1) {
			throw new RecordNotFoundException(accountClass, expr, accounts.size());
		}
		return accounts.get(0);
	}

	public static <T extends AccountInterface> T getDefaultTaxAccount(final ObjectContext context, Class<T> accountClass) throws RecordNotFoundException {
		final Long id = DefaultAccount.getDefaultAccountId(DefaultAccount.TAX);
		return getAccountWithId(id, context, accountClass);
	}

	@Deprecated
	public static <T extends AccountInterface> T getDefaultDiscountAccount(final ObjectContext context, Class<T> accountClass) throws RecordNotFoundException {
		final Long id = DefaultAccount.getDefaultAccountId(DefaultAccount.DISCOUNT);
		return getAccountWithId(id, context, accountClass);
	}

	public static <T extends AccountInterface> T getDefaultDebtorsAccount(final ObjectContext context, Class<T> accountClass) throws RecordNotFoundException {
		final Long id = DefaultAccount.getDefaultAccountId(DefaultAccount.DEBTORS);
		return getAccountWithId(id, context, accountClass);
	}

	public static <T extends AccountInterface> T getDefaultBankAccount(final ObjectContext context, Class<T> accountClass) throws RecordNotFoundException {
		final Long id = DefaultAccount.getDefaultAccountId(DefaultAccount.BANK);
		return getAccountWithId(id, context, accountClass);
	}

	public static <T extends AccountInterface> T getDefaultGSTAccount(final ObjectContext context, Class<T> accountClass) throws RecordNotFoundException {
		final Long id = DefaultAccount.getDefaultAccountId(DefaultAccount.GST);
		return getAccountWithId(id, context, accountClass);
	}

	public static <T extends AccountInterface> T getDefaultStudentEnrolmentsAccount(final ObjectContext context, Class<T> accountClass) throws RecordNotFoundException {
		final Long id = DefaultAccount.getDefaultAccountId(DefaultAccount.STUDENT_ENROLMENTS);
		return getAccountWithId(id, context, accountClass);
	}

	public static <T extends AccountInterface> T getDefaultPrepaidFeesAccount(final ObjectContext context, Class<T> accountClass) throws RecordNotFoundException {
		final Long id = DefaultAccount.getDefaultAccountId(DefaultAccount.PREPAID_FEES);
		return getAccountWithId(id, context, accountClass);
	}

	public static <T extends AccountInterface> T getDefaultVoucherLiabilityAccount(final ObjectContext context, Class<T> accountClass) throws RecordNotFoundException {
		final Long id = DefaultAccount.getDefaultAccountId(DefaultAccount.VOUCHER_LIABILITY);
		return getAccountWithId(id, context, accountClass);
	}

	public static <T extends AccountInterface> T getDefaultVoucherExpenseAccount(final ObjectContext context, Class<T> accountClass) throws RecordNotFoundException {
		final Long id = DefaultAccount.getDefaultAccountId(DefaultAccount.VOUCHER_UNDERPAYMENT);
		return getAccountWithId(id, context, accountClass);
	}

	public static <T extends AccountInterface> T getDefaultVouchersExpiredAccount(final ObjectContext context, Class<T> accountClass) throws RecordNotFoundException {
		final Long id = DefaultAccount.getDefaultAccountId(DefaultAccount.VOUCHERS_EXPIRED);
		return getAccountWithId(id, context, accountClass);
	}
}
