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

import com.google.inject.Inject
import ish.common.types.InvoiceType
import ish.math.Money
import ish.oncourse.API
import ish.oncourse.cayenne.ContactInterface
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.server.cayenne.glue._Quote
import ish.oncourse.server.services.IAutoIncrementService

import javax.annotation.Nonnull
import javax.annotation.Nullable
import java.time.LocalDate
/**
 * Pre-invoice state
 */
@API
@QueueableEntity
class Quote extends _Quote implements ExpandableTrait{

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

	@Override
	List<AbstractInvoiceLine> getAbstractInvoiceLines() {
		new ArrayList<AbstractInvoiceLine>(){{
			addAll(quoteLines)
		}}
	}

	void setContact(ContactInterface contact) {
		if (contact instanceof Contact) {
			super.setContact((Contact) contact)
		} else {
			throw new IllegalArgumentException("expected Contact.class, was " + contact.getClass())
		}
	}

	/**
	 * This is the contact to whom the quote was issued. They are liable for the debt this quote represents.
	 * Note that the quote contact might not be the same contact as the person enrolled in classes linked to
	 * quote lines.
	 *
	 * @return to whom the quote was issued
	 */
	@Nonnull
	@API
	@Override
	Contact getContact() {
		return super.getContact()
	}

	/**
	 * @return list of quote line records linked to this quote
	 */
	@API
	@Nonnull
	@Override
	List<QuoteLine> getQuoteLines() {
		return super.getQuoteLines()
	}

	/**
	 * @return Title of this Quote or null if it is not set
	 */
	@Nullable
	@API
	@Override
	String getTitle() {
		return super.getTitle()
	}

	/**
	 * @return shipping address for this invoice
	 */
	@API
	@Nullable
	@Override
	String getShippingAddress() {
		return super.getShippingAddress()
	}

	/**
	 * The quote description might be shown on printed reports or in emails.
	 *
	 * @return quote description text
	 */
	@Nullable
	@API
	@Override
	String getDescription() {
		return super.getDescription()
	}

	/**
	 * The user who created this quote, or null if it was created through a web purchase.
	 *
	 * @return internal user who created this quote
	 */
	@Nullable
	@API
	@Override
	SystemUser getCreatedByUser() {
		return super.getCreatedByUser()
	}

	/**
	 * @return billing address for this quote
	 */
	@Nullable
	@API
	@Override
	String getBillToAddress() {
		return super.getBillToAddress()
	}

	/**
	 *
	 * @return the total without tax
	 */
	@Nonnull
	@API
	@Override
	Money getTotal() {
		return super.getTotal()
	}

	/**
	 * @return the date on which this quote amount falls due
	 */
	@Nonnull
	@API
	@Override
	LocalDate getDateDue() {
		return super.getDateDue()
	}

	/**
	 * @return the total of this quote including tax.
	 */
	@Nonnull
	@API
	@Override
	Money getTotalIncTax() {
		return super.getTotalIncTax()
	}

	/**
	 * @return date when this quote was created
	 */
	@Nonnull
	@API
	@Override
	LocalDate getInvoiceDate() {
		return super.getInvoiceDate()
	}

	/**
	 *
	 * @return the total amount of tax included in this quote
	 */
	@Nonnull
	@API
	@Override
	Money getTotalTax() {
		return super.getTotalTax()
	}

	/**
	 * Some arbitrary piece of text such as a PO number or customer reference which is displayed on the printed quote.
	 *
	 * @return
	 */
	@Nullable
	@API
	@Override
	String getCustomerReference() {
		return super.getCustomerReference()
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

	@Override
	Class<? extends CustomField> getCustomFieldClass() {
		return QuoteCustomField
	}
}