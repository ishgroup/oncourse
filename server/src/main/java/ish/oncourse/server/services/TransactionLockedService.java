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
import ish.oncourse.server.ICayenneService;
import ish.oncourse.server.cayenne.Preference;
import ish.persistence.Preferences;
import ish.util.LocalDateUtils;
import ish.validation.TransactionLockedValidator;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;

public class TransactionLockedService {

	private static final Logger logger = LogManager.getLogger();

	private ICayenneService cayenneService;

	@Inject
	public TransactionLockedService(ICayenneService cayenneService) {
		this.cayenneService = cayenneService;
	}

	public LocalDate getTransactionLocked() {
		var stringValue = getTransactionLockedDate();
		return LocalDateUtils.stringToValue(stringValue, LocalDateUtils.formatter);
	}

	public String getTransactionLockedDate() {
		var preference = ObjectSelect.query(Preference.class)
				.where(Preference.NAME.eq(Preferences.FINANCE_TRANSACTION_LOCKED))
				.selectOne(cayenneService.getNewContext());
		return preference.getValueString();
	}

	public void updateTransactionLockedDate(LocalDate date) {
		var context = cayenneService.getNewContext();

		var preference = ObjectSelect.query(Preference.class)
				.where(Preference.NAME.eq(Preferences.FINANCE_TRANSACTION_LOCKED))
				.selectOne(context);
		var value = LocalDateUtils.valueToString(date, LocalDateUtils.formatter);
		preference.setValueString(value);
		context.commitChanges();
	}

	@Deprecated
	public void updateTransactionLockedDate(String date) {
		var context = cayenneService.getNewContext();

		var preference = ObjectSelect.query(Preference.class)
				.where(Preference.NAME.eq(Preferences.FINANCE_TRANSACTION_LOCKED))
				.selectOne(context);

		var oldDate = preference.getValueString();

		var newValue = LocalDateUtils.stringToValue(date, LocalDateUtils.formatter);
		var oldValue = LocalDateUtils.stringToValue(oldDate, LocalDateUtils.formatter);

		var result = TransactionLockedValidator.valueOf(oldValue, newValue).validate();

		if (result.isEmpty()) {
			preference.setValueString(date);
			context.commitChanges();
		} else {
			var errorCode = result.get(Preferences.FINANCE_TRANSACTION_LOCKED).name();
			logger.error(errorCode);
			throw new IllegalStateException(errorCode);
		}

	}

}
