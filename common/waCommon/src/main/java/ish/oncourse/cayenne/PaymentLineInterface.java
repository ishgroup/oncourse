/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.cayenne;

import ish.math.Money;
import ish.print.PrintableObject;

/**
 * Interface to unite server and client PaymentInLine, PaymentOutLine
 * 
 * @author marcin
 */
public interface PaymentLineInterface extends PrintableObject, PersistentObjectI {

	public static String PAYMENT = "payment";
	public static String AMOUNT = "amount";

	public Money getAmount();

	public void setAmount(Money amount);

	public PaymentInterface getPayment();

	public void setPayment(PaymentInterface payment);

	public InvoiceInterface getInvoice();

	public void setInvoice(InvoiceInterface invoice);

	public AccountInterface getAccount();

	public void setAccount(AccountInterface account);
}
