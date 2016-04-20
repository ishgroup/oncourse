package ish.oncourse.enrol.checkout;

import ish.common.types.CourseEnrolmentType;
import ish.oncourse.model.*;

import java.util.List;

import static ish.oncourse.enrol.checkout.PurchaseController.Action.disableEnrolment;
import static ish.oncourse.enrol.checkout.PurchaseController.Action.enableEnrolment;
import static ish.oncourse.enrol.checkout.PurchaseController.Message.illegalState;

public class ActionBackToEditCheckout extends APurchaseAction {

	private PurchaseController.Action action;

	@Override
	protected void makeAction() {
		ResetCorporatePass.valueOf(getController()).reset();

        getController().setPaymentEditorController(null);
		getController().setState(PurchaseController.State.editCheckout);
        getController().getModel().setApplyPrevOwing(false);

		/**
		 * clear credit card information
		 */
		getController().getModel().getPayment().setCreditCardCVV(null);
		getController().getModel().getPayment().setCreditCardName(null);
		getController().getModel().getPayment().setCreditCardNumber(null);
		getController().getModel().getPayment().setCreditCardExpiry(null);

		List<Contact> contacts = getModel().getContacts();
		List<CourseClass> classes = getModel().getClasses();
		List<Product> products = getModel().getProducts();

        /**
         * create disabled enrolments or applications which were deleted after an user pressed ProceedToPayment
         */
		for (Contact contact : contacts) {
			// is not available for companies 
			if (!contact.getIsCompany()) {
				for (CourseClass courseClass : classes) {

					if (CourseEnrolmentType.ENROLMENT_BY_APPLICATION.equals(courseClass.getCourse().getEnrolmentType()) &&
							getController().getApplicationService().findOfferedApplicationBy(courseClass.getCourse(),contact.getStudent()) == null) {
						Application application = getModel().getApplicationBy(contact, courseClass.getCourse());
						if (application == null) {
							application = getController().createApplication(contact.getStudent(), courseClass.getCourse());
							getModel().addApplication(application);
						}
					} else {
						Enrolment enrolment = getModel().getEnrolmentBy(contact, courseClass);
						if (enrolment == null) {
							enrolment = getModel().createEnrolment(courseClass, contact.getStudent());
							getModel().addEnrolment(enrolment);
						} else {
							ActionDisableEnrolment disableEnrolmentAction = disableEnrolment.createAction(getController());
							disableEnrolmentAction.setEnrolment(enrolment);
							getController().performAction(disableEnrolmentAction, disableEnrolment);

							enrolment = disableEnrolmentAction.getEnrolment();
							ActionEnableEnrolment enableEnrolmentAction = enableEnrolment.createAction(getController());
							enableEnrolmentAction.setEnrolment(enrolment);
							getController().performAction(enableEnrolmentAction, enableEnrolment);
						}
					}
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
