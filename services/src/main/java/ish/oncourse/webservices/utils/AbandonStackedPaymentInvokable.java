package ish.oncourse.webservices.utils;

import java.util.Date;

import ish.oncourse.model.PaymentIn;
import ish.oncourse.webservices.components.PaymentResult;
import ish.oncourse.webservices.pages.Payment;

import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.Invokable;

public class AbandonStackedPaymentInvokable implements Invokable<Boolean> {
	private static final Logger LOGGER = Logger.getLogger(AbandonStackedPaymentInvokable.class);
	public static final long SLEEP_TIME = 3 * 1000;//3 second
	public static final long TIMEOUT = 10 * 60 * 1000;//10 minutes
	private boolean isCanceled;
	private long invokedTime;
	private boolean isProcessed;
	private final PaymentIn payment;
	
	public AbandonStackedPaymentInvokable(long invokedTime, PaymentIn payment) {
		isCanceled = false;
		this.invokedTime = invokedTime;
		isProcessed = false;
		this.payment = payment;
	}
	
	/**
	 * @return the isProcessed
	 */
	public boolean isProcessed() {
		return isProcessed;
	}

	/**
	 * @param isCanceled the isCanceled to set
	 */
	public void setCanceled(boolean isCanceled) {
		this.isCanceled = isCanceled;
	}
	
	/**
	 * @return the isCanceled
	 */
	public boolean isCanceled() {
		return isCanceled;
	}

	/**
	 * @return the invokedTime
	 */
	public long getInvokedTime() {
		return invokedTime;
	}
	
	/**
	 * @return the timeout
	 */
	public long getTimeout() {
		return TIMEOUT;
	}

	@Override
	public Boolean invoke() {
		while (!isCanceled() && (new Date().getTime() - invokedTime <= getTimeout())) {
			try {
				LOGGER.info("Wait abandon till timeout. Seconds left = " + (getTimeout() - (new Date().getTime() - invokedTime))/1000);
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e) {
				LOGGER.error("Abandon stack payment watchdog was interrupted", e);
				isCanceled = true;
				isProcessed = true;
				return false;
			}
		}
		if (!isCanceled()) {
			//if abandon not re-scheduled yet we need to call it
			if (!PaymentResult.isSuccessPayment(payment) && !Payment.isPaymentCanceled(payment)) {
				synchronized (payment.getObjectContext()) {
					PaymentInAbandonUtil.abandonPaymentReverseInvoice(payment);
				}
			}
		}
		isProcessed = true;
		return true;
	}
}
