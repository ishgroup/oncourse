package ish.oncourse.enrol.services.payment;

import org.apache.log4j.Logger;

import ish.oncourse.model.PaymentIn;

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
	private static final Logger LOG = Logger.getLogger(PaymentExpressGatewayService.class);
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see ish.oncourse.enrol.services.payment.IPaymentGatewayService#performGatewayOperation(ish.oncourse.model.PaymentIn)
	 */
	@Override
	public void performGatewayOperation(PaymentIn payment) {
		if (performPaymentValidation(payment)) {
			LOG.debug("Payment details validation succeed.");
			processGateway(payment);
		} else {
			LOG.debug("Payment details validation failed.");
			payment.failed();
		}
	}

	/**
	 * Performs actual gateway process if the validation method
	 * {@link #performPaymentValidation(PaymentIn)} returned true.
	 * 
	 * @param payment
	 */
	public abstract void processGateway(PaymentIn payment);

	/**
	 * Perform the validation before send to gateway.
	 * 
	 * @param payment
	 *            the given payment to validate.
	 * @return
	 */
	public boolean performPaymentValidation(PaymentIn payment) {
		return payment.validateBeforeSend();
	}

}
