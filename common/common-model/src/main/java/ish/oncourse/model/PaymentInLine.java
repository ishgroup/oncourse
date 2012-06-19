package ish.oncourse.model;

import ish.oncourse.model.auto._PaymentInLine;
import ish.oncourse.utils.QueueableObjectUtils;

import org.apache.cayenne.validation.ValidationResult;
import org.apache.log4j.Logger;

public class PaymentInLine extends _PaymentInLine implements Queueable {

	private static final long serialVersionUID = -6157950790523998485L;
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(PaymentInLine.class);

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	/**
	 * Creates a copy of current paymentInLine object.
	 * 
	 * @return copy of paymentInLine object
	 */
	public PaymentInLine makeCopy() {
		PaymentInLine pl = getObjectContext().newObject(PaymentInLine.class);
		pl.setAmount(getAmount());
		pl.setCollege(getCollege());
		pl.setCreated(getCreated());
		pl.setInvoice(getInvoice());
		pl.setModified(getModified());
		return pl;
	}

	protected void onPostAdd() {
		/*final Long paymentInId = (getPaymentIn() != null) ? getPaymentIn().getId() : null;
		final Long invoiceId = (getInvoice() != null) ? getInvoice().getId() : null;

		if (LOG.isDebugEnabled()) {
			LOG.debug("Trace the source of PaymentInLines.", new Exception("trace post add for PaymentInLine with paymentin id = "
					+ paymentInId + " and invoiceid = " + invoiceId + " and PaymentInLineId = " + getId() + " and amount = " + getAmount()));
		}*/
	}

	protected void onPrePersist() {
		/*final Long paymentInId = (getPaymentIn() != null) ? getPaymentIn().getId() : null;
		final Long invoiceId = (getInvoice() != null) ? getInvoice().getId() : null;

		if (LOG.isDebugEnabled()) {
			LOG.debug("Trace the source of PaymentInLines.", new Exception("trace persist new PaymentInLine with paymentin id = "
					+ paymentInId + " and invoiceid = " + invoiceId + " and PaymentInLineId = " + getId() + " and amount = " + getAmount()));
		}*/
	}

	protected void onPreUpdate() {
		/*final Long paymentInId = (getPaymentIn() != null) ? getPaymentIn().getId() : null;
		final Long invoiceId = (getInvoice() != null) ? getInvoice().getId() : null;

		if (LOG.isDebugEnabled()) {
			LOG.debug("Trace the source of PaymentInLines.", new Exception("trace update of PaymentInLine with paymentin id = "
					+ paymentInId + " and invoiceid = " + invoiceId + " and PaymentInLineId = " + getId() + " and amount = " + getAmount()));
		}*/
	}

	/**
	 * Validation to prevent saving unbalanced PaymentIn into database.
	 */
	@Override
	protected void validateForSave(final ValidationResult result) {
		super.validateForSave(result);

		// Amount - mandatory field
		if (getAmount() == null) {
			result.addFailure(ValidationFailure.validationFailure(this, _PaymentInLine.AMOUNT_PROPERTY, "The payment amount cannot be empty."));
			return;
		}
		
		getPaymentIn().validateForSave(result);
	}

	/**
	 * Checks if async replication allowed on entity.
	 * @return
	 */
	public boolean isAsyncReplicationAllowed() {
		return getPaymentIn().isAsyncReplicationAllowed();
	}
}
