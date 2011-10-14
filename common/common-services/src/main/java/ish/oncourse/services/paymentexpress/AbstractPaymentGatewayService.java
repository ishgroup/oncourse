package ish.oncourse.services.paymentexpress;

import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentOut;

import org.apache.log4j.Logger;

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
	 * @see ish.oncourse.services.paymentexpress.IPaymentGatewayService#performGatewayOperation(ish.oncourse.model.PaymentIn)
	 */
	@Override
	public void performGatewayOperation(PaymentIn payment) {
		if (performPaymentValidation(payment)) {
			LOG.debug("Payment details validation succeed.");
			processGateway(payment);
		} else {
			LOG.debug("Payment details validation failed.");
			payment.setStatusNotes("Validation failed");
			payment.failPayment();
		}
	}


	protected abstract void initNewPaymentTransaction(PaymentIn payment); 
	
	protected abstract void initNewPaymentOutTransaction(PaymentOut paymentOut);
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see ish.oncourse.services.paymentexpress.IPaymentGatewayService#performGatewayOperation(ish.oncourse.model.PaymentOut)
	 */
	@Override
	public void performGatewayOperation(PaymentOut paymentOut) {
		// TODO Auto-generated method stub
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
	public abstract void processGateway(PaymentIn payment);
	
	
	/**
	 * Performs actual gateway process if the validation method
	 * {@link #performPaymentValidation(PaymentOut)} returned true.
	 * 
	 * @param payment
	 */
	public abstract void processGateway(PaymentOut paymentOut);
	

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
	
	/**
	 * Perform the validation before send to gateway.
	 * 
	 * @param paymentOut
	 *            the given payment to validate.
	 * @return
	 */
	public boolean performPaymentOutValidation(PaymentOut paymentOut) {
		return paymentOut.validateBeforeSend();
	}
}
