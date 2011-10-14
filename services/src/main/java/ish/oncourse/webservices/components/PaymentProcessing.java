package ish.oncourse.webservices.components;

import ish.common.types.PaymentStatus;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.services.paymentexpress.IPaymentGatewayService;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Session;

public class PaymentProcessing {

	private static final long POLL_INTERVAL = 1000l * 10;
	
	@Inject
	private Block paymentResultBlock;

	@Property
	@Parameter
	private PaymentIn payment;

	@Inject
	private Request request;

	@Inject
	private IPaymentGatewayService paymentGatewayService;

	@OnEvent(component = "processHolder", value = "progressiveDisplay")
	Object checkPaymentTask() throws Exception {
		
		Session session = request.getSession(true);

		if (!Boolean.TRUE.equals(session.getAttribute(PaymentIn.PAYMENT_PROCESSED_PARAM))) {
			session.setAttribute(PaymentIn.PAYMENT_PROCESSED_PARAM, Boolean.TRUE);

			if (payment.getStatus() == PaymentStatus.CARD_DETAILS_REQUIRED || payment.getStatus() == PaymentStatus.IN_TRANSACTION) {
				paymentGatewayService.performGatewayOperation(payment);

				if (payment.getStatus() == PaymentStatus.SUCCESS) {
					payment.getObjectContext().commitChanges();
				}
			}

			session.setAttribute(PaymentIn.PAYMENT_PROCESSED_PARAM, null);
		} else {
			while (Boolean.TRUE.equals(session.getAttribute(PaymentIn.PAYMENT_PROCESSED_PARAM))) {
				Thread.sleep(POLL_INTERVAL);
			}
		}
		
		return paymentResultBlock;
	}
}
