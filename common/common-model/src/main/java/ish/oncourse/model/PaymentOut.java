package ish.oncourse.model;

import ish.common.types.PaymentSource;
import ish.common.types.PaymentStatus;
import ish.oncourse.model.auto._PaymentOut;
import ish.oncourse.utils.QueueableObjectUtils;

import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
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

	/**
	 * Gets currently active transaction of paymentOut object.
	 * 
	 * @return payment out transaction
	 */
	public PaymentOutTransaction getActiveTransaction() {
		List<PaymentOutTransaction> transactions = getPaymentOutTransactions();
		Expression finalisedExpr = ExpressionFactory.matchExp(PaymentOutTransaction.IS_FINALISED_PROPERTY, false);
		finalisedExpr = finalisedExpr.orExp(ExpressionFactory.matchExp(PaymentOutTransaction.IS_FINALISED_PROPERTY, null));
		List<PaymentOutTransaction> activeTransactions = finalisedExpr.filterObjects(transactions);
		return (activeTransactions.isEmpty()) ? null : activeTransactions.get(0);
	}
}
