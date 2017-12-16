package ish.oncourse.util.payment;

import ish.oncourse.model.College;
import ish.oncourse.services.payment.IPaymentService;
import ish.oncourse.services.paymentexpress.INewPaymentGatewayService;
import ish.oncourse.services.paymentexpress.INewPaymentGatewayServiceBuilder;
import ish.oncourse.services.paymentexpress.NewDisabledPaymentGatewayService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.util.payment.PaymentProcessController.PaymentAction;
import org.apache.tapestry5.ioc.services.ParallelExecutor;
import org.apache.tapestry5.services.Session;

public class PaymentProcessControllerBuilder {
    private ParallelExecutor parallelExecutor;
    private INewPaymentGatewayServiceBuilder paymentGatewayServiceBuilder;
	private ICayenneService cayenneService;
    private IPaymentService paymentService;
    private Session session;
	
	public PaymentProcessControllerBuilder(ParallelExecutor parallelExecutor, INewPaymentGatewayServiceBuilder paymentGatewayServiceBuilder,
			ICayenneService cayenneService, IPaymentService paymentService, Session session) {
		this.parallelExecutor = parallelExecutor;
		this.paymentGatewayServiceBuilder = paymentGatewayServiceBuilder;
		this.cayenneService = cayenneService;
		this.paymentService = paymentService;
		this.session = session;
	}

	public PaymentProcessController build(PaymentInModel model) {
		if (session == null) {
			throw new IllegalArgumentException("PaymentProcessControllerBuilder can't build the PaymentProcessController without valid session!");
		}
		//need to setup the session value for WebSiteService for correct execution of PaymentGatewayService
        session.setAttribute(College.REQUESTING_COLLEGE_ATTRIBUTE, model.getPaymentIn().getCollege().getId());

		INewPaymentGatewayService paymentGatewayService = receivePaymentGatewayService();
		if (paymentGatewayService instanceof NewDisabledPaymentGatewayService) {
			throw new IllegalStateException("Unable to process payments for this college.");
		}

		PaymentProcessController controller = PaymentProcessController.valueOf(parallelExecutor,
				paymentGatewayService,
				cayenneService,
				paymentService,
				model);

		controller.processAction(PaymentAction.INIT_PAYMENT);
		return controller;
	}
	
	static boolean isControllerOldAndExpired(PaymentProcessController paymentProcessController, String sessionId) {
    	return sessionId != null && paymentProcessController.isExpired() && paymentProcessController.getPaymentIn() != null 
    		&& !sessionId.equals(paymentProcessController.getPaymentIn().getSessionId());
    }
	
	INewPaymentGatewayService receivePaymentGatewayService() {
		return paymentGatewayServiceBuilder.buildService();
	}

}
