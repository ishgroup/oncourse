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
import ish.common.payable.PayableLineInterface
import ish.common.types.ConfirmationStatus
import ish.common.types.EnrolmentStatus
import ish.common.types.InvoiceType
import ish.common.types.PaymentSource
import ish.math.Money
import ish.oncourse.API
import ish.oncourse.cayenne.ContactInterface
import ish.oncourse.cayenne.InvoiceInterface
import ish.oncourse.cayenne.PayableInterface
import ish.oncourse.cayenne.PaymentLineInterface
import ish.oncourse.server.cayenne.glue._AbstractInvoice
import ish.oncourse.server.cayenne.glue._Enrolment
import ish.oncourse.server.services.IAutoIncrementService
import ish.util.InvoiceUtil
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.exp.ExpressionFactory
import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.annotation.Nonnull
import javax.annotation.Nullable
import java.time.LocalDate

class AbstractInvoice extends _AbstractInvoice implements PayableInterface, InvoiceInterface, Queueable, NotableTrait, AttachableTrait {

	private static final String CONTACT_KEY = "contact";
	private static final String INVOICE_DATE_KEY = "invoiceDate";
	private static final String TOTAL_KEY = "total";
	private static final String TOTAL_TAX_KEY = "totalTax";
	private static final String TOTAL_INC_TAX_KEY = "totalIncTax";
	private static final String AMOUNT_OWING_KEY = "amountOwing";
	private static final String AMOUNT_PAID_KEY = "amountPaid";

	String TOTAL_INC_TAX_PROPERTY = "invoice_total_inc_property";

	private static final Logger logger = LogManager.getLogger()
	public static final String AMOUNT_OWING_PROPERTY = "amountOwing"
	public static final String AMOUNT_PAID_PROPERTY = "amountPaid"

	public static final String TOTAL_INC_GST_PROPERTY = "totalIncTax"
	public static final String TOTAL_EX_GST_PROPERTY = "total"
	public static final String TOTAL_TAX_PROPERTY = "totalTax"

	public static final String CREATED_BY_USER_NAME_PROPERTY = "createdByUserName"

	@Inject
	private transient IAutoIncrementService autoIncrementService

	/**
	 * Builds a list of enrolments from the invoice lines mapped per student.
	 *
	 * @return a map of enrolments (values as Collection) per student (keys)
	 */
	@Nonnull
	@API
	Map<Student, Set<Enrolment>> enrolmentsPerStudent() {
		final Map<Student, Set<Enrolment>> results = new HashMap<>()

		for (final InvoiceLine aLine : getInvoiceLines()) {
			final Enrolment anEnrolment = aLine.getEnrolment()
			final Student aStudent = anEnrolment.getStudent()
			Set<Enrolment> studentEnrolments = results.get(aStudent)
			if (studentEnrolments == null) {
				studentEnrolments = new HashSet<>()
				results.put(aStudent, studentEnrolments)
			}
			studentEnrolments.add(anEnrolment)
		}
		return results
	}

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

	@Override
	void onEntityCreation() {
		super.onEntityCreation()
		// those fields are set to default on client to pass validation
		if (getInvoiceDate() == null) {
			setInvoiceDate(LocalDate.now())
		}
		if (getDateDue() == null) {
			setDateDue(LocalDate.now())
		}
		if (getSource() == null) {
			setSource(PaymentSource.SOURCE_ONCOURSE)
		}
		if (getOverdue() == null) {
			setOverdue(Money.ZERO)
		}
		if (getType() == null) {
			setType(InvoiceType.INVOICE)
		}

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
		List<InvoiceLine> theInvoiceLines = getInvoiceLines()
		if (theInvoiceLines == null || theInvoiceLines.size() == 0) {
			return Money.ZERO
		}
		return InvoiceUtil.sumInvoiceLines(theInvoiceLines)
	}

	@Override
	void postAdd() {
		super.postAdd()
		// SPECIFIC DEFAULT VALUES
		if (getInvoiceDate() == null) {
			setInvoiceDate(LocalDate.now())
		}
		if (getDateDue() == null) {
			setDateDue(LocalDate.now())
		}
		if (getInvoiceNumber() == null) {
			setInvoiceNumber(autoIncrementService.getNextInvoiceNumber())
		}
		if (getConfirmationStatus() == null) {
			setConfirmationStatus(ConfirmationStatus.NOT_SENT)
		}
		if (getOverdue() == null) {
			setOverdue(Money.ZERO)
		}
		if (getType() == null) {
			setType(InvoiceType.INVOICE)
		}
	}

	void updateAllAmountsOwingForPayer() {
		for (final Invoice invoice : getContact().getInvoices()) {
			invoice.updateAmountOwing()
		}
	}

	/**
	 * @return
	 */
	@Nullable
	List<Enrolment> getEnrolmentsWithStatus(final EnrolmentStatus enrolmentStatus) {
		List<Enrolment> result = null
		Expression expr = ExpressionFactory.noMatchExp(InvoiceLine.ENROLMENT_PROPERTY, null)
		expr = expr.andExp(ExpressionFactory.matchExp(InvoiceLine.ENROLMENT_PROPERTY + "." + _Enrolment.STATUS_PROPERTY, enrolmentStatus))

		final List<InvoiceLine> invoiceLines = expr.filterObjects(getInvoiceLines())
		for (InvoiceLine invoiceLine : invoiceLines) {
			if (result == null) {
				result = new ArrayList<>()
			}
			if (invoiceLine != null && invoiceLine.getEnrolment() != null) {
				result.add(invoiceLine.getEnrolment())
			}
		}
		return result
	}

	/**
	 * @return
	 */
	@Nonnull
	List<PayableLineInterface> getPayableLines() {
		ArrayList<PayableLineInterface> list = new ArrayList<>()
		list.addAll(getInvoiceLines())
		return list
	}

