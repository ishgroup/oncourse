package ish.oncourse.util.payment;

import ish.oncourse.model.PaymentIn;
import ish.oncourse.services.payment.IPaymentService;
import ish.oncourse.services.paymentexpress.IPaymentGatewayService;
import ish.oncourse.services.paymentexpress.IPaymentGatewayServiceBuilder;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.util.payment.PaymentProcessController.PaymentAction;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.ParallelExecutor;

public class PaymentProcessControllerBuilder implements IPaymentProcessControllerBuilder{
	@Inject
    private ParallelExecutor parallelExecutor;
	@Inject
    private IPaymentGatewayServiceBuilder paymentGatewayServiceBuilder;
	@Inject
	private ICayenneService cayenneService;
	@Inject
    private IPaymentService paymentService;
	
	@Override
	public PaymentProcessController build(final String sessionId) {
		final PaymentIn paymentIn = takePaymentService().currentPaymentInBySessionId(sessionId);
        if (paymentIn == null) {
        	return null;
        }
		PaymentProcessController controller = new PaymentProcessController();
		controller.setObjectContext(takeCayenneService().newContext());
		controller.setParallelExecutor(takeParallelExecutor());
		controller.setPaymentGatewayService(receivePaymentGatewayService());
		controller.setCayenneService(takeCayenneService());
		controller.setPaymentIn(paymentIn);
		controller.processAction(PaymentAction.INIT_PAYMENT);
		return controller;
	}
	
	@Override
	public boolean isNeedResetOldSessionController(PaymentProcessController paymentProcessController, String sessionId) {
		return paymentProcessController != null && isControllerOldAndExpired(paymentProcessController, sessionId);
	}
	
	static boolean isControllerOldAndExpired(PaymentProcessController paymentProcessController, String sessionId) {
    	return sessionId != null && paymentProcessController.isExpired() && paymentProcessController.getPaymentIn() != null 
    		&& !sessionId.equals(paymentProcessController.getPaymentIn().getSessionId());
    }

	ParallelExecutor takeParallelExecutor() {
		return parallelExecutor;
	}
	
	IPaymentGatewayService receivePaymentGatewayService() {
		return paymentGatewayServiceBuilder.buildService();
	}

	ICayenneService takeCayenneService() {
		return cayenneService;
	}

	IPaymentService takePaymentService() {
		return paymentService;
	}
	
	

}
