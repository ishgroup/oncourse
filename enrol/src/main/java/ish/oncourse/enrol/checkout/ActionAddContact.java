package ish.oncourse.enrol.checkout;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Product;

import java.util.List;

public class ActionAddContact extends AAddContactAction {

    @Override
    protected boolean shouldChangePayer() {
        return getModel().getPayer() == null;
    }

    @Override
    protected boolean shouldAddEnrolments() {
        return true;
    }

    @Override
    protected boolean isApplyOwing() {
        return false;
    }

    @Override
    protected PurchaseController.State getFinalState() {
        return PurchaseController.State.editCheckout;
    }

    @Override
    protected PurchaseController.Action getAddAction() {
        return PurchaseController.Action.addContact;
    }

    @Override
    protected PurchaseController.Action getCancelAction() {
        return PurchaseController.Action.cancelAddContact;
    }

    @Override
    protected String getHeaderMessage() {

		List<Product> products = getModel().getProducts();
		List<CourseClass> classes = getModel().getClasses();

		if (products.size() > 0 && classes.size() > 0) {
			return getController().getMessages().format("message-enterDetailsForStudent");
		} else if (products.size() > 0) {
			return getController().getMessages().format("message-enterDetailsForPersonMakingPurchase");
		} else {
			return getController().getMessages().format("message-enterDetailsForPersonEnrolling");
		}
    }

    @Override
    protected String getHeaderTitle() {
        return getController().getMessages().format("message-addStudent");
    }
}
