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
		for (Contact contact : contacts) {
			for (Product product : products) {
				ProductItem productItem = getModel().getProductItemBy(contact, product);
				//vouchers can be added only for payer
				if (productItem == null && (!(product instanceof VoucherProduct) || contact.equals(getModel().getPayer())))
				{
					productItem = getController().createProductItem(contact, product);
					getModel().addProductItem(productItem);
				}
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
