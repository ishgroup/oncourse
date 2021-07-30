package ish.oncourse.model;

import ish.common.types.InvoiceType;
import ish.math.Money;
import ish.oncourse.model.auto._Invoice;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.query.ObjectIdQuery;
import org.apache.cayenne.validation.ValidationResult;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Date;
import java.util.List;

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

	public List<InvoiceLine> getLines() {
		return getInvoiceLines();
	};

	/**
	 * Check if async replication is allowed on this object.
	 *
	 * We need the method  to not add to the async replication a payment transactions
	 * which have not got the final status yet (DPS processing).
	 */
	public boolean isAsyncReplicationAllowed() {

		List<PaymentInLine> lines = getPaymentInLines();

		// We check linked payments, if one of them can replicate invoice can replicate too.
		if (!lines.isEmpty()) {
			for (PaymentInLine line : lines) {
				PaymentIn paymentIn = line.getPaymentIn();
				ObjectIdQuery q = new ObjectIdQuery(paymentIn.getObjectId(), false, ObjectIdQuery.CACHE_REFRESH);
				paymentIn = (PaymentIn) Cayenne.objectForQuery(getObjectContext(), q);
				if (paymentIn.isAsyncReplicationAllowed()) {
					return true;
				}
			}
			return false;
		}

		return super.isAsyncReplicationAllowed();
	}

	@Override
	public void addToLines(AbstractInvoiceLine abstractLine) {
		super.addToInvoiceLines((InvoiceLine) abstractLine);
	}

	public Money getOverdue() {
		if (getAmountOwing().isZero()) {
			return Money.ZERO;
		}
		Date currentDate = new Date();

		if (!getInvoiceDueDates().isEmpty()) {
			List<InvoiceDueDate> dueDates = getInvoiceDueDates();

			InvoiceDueDate.DUE_DATE.asc().orderList(dueDates);
			Money overdue = Money.ZERO;

			for (InvoiceDueDate dueDate : dueDates) {
				if (currentDate.after(dueDate.getDueDate())) {
					overdue = overdue.add(dueDate.getAmount());
				}
			}
			Money amountPaid = getTotalGst().subtract(getAmountOwing());
			overdue = overdue.subtract(amountPaid);
			return overdue.isGreaterThan(Money.ZERO) ? overdue : Money.ZERO;
		} else {
			return getDateDue().after(currentDate) ? Money.ZERO : getAmountOwing();
		}

	}
}
