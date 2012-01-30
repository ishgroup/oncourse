package ish.oncourse.model;

import ish.math.Money;
import ish.oncourse.model.auto._PaymentInLine;
import ish.oncourse.utils.QueueableObjectUtils;

import org.apache.cayenne.validation.ValidationResult;
import org.apache.log4j.Logger;

public class PaymentInLine extends _PaymentInLine implements Queueable {

	private static final long serialVersionUID = -6157950790523998485L;
	private static final Logger LOG = Logger.getLogger(PaymentInLine.class);

	public College getCollege() {
		return getPaymentIn().getCollege();
	}

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
		final Long paymentInId = (getPaymentIn() != null) ? getPaymentIn().getId() : null;
		final Long invoiceId = (getInvoice() != null) ? getInvoice().getId() : null;

		if (LOG.isInfoEnabled()) {
			LOG.info("Trace the source of PaymentInLines.", new Exception("trace post add for PaymentInLine with paymentin id = "
					+ paymentInId + " and invoiceid = " + invoiceId + " and PaymentInLineId = " + getId() + " and amount = " + getAmount()));
		}
	}

	protected void onPrePersist() {
		final Long paymentInId = (getPaymentIn() != null) ? getPaymentIn().getId() : null;
		final Long invoiceId = (getInvoice() != null) ? getInvoice().getId() : null;

		if (LOG.isInfoEnabled()) {
			LOG.info("Trace the source of PaymentInLines.", new Exception("trace persist new PaymentInLine with paymentin id = "
					+ paymentInId + " and invoiceid = " + invoiceId + " and PaymentInLineId = " + getId() + " and amount = " + getAmount()));
		}
	}

	protected void onPreUpdate() {
		final Long paymentInId = (getPaymentIn() != null) ? getPaymentIn().getId() : null;
		final Long invoiceId = (getInvoice() != null) ? getInvoice().getId() : null;

		if (LOG.isInfoEnabled()) {
			LOG.info("Trace the source of PaymentInLines.", new Exception("trace update of PaymentInLine with paymentin id = "
					+ paymentInId + " and invoiceid = " + invoiceId + " and PaymentInLineId = " + getId() + " and amount = " + getAmount()));
		}
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

		final Long currentPaymentInId = (getPaymentIn() != null) ? getPaymentIn().getId() : null;
		final Long currentInvoiceId = (getInvoice() != null) ? getInvoice().getId() : null;

		for (final PaymentInLine pil : getPaymentIn().getPaymentInLines()) {

			if (!this.equals(pil)) {
				// check for other paymentinline
				final Long paymentInId = (pil.getPaymentIn() != null) ? pil.getPaymentIn().getId() : null;
				final Long invoiceId = (pil.getInvoice() != null) ? pil.getInvoice().getId() : null;

				if (currentPaymentInId != null && currentInvoiceId != null) {
					if (currentPaymentInId.equals(paymentInId) && currentInvoiceId.equals(invoiceId)) {
						result.addFailure(ValidationFailure.validationFailure(this, _PaymentInLine.PAYMENT_IN_PROPERTY,
							"PaymentIn id = " + currentPaymentInId +" and Invoice id " + currentInvoiceId + 
							" couldn't be equal in different PaymentInLines. " + getId() + " and " + pil.getId() + " with sourse = " + 
							getPaymentIn().getSource()));
					}
				} else if (getPaymentIn() != null && getPaymentIn().equals(pil.getPaymentIn()) && getInvoice() != null
						&& getInvoice().equals(pil.getInvoice())) {
					result.addFailure(ValidationFailure.validationFailure(this, _PaymentInLine.PAYMENT_IN_PROPERTY,
						"PaymentIn and Invoice couldn't be equal in different PaymentInLines. " + getId() + " and " + pil.getId() + 
						" with sourse = " + getPaymentIn().getSource()));
				}
			}
		}

		final Money amount = new Money(getPaymentIn().getAmount());
		Money sum = Money.ZERO;
		for (final PaymentInLine paymentInLine : getPaymentIn().getPaymentInLines()) {
			sum = sum.add(paymentInLine.getAmount());
		}
		if (!amount.equals(sum)) {
			// some data after migration doesn't have angelId properly set on
			// paymentInLines
			// that is why we need this check too.
			if (getPaymentIn().isCommittedSumValid()) {
				// unregister not committed lines
				getPaymentIn().unregisterNotCommittedLines();
			} else {
				result.addFailure(ValidationFailure.validationFailure(this, _PaymentInLine.AMOUNT_PROPERTY, String.format(
						"The payment willowId:%s angelId:%s amount does not match the sum of amounts allocated for invoices/credit notes.",
						getId(), getAngelId())));
			}
		}

	}

	/**
	 * Checks if async replication allowed on entity.
	 * @return
	 */
	public boolean isAsyncReplicationAllowed() {
		return getPaymentIn().isAsyncReplicationAllowed();
	}
}
