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
package ish.messaging;

import ish.math.Money;
import ish.oncourse.cayenne.PersistentObjectI;

import java.time.LocalDate;
import java.util.List;

/**
 */
public interface IInvoice extends PersistentObjectI {
	String CONTACT_KEY = "contact";
	String INVOICE_DATE_KEY = "invoiceDate";
	String TOTAL_KEY = "total";
	String TOTAL_TAX_KEY = "totalTax";
	String TOTAL_INC_TAX_KEY = "totalIncTax";
	String AMOUNT_OWING_KEY = "amountOwing";
	String AMOUNT_PAID_KEY = "amountPaid";

	String TOTAL_INC_TAX_PROPERTY = "invoice_total_inc_property";

	Money getAmountOwing();

	Money getAmountPaid();

	IContact getContact();

	LocalDate getInvoiceDate();

	LocalDate getDateDue();

	List<? extends IInvoiceLine> getInvoiceLines();

	Long getInvoiceNumber();

	Money getTotal();

	Money getTotalTax();

	Money getTotalIncTax();

	String getDescription();

	ICorporatePass getCorporatePassUsed();

	String getCustomerReference();
}
