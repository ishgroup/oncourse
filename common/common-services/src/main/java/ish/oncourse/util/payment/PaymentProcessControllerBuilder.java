package ish.oncourse.util.payment;

import ish.oncourse.model.College;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.services.payment.IPaymentService;
import ish.oncourse.services.paymentexpress.DisabledPaymentGatewayService;
import ish.oncourse.services.paymentexpress.IPaymentGatewayService;
import ish.oncourse.services.paymentexpress.IPaymentGatewayServiceBuilder;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.util.payment.PaymentProcessController.PaymentAction;
import org.apache.tapestry5.ioc.services.ParallelExecutor;
import org.apache.tapestry5.services.Session;

public class PaymentProcessControllerBuilder {
    private ParallelExecutor parallelExecutor;
    private IPaymentGatewayServiceBuilder paymentGatewayServiceBuilder;
	private ICayenneService cayenneService;
    private IPaymentService paymentService;
    private Session session;
	
	public PaymentProcessControllerBuilder(ParallelExecutor parallelExecutor, IPaymentGatewayServiceBuilder paymentGatewayServiceBuilder,
			ICayenneService cayenneService, IPaymentService paymentService, Session session) {
		this.parallelExecutor = parallelExecutor;
		this.paymentGatewayServiceBuilder = paymentGatewayServiceBuilder;
		this.cayenneService = cayenneService;
		this.paymentService = paymentService;
		this.session = session;
	}

	public PaymentProcessController build(final String sessionId) {
		final PaymentIn paymentIn = paymentService.currentPaymentInBySessionId(sessionId);
        if (paymentIn == null) {
        	return null;
        }
		PaymentProcessController controller = new PaymentProcessController();
		if (session == null) {
			throw new IllegalArgumentException("PaymentProcessControllerBuilder can't build the PaymentProcessController without valid session!");
		}
		//need to setup the session value for WebSiteService for correct execution of PaymentGatewayService
        session.setAttribute(College.REQUESTING_COLLEGE_ATTRIBUTE, paymentIn.getCollege().getId());
		controller.setObjectContext(cayenneService.newContext());
		controller.setParallelExecutor(parallelExecutor);
		IPaymentGatewayService paymentGatewayService = receivePaymentGatewayService();
		if (paymentGatewayService instanceof DisabledPaymentGatewayService) {
			throw new IllegalStateException("Unable to process payments for this college.");
		}
		controller.setPaymentGatewayService(paymentGatewayService);
		controller.setCayenneService(cayenneService);
		controller.setPaymentService(paymentService);
		
		controller.setPaymentIn(paymentIn);
		controller.processAction(PaymentAction.INIT_PAYMENT);
		return controller;
	}
	
	public static boolean isNeedResetOldSessionController(PaymentProcessController paymentProcessController, String sessionId) {
		return paymentProcessController != null && isControllerOldAndExpired(paymentProcessController, sessionId);
	}
	
	static boolean isControllerOldAndExpired(PaymentProcessController paymentProcessController, String sessionId) {
    	return sessionId != null && paymentProcessController.isExpired() && paymentProcessController.getPaymentIn() != null 
    		&& !sessionId.equals(paymentProcessController.getPaymentIn().getSessionId());
    }
	
	IPaymentGatewayService receivePaymentGatewayService() {
		return paymentGatewayServiceBuilder.buildService();
	}

}
