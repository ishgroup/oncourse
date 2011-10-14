package ish.oncourse.enrol.listeners;

import java.util.List;

import ish.common.types.PaymentStatus;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.services.payment.IPaymentService;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.tapestry5.TapestryFilter;
import org.apache.tapestry5.ioc.Registry;

public class SessionListener implements HttpSessionListener {

	@Override
	public void sessionCreated(HttpSessionEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sessionDestroyed(HttpSessionEvent se) {

		Registry registry = (Registry) se.getSession().getServletContext()
				.getAttribute(TapestryFilter.REGISTRY_CONTEXT_NAME);

		// set payment status to fail if it's not completed
		IPaymentService paymentService = registry.getService(IPaymentService.class);

		List<PaymentIn> payments = paymentService.getPaymentsBySessionId(se.getSession().getId());

		for (PaymentIn payment : payments) {
			if (payment != null) {
				PaymentStatus status = payment.getStatus();

				if (status == PaymentStatus.CARD_DETAILS_REQUIRED || status == PaymentStatus.IN_TRANSACTION
						|| status == PaymentStatus.FAILED) {
					payment.abandonPayment();
					payment.getObjectContext().commitChanges();
				}
			}
		}

	}

}
