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

import javax.inject.Inject
import ish.common.types.InvoiceType
import ish.math.Money
import ish.oncourse.API
import ish.oncourse.cayenne.ContactInterface
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.server.cayenne.glue._Quote
import ish.oncourse.server.services.IAutoIncrementService
import ish.util.InvoiceUtil

import javax.annotation.Nonnull

/**
 * Pre-invoice state
 */
@API
@QueueableEntity
class Quote extends _Quote {

	@Inject
	private transient IAutoIncrementService autoIncrementService

	@Override
	void postAdd() {
		super.postAdd()
		if (getQuoteNumber() == null) {
			setQuoteNumber(autoIncrementService.getNextQuoteNumber())
		}
		setAmountOwing(Money.ZERO)
		setOverdue(Money.ZERO)
	}

	@Override
	InvoiceType getType() {
		return InvoiceType.QUOTE
	}

	Class<QuoteLine> getLinePersistentClass() {
		return QuoteLine.class
	}

	List<QuoteLine> getLines() {
		return this.getQuoteLines()
	}

	void setContact(ContactInterface contact) {
		if (contact instanceof Contact) {
			super.setContact((Contact) contact)
		} else {
			throw new IllegalArgumentException("expected Contact.class, was " + contact.getClass())
		}
	}

	/**
	 * @return They shouldn't have an owing amount because they can't accept payments.
	 */
	@Nonnull
	@API
	@Override
	Money getAmountOwing() {
		return Money.ZERO
	}

	/**
	 * @return They shouldn't have an overdue amount
	 */
	@Nonnull
	@API
	@Override
	Money getOverdue() {
		return Money.ZERO
	}

	void updateAmountOwing() {
		setAmountOwing(Money.ZERO)
	}

	void updateOverdue() {
		setOverdue(Money.ZERO)
	}
}
