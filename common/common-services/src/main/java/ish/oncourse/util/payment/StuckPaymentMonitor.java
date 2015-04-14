package ish.oncourse.util.payment;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.ioc.Invokable;

public class StuckPaymentMonitor implements Invokable<Boolean> {
    private static final Logger logger = LogManager.getLogger();
    public static final long TIMEOUT = 10 * 60 * 1000;//10 minutes

    private final PaymentProcessController paymentProcessController;

    public StuckPaymentMonitor(PaymentProcessController paymentProcessController) {
        this.paymentProcessController = paymentProcessController;
    }

    /**
     * @return TRUE when needs abandon
     */
    @Override
    public Boolean invoke() {
        try {
            logger.info("Wait for next state...");
            Thread.sleep(TIMEOUT);
            synchronized (paymentProcessController)
            {
                paymentProcessController.processAction(PaymentProcessController.PaymentAction.EXPIRE_PAYMENT);
            }
            return Boolean.TRUE;
        } catch (InterruptedException e) {
            logger.info("StackedPaymentMonitor watchdog was interrupted", e);
            return Boolean.FALSE;
        }
    }
}
