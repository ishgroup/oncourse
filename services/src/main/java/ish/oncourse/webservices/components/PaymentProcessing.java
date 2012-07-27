package ish.oncourse.webservices.components;

import ish.oncourse.model.PaymentIn;
import ish.oncourse.webservices.pages.Payment;
import org.apache.log4j.Logger;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

public class PaymentProcessing {
	private static final Logger LOGGER = Logger.getLogger(PaymentProcessing.class);
	private static final long POLL_INTERVAL = 1000l * 10;

	@Inject
	private Block paymentResultBlock;

	@SuppressWarnings("all")
	@Property
	@Parameter
	private PaymentIn payment;
	
	@InjectPage
    private Payment paymentPage;
	
	@InjectComponent
    private PaymentResult result;

	@OnEvent(component = "processHolder", value = "progressiveDisplay")
	Object checkPaymentTask() throws Exception {
		if (!paymentPage.isCheckoutResult()) {
			try {
				paymentPage.setCheckoutResult(true);
				paymentPage.processPayment();
			} catch (Exception e) {
				LOGGER.warn("Unexpected Exception", e);
				result.setUnexpectedException(e);
			} finally {
				paymentPage.setCheckoutResult(false);
			}
		} else {
			while (paymentPage.isCheckoutResult()) {
				Thread.sleep(POLL_INTERVAL);
			}
		}
		return result;
	}

	/**
	 * @return the paymentResultBlock
	 */
	public Block takePaymentResultBlock() {
		return paymentResultBlock;
	}
	
	
}
