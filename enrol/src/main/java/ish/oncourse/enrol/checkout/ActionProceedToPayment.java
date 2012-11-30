package ish.oncourse.enrol.checkout;

import ish.oncourse.enrol.checkout.payment.PaymentEditorController;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.ProductItem;
import ish.oncourse.util.payment.PaymentProcessController;

import java.util.List;

import static ish.oncourse.enrol.checkout.PurchaseController.Action.*;
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
            paymentProcessController.setStartWatcher(false);
			paymentProcessController.setObjectContext(getModel().getObjectContext());
			paymentProcessController.setPaymentIn(getModel().getPayment());
			paymentProcessController.setCayenneService(getController().getCayenneService());
			paymentProcessController.setPaymentGatewayService(getController().getPaymentGatewayServiceBuilder().buildService());
			paymentProcessController.setParallelExecutor(getController().getParallelExecutor());
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
		{
			boolean result = validateEnrolments();
			result = result && validateProductItems();

			if (result)
			{
				int size = getModel().getAllEnabledEnrolments().size() + getModel().getAllEnabledEnrolments().size();
				if (size < 1)
				{
					getController().addError(PurchaseController.Error.noSelectedItemForPurchase);
					result = false;
				}
			}
			return result;
		}
	}

	private boolean validateEnrolments() {
		ActionEnableEnrolment actionEnableEnrolment = enableEnrolment.createAction(getController());
		List<Enrolment> enrolments = getController().getModel().getAllEnabledEnrolments();
		boolean result = true;
		for (Enrolment enrolment : enrolments) {
			actionEnableEnrolment.setEnrolment(enrolment);
			boolean valid = actionEnableEnrolment.validateEnrolment();
			if (!valid)
			{
				ActionDisableEnrolment actionDisableEnrolment = disableEnrolment.createAction(getController());
				actionDisableEnrolment.setEnrolment(enrolment);
				actionDisableEnrolment.action();
			}
			result = result && valid;
		}
		return result;
	}

	private boolean validateProductItems() {
		ActionEnableProductItem actionEnableProductItem = PurchaseController.Action.enableProductItem.createAction(getController());
		List<ProductItem> items = getController().getModel().getAllProductItems(getModel().getPayer());
		boolean result = true;
		for (ProductItem item : items) {
			actionEnableProductItem.setProductItem(item);
			boolean valid = actionEnableProductItem.validateProductItem();
			if (!valid)
			{
				ActionDisableProductItem actionDisableProductItem = disableProductItem.createAction(getController());
				actionDisableProductItem.setProductItem(item);
				actionDisableProductItem.action();
			}
			result = result && valid;
		}
		return result;
	}

}
