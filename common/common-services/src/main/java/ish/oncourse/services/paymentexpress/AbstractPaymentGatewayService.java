package ish.oncourse.services.paymentexpress;

import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentOut;
import ish.oncourse.model.PaymentOutTransaction;
import ish.oncourse.util.payment.PaymentInFail;
import ish.oncourse.util.payment.PaymentInModel;
import ish.oncourse.util.payment.PaymentInSucceed;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * This service performs the validation of payment before sending it to the
 * processing.
 * 
 * @author ksenia
 * 
 */
public abstract class AbstractPaymentGatewayService implements IPaymentGatewayService {

	/**
	 * Logger for service.
	 */
	protected static final Logger LOG = LogManager.getLogger();

	/**
	 * {@inheritDoc}
	 * 
	 * @see ish.oncourse.services.paymentexpress.IPaymentGatewayService#performGatewayOperation(ish.oncourse.util.payment.PaymentInModel)
	 */
	@Override
	public void performGatewayOperation(ish.oncourse.util.payment.PaymentInModel model) {
		performGatewayOperation(model, null);
	}

	@Override
	public void performGatewayOperation(PaymentInModel model, String billingId) {
		PaymentIn payment = model.getPaymentIn();
		if (payment.isZeroPayment()) {
			PaymentInSucceed.valueOf(model).perform();
		} else {
			if (performPaymentValidation(payment)) {
				LOG.debug("Payment details validation succeed.");
				processGateway(model, billingId);
			} else {
				LOG.debug("Payment details validation failed.");
				payment.setStatusNotes("Validation failed");
				PaymentInFail.valueOf(model).perform();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see ish.oncourse.services.paymentexpress.IPaymentGatewayService#performGatewayOperation(ish.oncourse.model.PaymentOut)
	 */
	@Override
	public void performGatewayOperation(PaymentOut paymentOut) {
		if (performPaymentOutValidation(paymentOut)) {
			LOG.debug("Payment details validation succeed.");
			processGateway(paymentOut);
		} else {
			LOG.debug("Payment details validation failed.");
			paymentOut.failed();
		}
	}

	/**
	 * Performs actual gateway process if the validation method
	 * {@link #performPaymentValidation(PaymentIn)} returned true.
	 * 
	 * @param payment
	 */
	protected abstract void processGateway(PaymentInModel payment, String billingId);

	/**
	 * Performs actual gateway process if the validation method
	 * {@link #performPaymentOutValidation(ish.oncourse.model.PaymentOut)} returned true.
	 * 
	 * @param paymentOut
	 */
	protected abstract void processGateway(PaymentOut paymentOut);

	/**
	 * Perform the validation before send to gateway.
	 * 
	 * @param payment
	 *            the given payment to validate.
	 * @return
	 */
	private boolean performPaymentValidation(PaymentIn payment) {
		return payment.validateBeforeSend();
	}

	/**
	 * Perform the validation before send to gateway.
	 * 
	 * @param paymentOut
	 *            the given payment to validate.
	 * @return
	 */
	private boolean performPaymentOutValidation(PaymentOut paymentOut) {

		List<PaymentOutTransaction> paymentOutTransactions = paymentOut.getPaymentOutTransactions();
		/**
		 * The check was introduced to exclude possibility to send  paymentOut to DPS twice.
		 * We did not manage to reproduce this behavior on local test system, so it is rather workaround than a fix
		 */

		 // paymentOut cannot have DPS transactions.
		if (!paymentOutTransactions.isEmpty())
		{
			String message = String.format("Attempt to process paymentOut: %d when the payment out already has dps transactions", paymentOut.getId());
			paymentOut.setStatusNotes(message);
			LOG.error(message);
			return false;
		}
		return paymentOut.validateBeforeSend();
	}
}
