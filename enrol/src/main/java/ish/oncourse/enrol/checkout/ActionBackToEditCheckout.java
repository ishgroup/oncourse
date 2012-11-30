package ish.oncourse.enrol.checkout;

import ish.oncourse.model.Contact;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Product;

import java.util.List;

import static ish.oncourse.enrol.checkout.PurchaseController.Error.illegalState;

public class ActionBackToEditCheckout extends APurchaseAction {

	private PurchaseController.Action action;

	@Override
	protected void makeAction() {
		getController().setState(PurchaseController.State.editCheckout);

		List<Contact> contacts = getModel().getContacts();
		List<CourseClass> classes = getModel().getClasses();
		List<Product> products = getModel().getProducts();
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

		for (Product product : products) {

		}
	}

	@Override
	protected void parse() {
		if (getParameter() != null)
			action = getParameter().getValue(PurchaseController.Action.class);
	}

	@Override
	protected boolean validate() {

		if (getController().getState() == PurchaseController.State.editPayment
				&& PurchaseController.COMMON_ACTIONS.contains(action)) {
			getController().setState(PurchaseController.State.editCheckout);
			return true;
		}
		getController().addError(illegalState);
		return false;
	}
}
