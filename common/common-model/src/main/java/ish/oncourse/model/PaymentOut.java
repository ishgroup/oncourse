package ish.oncourse.model;

import ish.common.types.ConfirmationStatus;
import ish.common.types.PaymentSource;
import ish.common.types.PaymentStatus;
import ish.oncourse.model.auto._PaymentOut;
import ish.oncourse.utils.QueueableObjectUtils;
import org.apache.log4j.Logger;

public class PaymentOut extends _PaymentOut implements Queueable {
	private static final long serialVersionUID = -8918491908161700718L;
	private static final Logger LOG = Logger.getLogger(PaymentOut.class);

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	protected void onPrePersist() {
		
		if (getStatus() == null) {
			setStatus(PaymentStatus.IN_TRANSACTION);
		}
		
		if (getSource() == null) {
			setSource(PaymentSource.SOURCE_ONCOURSE);
		}
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
		switch (getStatus()) {
		case FAILED:
		case FAILED_CARD_DECLINED:
			break;
		default:
			setStatus(ish.common.types.PaymentStatus.FAILED);
		}
	}

	/**
	 * Retrieves the "client" identificator, ie with the "W" addon.
	 * 
	 * @return client identificator string.
	 */
	public String getClientReference() {
		return PaymentSource.SOURCE_WEB.getDatabaseValue() + getId();
	}

	@Override
	public void setStatus(final PaymentStatus status) {
		if (getStatus() == null) {
			//nothing to check
		} else {
			switch (getStatus()) {
			case NEW:
				if (status == null) {
					throw new IllegalArgumentException(String.format("Can't set the empty paymentout status with id = %s !", getId()));
				}
				break;
			case QUEUED:
				if (status == null || PaymentStatus.NEW.equals(status)) {
					throw new IllegalArgumentException(String.format("Can't set the %s status for paymentout with %s status and id = %s !", 
						status, getStatus(), getId()));
				}
				break;
			case IN_TRANSACTION:
			case CARD_DETAILS_REQUIRED:
				if (status == null || PaymentStatus.NEW.equals(status) || PaymentStatus.QUEUED.equals(status)) {
					throw new IllegalArgumentException(String.format("Can't set the %s status for paymentout with %s status and id = %s !", 
						status, getStatus(), getId()));
				}
				break;
			case SUCCESS:
				if (!PaymentStatus.SUCCESS.equals(status)) {
					throw new IllegalArgumentException(String.format("Can't set the %s status for paymentout with %s status and id = %s !", 
						status, getStatus(), getId()));
				}
				break;
			case FAILED:
			case FAILED_CARD_DECLINED:
			case FAILED_NO_PLACES:
				if (!(getStatus().equals(status))) {
					throw new IllegalArgumentException(String.format("Can't set the %s status for paymentout with %s status and id = %s !", 
						status, getStatus(), getId()));
				}
				break;
			default:
				throw new IllegalArgumentException(String.format("Unsupported status %s found for paymentout with id = %s ", status, getId()));
			}
		}
		super.setStatus(status);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}

	@Override
	protected void onPostAdd() {
		if (getConfirmationStatus() == null) {
			setConfirmationStatus(ConfirmationStatus.NOT_SENT);
		}
	}
}
