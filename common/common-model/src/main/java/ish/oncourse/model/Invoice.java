package ish.oncourse.model;

import ish.common.types.PaymentSource;
import ish.common.types.PaymentStatus;
import ish.math.Money;
import ish.oncourse.model.auto._Invoice;
import ish.oncourse.utils.QueueableObjectUtils;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.ObjectIdQuery;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.validation.ValidationResult;

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
		refundInvoice.setInvoiceDate(new Date());
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
		Expression expr = ExpressionFactory.matchExp(Invoice.CONTACT_PROPERTY, getContact());
		expr = expr.andExp(ExpressionFactory.matchExp(Invoice.COLLEGE_PROPERTY, getCollege()));
		expr = expr.andExp(ExpressionFactory.matchExp(Invoice.AMOUNT_OWING_PROPERTY, Money.ZERO.subtract(getAmountOwing())));
		SelectQuery q = new SelectQuery(Invoice.class, expr);
		return getObjectContext().performQuery(q);
	}

	public void updateAmountOwing() {
		// update invoice owing
		Money totalCredit = getTotalCredit();
		Money totalInvoiced = getTotalInvoiced();
		Money totalOwing = totalInvoiced.subtract(totalCredit);
		setAmountOwing(totalOwing);
	}
	
	private Money getTotalCredit() {
		Money result = Money.ZERO;
		List<PaymentInLine> paymentLines = getPaymentInLines();
		for (PaymentInLine paymentLine : paymentLines) {
			if (PaymentStatus.SUCCESS.equals(paymentLine.getPaymentIn().getStatus())) {
				result = result.add(paymentLine.getAmount());
			}
		}
		return result;
	}

	private Money getTotalInvoiced() {
		Money result = Money.ZERO;
		List<InvoiceLine> invoiceLines = getInvoiceLines();
		for (InvoiceLine invoiceLine : invoiceLines) {
			result = result.add(invoiceLine.getFinalPriceToPayIncTax());
		}
		return result;
	}

	@Override
	protected void onPostAdd() {
		if (getSource() == null) {
			setSource(PaymentSource.SOURCE_WEB);
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
	 * @return true
	 */
	public boolean isAsyncReplicationAllowed() {
		
		// TODO this code is needed for compatibility with 2.1.x automatic email  
		// confirmations queuing mechanism and should be removed after we'll 
		// stop support 2.1.x clients.
		//
		// For angel version 3.0 and higher we may always return true here
		
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
