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

package ish.validation;

import ish.persistence.Preferences;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class TransactionLockedValidator implements Validator<TransactionLockedErrorCode> {

	private Map<String, TransactionLockedErrorCode> result;

	private LocalDate oldValue;
	private LocalDate newValue;

	public Map<String, TransactionLockedErrorCode>  validate() {
		LocalDate dateAfterTransactionLocked = oldValue.plusDays(1);
		LocalDate today = LocalDate.now();

		if (dateAfterTransactionLocked.isEqual(today)) {
			result.put(Preferences.FINANCE_TRANSACTION_LOCKED, TransactionLockedErrorCode.allDaysFinalised);
		} else if (newValue.isBefore(dateAfterTransactionLocked)) {
			result.put(Preferences.FINANCE_TRANSACTION_LOCKED, TransactionLockedErrorCode.beforeCurrentValue);
		} else if (newValue.isEqual(today) || newValue.isAfter(today)) {
			result.put(Preferences.FINANCE_TRANSACTION_LOCKED, TransactionLockedErrorCode.todayOrInFuture);
		}

		return result;
	}

	public static TransactionLockedValidator valueOf(LocalDate oldValue, LocalDate newValue) {
		TransactionLockedValidator validator = new TransactionLockedValidator();
		validator.result = new HashMap<>();
		validator.oldValue = oldValue;
		validator.newValue = newValue;
		return validator;
	}
}
