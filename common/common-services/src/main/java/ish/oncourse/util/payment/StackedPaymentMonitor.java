package ish.oncourse.util.payment;

import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.Invokable;

public class StackedPaymentMonitor implements Invokable<Boolean> {
    private static final Logger LOGGER = Logger.getLogger(StackedPaymentMonitor.class);
    public static final long TIMEOUT = 10 * 60 * 1000;//10 minutes

    private final PaymentProcessController paymentProcessController;

    public StackedPaymentMonitor(PaymentProcessController paymentProcessController) {
        this.paymentProcessController = paymentProcessController;
    }

    /**
     * @return TRUE when needs abandon
     */
    @Override
    public Boolean invoke() {
        try {
            LOGGER.info("Wait for next state ....");
            Thread.sleep(TIMEOUT);
            synchronized (paymentProcessController)
            {
                paymentProcessController.processAction(PaymentProcessController.PaymentAction.EXPIRE_PAYMENT);
            }
            return Boolean.TRUE;
        } catch (InterruptedException e) {
            LOGGER.info("StackedPaymentMonitor watchdog was interrupted", e);
            return Boolean.FALSE;
        }
    }
}
