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
