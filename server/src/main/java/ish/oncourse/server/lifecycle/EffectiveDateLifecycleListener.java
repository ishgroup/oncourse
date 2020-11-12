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

import ish.oncourse.server.cayenne.AccountTransaction;
import ish.oncourse.server.cayenne.Invoice;
import ish.oncourse.server.cayenne.PaymentIn;
import ish.oncourse.server.cayenne.PaymentOut;
import ish.oncourse.server.services.TransactionLockedService;
import ish.util.ValidateEffectiveDate;
import ish.validation.ValidationFailure;
import ish.validation.ValidationResult;
import org.apache.cayenne.Persistent;
import org.apache.cayenne.annotation.PrePersist;
import org.apache.cayenne.annotation.PreUpdate;
import org.apache.cayenne.validation.ValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class EffectiveDateLifecycleListener {

	public TransactionLockedService transactionLockedService;
	public static final String SKIP_EFFECTIVE_DATE_CHECK =  "SKIP_EFFECTIVE_DATE_CHECK";


	private static final Logger logger = LogManager.getLogger();


	public EffectiveDateLifecycleListener(TransactionLockedService transactionLockedService) {
		this.transactionLockedService = transactionLockedService;
	}


	@PrePersist(value = Invoice.class)
	public void prePersist(Invoice invoice) {
		checkEffectiveDate(invoice, Invoice.INVOICE_DATE.getName());
	}

	@PreUpdate(value = Invoice.class)
	public void preUpdate(Invoice invoice) {
		checkEffectiveDate(invoice,  Invoice.INVOICE_DATE.getName());
	}

	@PrePersist(value = PaymentIn.class)
	public void prePersist(PaymentIn paymentIn) {
		checkEffectiveDate(paymentIn, PaymentIn.PAYMENT_DATE.getName());
	}

	@PreUpdate(value = PaymentIn.class)
	public void preUpdate(PaymentIn paymentIn) {
		checkEffectiveDate(paymentIn, PaymentIn.PAYMENT_DATE.getName());
	}

	@PrePersist(value = PaymentOut.class)
	public void prePersist(PaymentOut paymentOut) {
		checkEffectiveDate(paymentOut, PaymentOut.PAYMENT_DATE.getName());
	}

	@PreUpdate(value = PaymentOut.class)
	public void preUpdate(PaymentOut paymentOut) {
		checkEffectiveDate(paymentOut,  PaymentOut.PAYMENT_DATE.getName());
	}

	@PrePersist(value = AccountTransaction.class)
	public void prePersist(AccountTransaction transaction) {
		var val = transaction.getContext().getUserProperty(SKIP_EFFECTIVE_DATE_CHECK);
		if (val instanceof Boolean && Boolean.TRUE.equals(val)) {
			//skip validation
			return;
		}
		checkEffectiveDate(transaction, AccountTransaction.TRANSACTION_DATE.getName());
	}

	@PreUpdate(value = AccountTransaction.class)
	public void preUpdate(AccountTransaction transaction) {
		checkEffectiveDate(transaction,  AccountTransaction.TRANSACTION_DATE.getName());
	}

	private void checkEffectiveDate(Persistent o, String propertyName) {
		var change = ChangeFilter.getAtrAttributeChange(o.getObjectContext(), o.getObjectId(), propertyName);

		if (change != null) {
			var newValue = change.getNewValue();
			var lockedFromString = transactionLockedService.getTransactionLockedDate();
			if (!ValidateEffectiveDate.valueOf(lockedFromString, newValue).validate()) {
				logger.error("Attempt to save {} with {} property which is before transaction locked date, objectId: {}, property value: {}, transaction locked date is:{}",
						o.getClass().getSimpleName(), propertyName, o.getObjectId(), newValue, lockedFromString);

				var result = new ValidationResult();
				result.addFailure(new ValidationFailure(o, propertyName, "You must choose a date after " + lockedFromString));
				throw new ValidationException( result);
			}
		}
	}
}
