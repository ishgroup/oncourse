package ish.oncourse.model;

import ish.common.types.InvoiceType;
import ish.math.Money;
import ish.oncourse.model.auto._Invoice;
import org.apache.cayenne.validation.ValidationResult;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Date;

public class Invoice extends _Invoice {

	public Invoice createRefundInvoice() {
		final Invoice refundInvoice = getObjectContext().newObject(Invoice.class);

		refundInvoice.setCollege(getCollege());
		refundInvoice.setBillToAddress(getBillToAddress());
		refundInvoice.setDescription("Refund for enrolments");
		refundInvoice.setPublicNotes(getPublicNotes());
		refundInvoice.setShippingAddress(getShippingAddress());
		refundInvoice.setInvoiceDate(DateUtils.setHours(new Date(), 12));
		refundInvoice.setDateDue(new Date());
		refundInvoice.setContact(getContact());
		refundInvoice.setTotalExGst(Money.ZERO.subtract(getTotalExGst()));
		refundInvoice.setTotalGst(Money.ZERO.subtract(getTotalGst()));
		refundInvoice.setAmountOwing(Money.ZERO.subtract(getAmountOwing()));
		refundInvoice.setSource(this.getSource());
		refundInvoice.setWebSite(this.getWebSite());

		final ValidationResult vr = new ValidationResult();
		// Mark all the enrolments as refunded.
		for (int i = 0; i < getInvoiceLines().size(); i++) {
			final InvoiceLine il = getInvoiceLines().get(i);
			il.refund(vr, this, refundInvoice);
		}

		return refundInvoice;
	}

	@Override
	public InvoiceType getType() {
		return InvoiceType.INVOICE;
	}

	/**
	 * Makes a copy of current Invoice object.
	 *
	 * @return invoice.
	 */
	public Invoice makeCopy(){
		Invoice invoice = makeShallowCopy();

		for (InvoiceLine line: getInvoiceLines()) {
			InvoiceLine invoiceLine = line.makeCopy();
			invoiceLine.setInvoice(invoice);
		}
		return invoice;
	}

	/**
	 * Makes shallow copy of current Invoice object.
	 *
	 * @return Invoice.
	 */
	public Invoice makeShallowCopy() {
		Invoice invoice = objectContext.newObject(Invoice.class);

		invoice.setCollege(getCollege());
		invoice.setContact(getContact());

		invoice.setType(getType());
		invoice.setAmountOwing(getAmountOwing());
		invoice.setInvoiceDate(getInvoiceDate());
		invoice.setInvoiceNumber(getInvoiceNumber());
		invoice.setDateDue(getDateDue());
		invoice.setAllowAutoPay(getAllowAutoPay());
		invoice.setConfirmationStatus(getConfirmationStatus());
		invoice.setTotalExGst(getTotalExGst());
		invoice.setTotalGst(getTotalGst());
		invoice.setSource(getSource());
		invoice.setDescription(getDescription());

		return invoice;
	}
}
