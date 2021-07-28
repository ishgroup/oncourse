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

import ish.common.payable.PayableLineInterface
import ish.common.types.ConfirmationStatus
import ish.common.types.InvoiceType
import ish.common.types.PaymentSource
import ish.math.Money
import ish.oncourse.API
import ish.oncourse.cayenne.ContactInterface
import ish.oncourse.cayenne.PayableInterface
import ish.oncourse.cayenne.PaymentLineInterface
import ish.oncourse.server.cayenne.glue._AbstractInvoice
import ish.util.InvoiceUtil
import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.annotation.Nonnull
import javax.annotation.Nullable
import java.time.LocalDate

abstract class AbstractInvoice extends _AbstractInvoice implements PayableInterface, Queueable, NotableTrait, AttachableTrait {

	private static final Logger logger = LogManager.getLogger()

	public static final String TOTAL_INC_TAX_KEY = "totalIncTax"

	public static final String TOTAL_INC_TAX_PROPERTY = "invoice_total_inc_property"
	public static final String AMOUNT_OWING_PROPERTY = "amountOwing"
	public static final String AMOUNT_PAID_PROPERTY = "amountPaid"

	public static final String TOTAL_INC_GST_PROPERTY = "totalIncTax"
	public static final String TOTAL_EX_GST_PROPERTY = "total"
	public static final String TOTAL_TAX_PROPERTY = "totalTax"

	public static final String CREATED_BY_USER_NAME_PROPERTY = "createdByUserName"

	abstract Class<? extends AbstractInvoiceLine> getLinePersistentClass()

	abstract List<? extends AbstractInvoiceLine> getLines()

	static Money amountOwingForPayer(@Nullable Contact payer) {
		Money result = Money.ZERO

		// update invoice owing
		if (payer != null) {
			List<Invoice> invoices = payer.getInvoices()

			for (Invoice invoice : invoices) {
				result = result.add(invoice.getAmountOwing())
			}
		}
		return result
	}

	/**
	 * @return unique invoice number
	 */
	@Nonnull
	@API
	@Override
	Long getInvoiceNumber() {
		return super.getInvoiceNumber()
	}

	/**
	 * @return the amount still owing on this invoice
	 */
	@Nonnull
	@API
	@Override
	Money getAmountOwing() {
		final Money value = super.getAmountOwing()
		if (value == null) {
			updateAmountOwing()
			return super.getAmountOwing()
		}
		return value
	}

	/**
	 * @return the amount already paid against this invoice
	 */
	@API
	Money getAmountPaid() {
		updateAmountOwing()
		return getTotalIncTax().subtract(getAmountOwing())
	}

	/**
	 * @return the total of this invoice including tax.
	 */
	@API
	Money getTotalIncTax() {
		List<AbstractInvoiceLine> theInvoiceLines = getLines()
		if (theInvoiceLines == null || theInvoiceLines.size() == 0) {
			return Money.ZERO
		}
		return InvoiceUtil.sumInvoiceLines(theInvoiceLines)
	}

	void updateAllAmountsOwingForPayer() {
		for (final Invoice invoice : getContact().getInvoices()) {
			invoice.updateAmountOwing()
		}
	}

	/**
	 * @return
	 */
	@Nonnull
	List<PayableLineInterface> getPayableLines() {
		ArrayList<PayableLineInterface> list = new ArrayList<>()
		list.addAll(getLines())
		return list
	}

	void setContact(ContactInterface contact) {
		if (contact instanceof Contact) {
			super.setContact((Contact) contact)
		} else {
			throw new IllegalArgumentException("expected Contact.class, was " + contact.getClass())
		}
	}

	void updateAmountOwing() {
		InvoiceUtil.updateAmountOwing(this)
	}

	void updateDateDue() {
		if (!getInvoiceDueDates().isEmpty()) {

			Money amountPaid = getAmountPaid()
			List<InvoiceDueDate> dueDates = getInvoiceDueDates()

			InvoiceDueDate.DUE_DATE.asc().orderList(dueDates)
			Money payedLinesSum = Money.ZERO
			for (InvoiceDueDate dueDate : dueDates) {
				payedLinesSum = payedLinesSum.add(dueDate.getAmount())
				if (amountPaid.isLessThan(payedLinesSum)) {
					this.setDateDue(dueDate.getDueDate())
					return
				}
			}
		}
	}

	void updateOverdue() {
		LocalDate currentDate = LocalDate.now()
		if (!getInvoiceDueDates().isEmpty()) {
			List<InvoiceDueDate> dueDates = getInvoiceDueDates()

			InvoiceDueDate.DUE_DATE.asc().orderList(dueDates)
			Money overdue = Money.ZERO

			for (InvoiceDueDate dueDate : dueDates) {
				if (currentDate.isAfter(dueDate.getDueDate())) {
					overdue = overdue.add(dueDate.getAmount())
				}
			}

			overdue = overdue.subtract(getAmountPaid())
			setOverdue(overdue.isGreaterThan(Money.ZERO) ? overdue : Money.ZERO)
		} else {
			setOverdue(getDateDue().isAfter(currentDate) ? Money.ZERO : getAmountOwing())
		}
	}

	/**
	 * @see ish.print.PrintableObject#getShortRecordDescription()
	 * @return
	 */
	@Nullable
	String getShortRecordDescription() {
		// not important on server
		return null
	}

	@Override
	void addToAttachmentRelations(AttachmentRelation relation) {
		if (relation instanceof InvoiceAttachmentRelation) {
			addToAttachmentRelations((InvoiceAttachmentRelation) relation)
		} else {
			throw new IllegalArgumentException("expected InvoiceAttachmentRelation.class, was " + relation.getClass())
		}
	}

