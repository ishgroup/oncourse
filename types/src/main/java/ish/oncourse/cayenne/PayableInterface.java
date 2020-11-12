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

import ish.common.payable.PayableLineInterface;
import ish.math.Money;
import ish.print.PrintableObject;

import java.util.List;

/**
 * Interface for all entities which can be paid for: Invoice, PO, Payroll. For now works only with Invoice, but required to unify the methods between server and
 * client.
 *
 * @author marcin
 */
public interface PayableInterface extends PrintableObject, PersistentObjectI {

	public void updateAmountOwing();

	public void setAmountOwing(Money amountOwing);

	public Money getAmountOwing();

	public void setContact(ContactInterface contact);

	public ContactInterface getContact();

	public List<PaymentLineInterface> getPaymentLines();

	public List<PayableLineInterface> getPayableLines();

}
