package ish.oncourse.enrol.checkout;

import ish.oncourse.model.Enrolment;
import ish.oncourse.model.InvoiceLine;

import static ish.oncourse.enrol.checkout.PurchaseController.Error.*;

public class ActionEnableEnrolment extends APurchaseAction {

	private Enrolment enrolment;

	@Override
	protected void makeAction() {
		getModel().enableEnrolment(enrolment);
		InvoiceLine il = getController().getInvoiceProcessingService().createInvoiceLineForEnrolment(enrolment, getModel().getDiscounts());
		il.setInvoice(getModel().getInvoice());
		enrolment.setInvoiceLine(il);
	}

	@Override
	protected void parse() {
		if (getParameter() != null)
			enrolment = getParameter().getValue(Enrolment.class);
	}

	@Override
	protected boolean validate() {
		return !getModel().isEnrolmentEnabled(enrolment) && validateEnrolment();
	}

	boolean validateEnrolment()
	{
		if (enrolment.isDuplicated()) {
			getController().addError(duplicatedEnrolment, enrolment.getStudent().getFullName(), enrolment.getCourseClass().getCourse().getName());
			return false;
		}

		if (!enrolment.getCourseClass().isHasAvailableEnrolmentPlaces()) {
			getController().addError(noCourseClassPlaces, enrolment);
			return false;
		}

		if (enrolment.getCourseClass().hasEnded()) {
			getController().addError(courseClassEnded, enrolment.getCourseClass());
			return false;
		}
		return true;
	}

	public Enrolment getEnrolment() {
		return enrolment;
	}

	public void setEnrolment(Enrolment enrolment) {
		this.enrolment = enrolment;
	}
}
