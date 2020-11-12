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
package ish.print.transformations;

import ish.print.AdditionalParameters;

import java.time.LocalDate;

public class AccountAccountTransactionTransformation extends PrintTransformation {

	public AccountAccountTransactionTransformation() {
		setInputEntityName("Account");
		setOutputEntityName("AccountTransaction");

		LocalDate now = LocalDate.now();
		LocalDate defaultStartDate = now.withDayOfMonth(1);
		LocalDate defaultEndDate = now.withDayOfMonth(now.lengthOfMonth());

		PrintTransformationField<LocalDate> from = new PrintTransformationField<>(AdditionalParameters.LOCALDATERANGE_FROM.toString(), "from", LocalDate.class, defaultStartDate);
		PrintTransformationField<LocalDate> to = new PrintTransformationField<>(AdditionalParameters.LOCALDATERANGE_TO.toString(), "to", LocalDate.class, defaultEndDate);

		addFieldDefinition(from);
		addFieldDefinition(to);

		setTransformationFilter("account.id in $sourceIds and transactionDate >= $" + from.getFieldCode() + " and transactionDate <= $" + to.getFieldCode());
	}
}
