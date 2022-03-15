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
package ish.oncourse.cayenne

import ish.common.types.PaymentStatus
import ish.math.Money

import java.time.LocalDate

interface FinancialItem {

	String DATE = "date"
	String CREATED_ON = "createdOn"
	String DESCRIPTION = "description"
	String TOTAL = "total"
	String AMOUNT = "amount"

	FinancialItemType getFinancialItemType();

	Money getAmount();

	void setTotal(Money total);

	PaymentStatus getPaymentStatus();

	LocalDate getDate();

	Date getCreatedOn();

	enum FinancialItemType {
		PAYMENT_IN,
		PAYMENT_OUT,
		INVOICE
	}
}
