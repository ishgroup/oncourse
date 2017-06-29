package ish.oncourse.model;

import ish.common.types.ConfirmationStatus;
import ish.common.types.PaymentSource;
import ish.math.Money;
import ish.oncourse.model.auto._Invoice;
import ish.oncourse.utils.QueueableObjectUtils;
import ish.oncourse.utils.invoice.GetAmountOwing;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.query.ObjectIdQuery;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.validation.ValidationResult;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Date;
import java.util.List;

public class Invoice extends _Invoice implements Queueable {

	private static final long serialVersionUID = 2985348837041766278L;

	/**
	 * Returns the primary key property - id of {@link Invoice}.
	 * 
	 * @return
	 */
	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

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

	@SuppressWarnings("unchecked")
	public List<Invoice> getRefundedInvoices() {
		return ObjectSelect.query(Invoice.class).
				where(Invoice.CONTACT.eq(getContact())).
				and(Invoice.COLLEGE.eq(getCollege())).
				and(Invoice.AMOUNT_OWING.eq(Money.ZERO.subtract(getAmountOwing()))).
				select(getObjectContext());
	}

	public void updateAmountOwing() {
		setAmountOwing(GetAmountOwing.valueOf(this).get());
	}
	

	@Override
	protected void onPostAdd() {
		if (getSource() == null) {
			setSource(PaymentSource.SOURCE_WEB);
		}
		if (getCreated() == null) {
			setCreated(new Date());
		}
		if (getModified() == null) {
			setModified(getCreated());
		}
		if (getConfirmationStatus() == null) {
			setConfirmationStatus(ConfirmationStatus.DO_NOT_SEND);
		}
	}

	@Override
	protected void onPrePersist() {
		onPostAdd();
	}

	@Override
	protected void onPreUpdate() {
	}

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
		
		// If invoice is not yet linked to any payments.
		for (InvoiceLine invLine : getInvoiceLines()) {
			Enrolment enrol = invLine.getEnrolment();
			if (enrol != null) {
				ObjectIdQuery q = new ObjectIdQuery(enrol.getObjectId(), false, ObjectIdQuery.CACHE_REFRESH);
				enrol = (Enrolment) Cayenne.objectForQuery(getObjectContext(), q);
				if (!enrol.isAsyncReplicationAllowed()) {
					return false;
				}
			}
		}
		
		return true;
	}
}
