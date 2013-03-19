package ish.oncourse.enrol.checkout;

import ish.oncourse.model.*;

import java.util.List;

import static ish.oncourse.enrol.checkout.PurchaseController.Message.illegalState;

public class ActionBackToEditCheckout extends APurchaseAction {

	private PurchaseController.Action action;

	@Override
	protected void makeAction() {
        getController().setPaymentEditorController(null);
		getController().setState(PurchaseController.State.editCheckout);
        getController().getModel().setApplyPrevOwing(false);

		List<Contact> contacts = getModel().getContacts();
		List<CourseClass> classes = getModel().getClasses();
		List<Product> products = getModel().getProducts();

        /**
         * create disabled enrolments which were deleted after an user pressed ProceedToPayment
         */
		for (Contact contact : contacts) {
			for (CourseClass courseClass : classes) {
				Enrolment enrolment = getModel().getEnrolmentBy(contact, courseClass);
				if (enrolment == null)
				{
					enrolment = getController().createEnrolment(courseClass, contact.getStudent());
					getModel().addEnrolment(enrolment);
				}
			}
		}

        /**
         * create disabled productItems which were deleted after an user pressed ProceedToPayment
         */
        for (Product product : products) {
            ProductItem productItem = getModel().getProductItemBy(getModel().getPayer(), product);
            if (productItem == null)
            {
                productItem = getController().createProductItem(getModel().getPayer(), product);
                getModel().addProductItem(productItem);
            }
		}
	}

	@Override
	protected void parse() {
		if (getParameter() != null)
			action = getParameter().getValue(PurchaseController.Action.class);
	}

	@Override
	protected boolean validate() {

		if ((getController().isEditPayment() ||
				getController().isEditCorporatePass())
				&& PurchaseController.COMMON_ACTIONS.contains(action)) {
			return true;
		}
		getController().addError(illegalState);
		return false;
	}
}
