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

package ish.oncourse.server.cayenne

import ish.math.Money
import ish.oncourse.API
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.server.cayenne.glue._InvoiceDueDate

import javax.annotation.Nonnull
import javax.annotation.Nullable
import java.time.LocalDate
import java.util.Date

/**
 * Object representing single payment due date of a payment plan.
 */
@API
@QueueableEntity
class InvoiceDueDate extends _InvoiceDueDate implements Queueable {



	@Override
	void prePersist() {
		super.prePersist()
		getInvoice().updateDateDue()
		getInvoice().updateOverdue()
	}

	@Override
	void preUpdate() {
		super.preUpdate()
		getInvoice().updateDateDue()
		getInvoice().updateOverdue()
	}

	/**
	 * @return amount due to be paid
	 */
	@API
	@Nonnull
	@Override
	Money getAmount() {
		return super.getAmount()
	}

	/**
	 * @return the date and time this record was created
	 */
	@API
	@Nullable
	@Override
	Date getCreatedOn() {
		return super.getCreatedOn()
	}

	/**
	 * @return the date when payment is due
	 */
	@API
	@Nonnull
	@Override
	LocalDate getDueDate() {
		return super.getDueDate()
	}

	/**
	 * @return the date and time this record was modified
	 */
	@API
	@Nullable
	@Override
	Date getModifiedOn() {
		return super.getModifiedOn()
	}

	/**
	 * @return invoice record linked to this
	 */
	@API
	@Nonnull
	@Override
	Invoice getInvoice() {
		return super.getInvoice()
	}
}