	/**
	 * Get a list of paymentInLines and paymentOutLines linked to this invoice
	 * @return all paymentLines against this invoice
	 */
	@Nonnull
	@API
	List<PaymentLineInterface> getPaymentLines() {
		ArrayList<PaymentLineInterface> list = new ArrayList<>()
		list.addAll(getPaymentInLines())
		list.addAll(getPaymentOutLines())
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
		List<InvoiceLine> theInvoiceLines = getInvoiceLines()
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

	@Override
	void prePersist() {
		super.prePersist()
		updateAmountOwing()
		updateDateDue()
		updateOverdue()
	}

	@Override
	void preUpdate() {
		super.preUpdate()
		updateAmountOwing()
		updateDateDue()
		updateOverdue()
	}

	void removeFromPaymentLines(PaymentLineInterface pLine) {
		if (pLine instanceof PaymentInLine) {
			removeFromPaymentInLines((PaymentInLine) pLine)
		} else if (pLine instanceof PaymentOutLine) {
			removeFromPaymentOutLines((PaymentOutLine) pLine)
		}
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
	 * @return unique invoice number
	 */
	@Nonnull
	@API
	@Override
	Long getInvoiceNumber() {
		return super.getInvoiceNumber()
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

	/**
	 * This is the contact to whom the invoice was issued. They are liable for the debt this invoice represents.
	 * Note that the invoice contact might not be the same contact as the person enrolled in classes linked to
	 * invoice lines.
	 *
	 * @return to whom the invoice was issued
	 */
	@Nonnull
	@API
	@Override
	Contact getContact() {
		return super.getContact()
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
	 * @return
	 */
	@Nonnull
	@API
	@Override
	Account getDebtorsAccount() {
		return super.getDebtorsAccount()
	}

	/**
	 * @return list of invoice line records linked to this invoice
	 */
	@Nonnull
	@API
	@Override
	List<InvoiceLine> getInvoiceLines() {
		return super.getInvoiceLines()
	}

	/**
	 * Follow this join to find all payments made against this invoice. Remember that onCourse supports multiple payments
	 * against one invoice and also partial payments against an invoice, so you can follow this join to many payments
	 * and some of those payment may also link to other invoices.
	 *
	 * @return all payementInLines against this invoice
	 */
	@Nonnull
	@API
	@Override
	List<PaymentInLine> getPaymentInLines() {
		return super.getPaymentInLines()
	}

	/**
	 * If this invoice was a credit note, then a PaymentOut (or several) might be linked against it.
	 *
	 * @return all payementOutLines against this invoice
	 */
	@Nonnull
	@API
	@Override
	List<PaymentOutLine> getPaymentOutLines() {
		return super.getPaymentOutLines()
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

	//fixme: temporary workaround OD-12674
	@Override
	void addToPaymentOutLines(PaymentOutLine obj) {
		if (obj != null) {
			obj.setInvoice((Invoice) this)
		}
	}

	@Override
	void removeFromPaymentOutLines(PaymentOutLine obj) {
		if (obj != null) {
			obj.setInvoice(null)
		}
	}
	@Override
	void addToPaymentInLines(PaymentInLine obj) {
		if (obj != null) {
			obj.setInvoice((Invoice) this)
		}
	}
	@Override
	void removeFromPaymentInLines(PaymentInLine obj) {
		if (obj != null) {
			obj.setInvoice(null)
		}
	}
	@Override
	void addToNoteRelations(InvoiceNoteRelation obj) {
		if (obj != null) {
			obj.setNotedInvoice((Invoice)this)
		}
	}

	@Override
	void removeFromNoteRelations(InvoiceNoteRelation obj) {
		if (obj != null) {
			obj.setNotedInvoice(null)
		}
	}
	@Override
	void addToInvoiceLines(InvoiceLine obj) {
		if (obj != null) {
			obj.setInvoice((Invoice) this)
		}
	}
	@Override
	void removeFromInvoiceLines(InvoiceLine obj) {
		if (obj != null) {
			obj.setInvoice(null)
		}
	}
	@Override
	void addToInvoiceDueDates(InvoiceDueDate obj) {
		if (obj != null) {
			obj.setInvoice((Invoice) this)
		}
	}
	@Override
	void removeFromInvoiceDueDates(InvoiceDueDate obj) {
		if (obj != null) {
			obj.setInvoice(null)
		}
	}

	@Override
	void setDebtorsAccount(Account obj) {
		if (obj != null) {
			obj.addToInvoices((Invoice) this)
		}
	}
	@Override
	void setCreatedByUser(SystemUser obj) {
		if (obj != null) {
			obj.addToInvoicesCreated((Invoice) this)
		}
	}

	@Override
	void setCorporatePassUsed(CorporatePass obj) {
		if (obj != null) {
			obj.addToInvoices((Invoice) this)
		}
	}
	@Override
	void setContact(Contact obj) {
		if (obj != null) {
			obj.addToInvoices((Invoice)this)
		}
	}
	@Override
	void setAuthorisedRebillingCard(PaymentIn obj) {
		if (obj != null) {
			obj.addToAuthorisedInvoices((Invoice)this)
		}
	}
	@Override
	void addToAttachmentRelations(InvoiceAttachmentRelation obj) {
		if (obj != null) {
			obj.setAttachedInvoice((Invoice) this)
		}
	}

	@Override
	void removeFromAttachmentRelations(InvoiceAttachmentRelation obj) {
		if (obj != null) {
			obj.setAttachedInvoice(null)
		}
	}

	@Override
	String getSummaryDescription() {
		if (getDescription() == null) {
			return "#" + getInvoiceNumber()
		}
		return "#" + getInvoiceNumber() + " " + getDescription()
	}
}



