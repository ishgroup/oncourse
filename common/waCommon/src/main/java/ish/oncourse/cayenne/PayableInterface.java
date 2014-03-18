/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
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
