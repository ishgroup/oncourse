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
package ish.oncourse.server.lifecycle;

import ish.oncourse.server.cayenne.PaymentOut;
import org.apache.cayenne.annotation.PostPersist;
import org.apache.cayenne.annotation.PostUpdate;
import org.apache.cayenne.annotation.PrePersist;
import org.apache.cayenne.annotation.PreUpdate;

import java.util.Date;

/**
 */
public class PaymentOutLifecycleListener {

	/**
	 * @see org.apache.cayenne.LifecycleListener#prePersist(Object)
	 */
	@PrePersist(value = PaymentOut.class)
	public void prePersist(PaymentOut paymentOut) {
		PaymentHelper.tryToAssignBankingAutomatically(paymentOut);
	}

	/**
	 * @see org.apache.cayenne.LifecycleListener#postPersist(Object)
	 */
	@PostPersist(value = PaymentOut.class)
	public void postPersist(PaymentOut pOut) {
		// touch each related invoice to update amount owing
		pOut.updateAmountsOwing(pOut.getObjectContext());
		pOut.getObjectContext().commitChanges();
	}

	/**
	 * @see org.apache.cayenne.LifecycleListener#preUpdate(Object)
	 */
	@PreUpdate(value = PaymentOut.class)
	public void preUpdate(PaymentOut pOut) {
		var date = new Date();
		// touch all payment lines, in case the status of the payment is set to success, then the account transactions have to be created in relevant
		// trigger
		if (pOut.getPaymentOutLines() != null) {
			for (var poutl : pOut.getPaymentOutLines()) {
				poutl.setModifiedOn(date);
			}
		}

		PaymentHelper.tryToAssignBankingAutomatically(pOut);
	}

	/**
	 * @see org.apache.cayenne.LifecycleListener#postUpdate(Object)
	 */
	@PostUpdate(value = PaymentOut.class)
	public void postUpdate(PaymentOut pOut) {
		// touch each related invoice to update amount owing
		pOut.updateAmountsOwing(pOut.getObjectContext());
		pOut.getObjectContext().commitChanges();
	}
}
