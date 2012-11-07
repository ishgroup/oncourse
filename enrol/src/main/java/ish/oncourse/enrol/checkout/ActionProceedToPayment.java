package ish.oncourse.enrol.checkout;

import ish.oncourse.enrol.checkout.payment.PaymentEditorController;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.util.payment.PaymentProcessController;
import ish.oncourse.util.payment.ProcessPaymentInvokable;
import org.apache.tapestry5.ioc.Invokable;
import org.apache.tapestry5.ioc.services.ParallelExecutor;

import java.util.concurrent.Future;

import static ish.oncourse.enrol.checkout.PurchaseController.State.editPayment;

public class ActionProceedToPayment extends APurchaseAction {
	private PaymentIn paymentIn;

	@Override
	protected void makeAction() {
		//the first time proceed
		if (paymentIn == getModel().getPayment()) {
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
		} else {
			getModel().setPayment(paymentIn);
		}
		getController().setState(editPayment);
	}

	@Override
	protected void parse() {
		paymentIn = getParameter().getValue(PaymentIn.class);
	}

	@Override
	protected boolean validate() {

		PaymentEditorController paymentEditorController = (PaymentEditorController) getController().getPaymentEditorDelegate();
		if (paymentEditorController != null)
		{
			return !(paymentEditorController.getPaymentProcessController().isIllegalState() ||
					paymentEditorController.getPaymentProcessController().isExpired() ||
					paymentEditorController.getPaymentProcessController().geThrowable() != null);
		}
		else
			return true;
	}
}