	@Override
	void removeFromAttachmentRelations(AttachmentRelation relation) {
		if (relation instanceof InvoiceAttachmentRelation) {
			removeFromAttachmentRelations((InvoiceAttachmentRelation) relation)
		} else {
			throw new IllegalArgumentException("expected InvoiceAttachmentRelation.class, was " + relation.getClass())
		}
	}

	@Override
	Class<? extends AttachmentRelation> getRelationClass() {
		return InvoiceAttachmentRelation.class
	}

	/**
	 *
	 * @return the total without tax
	 */
	@API
	Money getTotal() {
		List<AbstractInvoiceLine> theInvoiceLines = getLines()
		if (theInvoiceLines == null || theInvoiceLines.size() == 0) {
			return Money.ZERO
		}
		return InvoiceUtil.sumInvoiceLines(theInvoiceLines, false)
	}

	/**
	 *
	 * @return the total amount of tax included in this invoice
	 */
	@API
	Money getTotalTax() {
		Money result = Money.ZERO
		Money exTotal = getTotal()
		Money incTotal = getTotalIncTax()

		// if either ex or inc totals are zero or equal to each other
		// return the current result of zero
		if (result == exTotal || result == incTotal || exTotal == incTotal) {
			return result
		}

		return incTotal.subtract(exTotal)
	}

	/**
	 * Returns string name of SystemUser who created the invoice.
	 * Use getCreatedBy().getFullName() instead
	 *
	 * @return SystemUser name
	 */
	@Deprecated
	String getCreatedByUserName() {

		SystemUser user = getCreatedByUser()
		if (user != null) {
			return String.format("%s %s", user.getFirstName(), user.getLastName())
		}

		return StringUtils.EMPTY
	}

	/**
	 * @return billing address for this invoice
	 */
	@API
	@Override
	String getBillToAddress() {
		return super.getBillToAddress()
	}

	/**
	 * A status which records whether the invoice email has already been sent or is disabled.
	 *
	 * @return status indicating whether a confirmation email has been sent
	 */
	@Nonnull
	@API
	@Override
	ConfirmationStatus getConfirmationStatus() {
		return super.getConfirmationStatus()
	}

	/**
	 * @return the date and time this record was created
	 */
	@API
	@Override
	Date getCreatedOn() {
		return super.getCreatedOn()
	}

	/**
	 * Some arbitrary piece of text such as a PO number or customer reference which is displayed on the printed invoice.
	 *
	 * @return
	 */
	@API
	@Override
	String getCustomerReference() {
		return super.getCustomerReference()
	}

	/**
	 * @return the date on which this invoice amount falls due
	 * TODO: how does this work with payment plans?
	 */
	@Nonnull
	@API
	@Override
	LocalDate getDateDue() {
		return super.getDateDue()
	}

	/**
	 * The invoice description might be shown on printed reports or in emails.
	 *
	 * @return invoice desciption text
	 */
	@API
	@Override
	String getDescription() {
		return super.getDescription()
	}


	/**
	 * @return date when this invoice was created
	 */
	@Nonnull
	@API
	@Override
	LocalDate getInvoiceDate() {
		return super.getInvoiceDate()
	}

	/**
	 * @return the date and time this record was modified
	 */
	@API
	@Override
	Date getModifiedOn() {
		return super.getModifiedOn()
	}

	/**
	 * These notes will be printed on the invoice and displayed to the recipient in emails and other places.
	 *
	 * @return public notes attached to invoice
	 */
	@API
	@Override
	String getPublicNotes() {
		return super.getPublicNotes()
	}

	/**
	 * @return shipping address for this invoice
	 */
	@API
	@Override
	String getShippingAddress() {
		return super.getShippingAddress()
	}

	/**
	 * Payment can be made in onCourse (office) or from the onCourse website (web). The invoice source is bound to the same enum as for payments.
	 *
	 * @return where the payment for this invoice was made
	 */
	@Nonnull
	@API
	@Override
	PaymentSource getSource() {
		return super.getSource()
	}

	@Override
	List<PaymentLineInterface> getPaymentLines() {
		return new ArrayList<PaymentLineInterface>()
	}

	/**
	 * @return CorporatePass record if purchase which resulted in creation of this invoice was made using CorporatePass, null if not
	 */
	@Nonnull
	@API
	@Override
	CorporatePass getCorporatePassUsed() {
		return super.getCorporatePassUsed()
	}

	/**
	 * The user who created this invoice, or null if it was created through a web purchase.
	 *
	 * @return internal user who created this invoice
	 */
	@Nullable
	@API
	@Override
	SystemUser getCreatedByUser() {
		return super.getCreatedByUser()
	}

	/**
	 * The invoice overdue amount is calculated based on not just the invoice total, but the whole payment plan
	 * and each due date.
	 *
	 * @return Current overdue amount
	 */
	@Nonnull @Override @API
	Money getOverdue() {
		return super.getOverdue()
	}

	/**
	 * When a credit card is used to pay for an invoice, a reference is stored to allow
	 * a subsequent payment to be processed or a refund issued. This reference points to the paymentIn
	 * record which stores the reference for that card.
	 *
	 * Note that onCourse never stores the actual complete card details since that would cause a breach of PCI DSS.
	 * This reference can only be used to process another payment or refund between this card and the same merchant.
	 *
	 * @return paymentIn record with a credit card reference attacehd
	 */
	@Nullable @Override @API
	PaymentIn getAuthorisedRebillingCard() {
		return super.getAuthorisedRebillingCard()
	}

	/**
	 *
	 * @return all the due dates (payment plan) for this invoice
	 */
	@Nonnull @Override @API
	List<InvoiceDueDate> getInvoiceDueDates() {
		return super.getInvoiceDueDates()
	}

	@Override
	String getSummaryDescription() {
		return getDescription()
	}
}



