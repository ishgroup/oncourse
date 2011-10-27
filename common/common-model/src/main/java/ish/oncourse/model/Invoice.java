package ish.oncourse.model;

import ish.common.types.PaymentSource;
import ish.common.types.PaymentStatus;
import ish.math.Money;
import ish.oncourse.model.auto._Invoice;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.validation.ValidationResult;

public class Invoice extends _Invoice implements Queueable {

	/**
	 * Returns the primary key property - id of {@link Invoice}.
	 * 
	 * @return
	 */
	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? (Long) getObjectId().getIdSnapshot().get(
				ID_PK_COLUMN) : null;
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
		refundInvoice.setTotalExGst(BigDecimal.ZERO.subtract(getTotalExGst()));
		refundInvoice.setTotalGst(BigDecimal.ZERO.subtract(getTotalGst()));
		refundInvoice.setStatus(InvoiceStatus.SUCCESS);
		refundInvoice.setAmountOwing(BigDecimal.ZERO.subtract(getAmountOwing()));
		refundInvoice.setSource(PaymentSource.SOURCE_WEB);

		final ValidationResult vr = new ValidationResult();
		// Mark all the enrolments as refunded.
		for (int i = 0; i < getInvoiceLines().size(); i++) {
			final InvoiceLine il = getInvoiceLines().get(i);
			il.refund(vr, this, refundInvoice);
		}

		return refundInvoice;
	}

	public List<Invoice> getRefundedInvoices() {
		Expression expr = ExpressionFactory.matchExp(Invoice.CONTACT_PROPERTY, getContact());
		expr = expr.andExp(ExpressionFactory.matchExp(Invoice.COLLEGE_PROPERTY, getCollege()));
		expr = expr.andExp(ExpressionFactory.matchExp(Invoice.AMOUNT_OWING_PROPERTY,
				BigDecimal.ZERO.subtract(getAmountOwing())));
		SelectQuery q = new SelectQuery(Invoice.class, expr);
		return getObjectContext().performQuery(q);
	}

	public void updateAmountOwing() {
		// update invoice owing

		Money totalCredit = getTotalCredit();
		Money totalInvoiced = getTotalInvoiced();
		Money totalOwing = totalInvoiced.subtract(totalCredit);

		setAmountOwing(totalOwing.toBigDecimal());
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
	protected void onPreUpdate() {
		updateAmountOwing();
	}

	@Override
	protected void onPrePersist() {
		updateAmountOwing();
		
		if (getStatus() == null) {
			setStatus(InvoiceStatus.PENDING);
		}
	}

	@Override
	public void setStatus(InvoiceStatus status) {
		super.setStatus(status);
		Date now = new Date();
		for (InvoiceLine line : getInvoiceLines()) {
			line.setModified(now);
			for(InvoiceLineDiscount ilDiscount: line.getInvoiceLineDiscounts()){
				ilDiscount.setModified(now);
			}
		}
	}
	
}
