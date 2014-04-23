package ish.oncourse.model;

import ish.oncourse.model.auto._PaymentInLine;
import ish.oncourse.utils.QueueableObjectUtils;

import org.apache.cayenne.validation.ValidationResult;
import org.apache.log4j.Logger;

import java.util.Date;

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
	
	@Override
	protected void onPostAdd() {
		if (getCreated() == null) {
			setCreated(new Date());
		}
		if (getModified() == null) {
			setModified(getCreated());
		}
	}

	protected void onPrePersist() {
	}

	protected void onPreUpdate() {
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
		return getPaymentIn() != null && getPaymentIn().isAsyncReplicationAllowed();
	}
}
