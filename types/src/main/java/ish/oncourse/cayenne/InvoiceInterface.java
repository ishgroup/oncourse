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
package ish.oncourse.cayenne;

import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SortOrder;

import java.util.List;

/**
 * @author marcin
 */
public interface InvoiceInterface extends PayableInterface {
	public static final String TO_BE_PAID_PROPERTY = "invoice_to_be_paid_property";
	public static final String IS_SELECTED = "is_selected";
	public static final String AMOUNT_OWING_PROPERTY = "amountOwing";
	public static final String DATE_DUE_PROPERTY = "dateDue";

	public static final Ordering INVOICES_DUE_ORDERING = new Ordering(DATE_DUE_PROPERTY, SortOrder.ASCENDING);

	public List<? extends PaymentOutLineInterface> getPaymentOutLines();

	public List<? extends PaymentInLineInterface> getPaymentInLines();

	public void removeFromPaymentLines(PaymentLineInterface pLine);

	public AccountInterface getDebtorsAccount();

	public Long getInvoiceNumber();

}
