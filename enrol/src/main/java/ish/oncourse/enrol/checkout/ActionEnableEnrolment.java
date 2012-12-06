package ish.oncourse.enrol.checkout;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.InvoiceLine;

import static ish.oncourse.enrol.checkout.PurchaseController.Message.*;

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
		/**
		 * If enrolment was committed so we should not check these conditions
		 */
		if (enrolment.getObjectId().isTemporary())
		{
			if (enrolment.isDuplicated()) {
				getController().getModel().setErrorFor(enrolment,
                        duplicatedEnrolment.getMessage(getController().getMessages(), enrolment.getStudent().getFullName(), enrolment.getCourseClass().getCourse().getName()));
				return false;
			}

			if (enrolment.getCourseClass().isHasAvailableEnrolmentPlaces()) {
                getController().getModel().setErrorFor(enrolment,
                        noCourseClassPlaces.getMessage(getController().getMessages(),
                                getClassName(enrolment.getCourseClass()),
                                enrolment.getCourseClass().getCourse().getCode()));
				return false;
			}
		}
		if (enrolment.getCourseClass().hasEnded()) {
            getController().getModel().setErrorFor(enrolment,
                    courseClassEnded.getMessage(getController().getMessages(),
                            getClassName(enrolment.getCourseClass()),
                            enrolment.getCourseClass().getCourse().getCode()));
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


	private String getClassName(CourseClass courseClass)
	{
		return String.format("%s (%s-%s)", courseClass.getCourse().getName(), courseClass.getCourse().getCode(), courseClass.getCode());
	}
}
