package ish.oncourse.enrol.checkout;

import ish.oncourse.enrol.checkout.payment.PaymentEditorController;
import ish.oncourse.util.payment.PaymentProcessController;
import ish.oncourse.util.payment.ProcessPaymentInvokable;
import org.apache.tapestry5.ioc.Invokable;
import org.apache.tapestry5.ioc.services.ParallelExecutor;

import java.util.concurrent.Future;

import static ish.oncourse.enrol.checkout.PurchaseController.State.editPayment;

public class ActionProceedToPayment extends APurchaseAction{

	@Override
	protected void makeAction() {
		getModel().prepareToMakePayment();
		getModel().getObjectContext().commitChanges();

		PaymentProcessController paymentProcessController = new PaymentProcessController();
		paymentProcessController.setObjectContext(getModel().getObjectContext());
		paymentProcessController.setPaymentIn(getModel().getPayment());
		paymentProcessController.setCayenneService(getController().getCayenneService());
		paymentProcessController.setPaymentGatewayService(getController().getPaymentGatewayServiceBuilder().buildService());
		paymentProcessController.setParallelExecutor(new ParallelExecutor() {
			@Override
			public <T> Future<T> invoke(Invokable<T> invocable) {
				if (invocable instanceof ProcessPaymentInvokable)
					invocable.invoke();
				return null;
			}

			@Override
			public <T> T invoke(Class<T> proxyType, Invokable<T> invocable) {
				return null;
			}
		});
		paymentProcessController.processAction(PaymentProcessController.PaymentAction.INIT_PAYMENT);
		PaymentEditorController paymentEditorController = new PaymentEditorController();
		paymentEditorController.setPaymentProcessController(paymentProcessController);
		paymentEditorController.setPurchaseController(getController());
		getController().setPaymentEditorController(paymentEditorController);
		getController().setState(editPayment);
	}

	@Override
	protected void parse() {

	}

	@Override
	protected boolean validate() {
		return true;
	}
}
