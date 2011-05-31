package ish.oncourse.model;

import ish.common.types.PaymentSource;
import ish.oncourse.model.auto._PaymentOut;

import org.apache.log4j.Logger;

public class PaymentOut extends _PaymentOut implements Queueable {

	private static final Logger LOG = Logger.getLogger(PaymentOut.class);

	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? (Long) getObjectId().getIdSnapshot().get(ID_PK_COLUMN) : null;
	}

	public boolean validateBeforeSend() {
		// TotalAmount - mandatory field
		if (this.getTotalAmount() == null) {
			LOG.warn(String.format("The payment amount cannot be empty PaymentOut angelId:%s.", this.getAngelId()));
			return false;
		}

		return true;
	}

	/**
	 * Sets the status of payment to {@link PaymentStatus#SUCCESS} Invoked when
	 * the payment gateway processing is succeed.
	 * 
	 */
	public void succeed() {
		setStatus(ish.common.types.PaymentStatus.SUCCESS);
	}

	/**
	 * Sets the status of payment to {@link PaymentStatus#FAILED} Invoked when
	 * the payment gateway processing is failed(the payment is failed because of
	 * declined card).
	 * 
	 */
	public void failed() {
		setStatus(ish.common.types.PaymentStatus.FAILED);
	}

	/**
	 * Retrieves the "client" identificator, ie with the "W" addon.
	 * 
	 * @return client identificator string.
	 */
	public String getClientReference() {
		return PaymentSource.SOURCE_WEB.getDatabaseValue() + getId();
	}
}
