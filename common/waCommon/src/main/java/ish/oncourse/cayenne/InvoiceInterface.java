/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
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
