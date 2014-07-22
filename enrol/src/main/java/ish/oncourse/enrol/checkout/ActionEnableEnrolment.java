package ish.oncourse.enrol.checkout;

import ish.common.types.EnrolmentStatus;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.InvoiceLine;
import org.joda.time.DateTime;
import org.joda.time.Years;

import java.util.Date;

import static ish.oncourse.enrol.checkout.PurchaseController.Message.*;

public class ActionEnableEnrolment extends APurchaseAction {

    private Enrolment enrolment;

    @Override
    protected void makeAction() {
        getModel().enableEnrolment(enrolment);
        InvoiceLine il = getController().getInvoiceProcessingService().createInvoiceLineForEnrolment(enrolment, getModel().getDiscounts());
        il.setInvoice(getModel().getInvoice());
        il.setEnrolment(getEnrolment());
        //we set status IN_TRANSACTION for enable enrolment in transaction to consider the enrolment in places check
        enrolment.setStatus(EnrolmentStatus.IN_TRANSACTION);
    }

    @Override
    protected void parse() {
        if (getParameter() != null)
            enrolment = getParameter().getValue(Enrolment.class);
    }

    @Override
    protected boolean validate() {
        return !getModel().isEnrolmentEnabled(enrolment) && validateEnrolment(false);
    }

    boolean validateEnrolment(boolean showErrors) {
        /**
         * If enrolment was committed so we should not check these conditions
         */
        if (enrolment.getObjectId().isTemporary()) {
            if (enrolment.isDuplicated()) {
				String message = duplicatedEnrolment.getMessage(getController().getMessages(), enrolment.getStudent().getFullName(), enrolment.getCourseClass().getCourse().getCode());
                getController().getModel().setErrorFor(enrolment,message);
				if (showErrors)
					getController().getErrors().put(duplicatedEnrolment.name(), message);
                return false;
            }
        }

        boolean hasPlaces = getController().hasAvailableEnrolmentPlaces(enrolment);
        if (!hasPlaces) {
			String message = noCourseClassPlaces.getMessage(getController().getMessages(),
					getController().getClassName(enrolment.getCourseClass()),
					enrolment.getCourseClass().getCourse().getCode());
            getController().getModel().setErrorFor(enrolment,message);
			if (showErrors)
				getController().getErrors().put(noCourseClassPlaces.name(), message);
            return false;
        }
        if (enrolment.getCourseClass().hasEnded()) {
			String message = courseClassEnded.getMessage(getController().getMessages(),
					getController().getClassName(enrolment.getCourseClass()),
					enrolment.getCourseClass().getCourse().getCode());
            getController().getModel().setErrorFor(enrolment, message);
			if (showErrors)
				getController().getErrors().put(courseClassEnded.name(), message);
            return false;
        }
		
		// validate age restrictions for the class
		Integer minEnrolmentAge = enrolment.getCourseClass().getMinStudentAge();
		Integer maxEnrolmentAge = enrolment.getCourseClass().getMaxStudentAge();
		
		if (minEnrolmentAge != null || maxEnrolmentAge != null) {
			Date dateOfBirth = enrolment.getStudent().getContact().getDateOfBirth();
			
			if (dateOfBirth != null) {
				Integer age = Years.yearsBetween(new DateTime(dateOfBirth.getTime()),
						new DateTime(new Date().getTime())).getYears();
				
				if ((minEnrolmentAge != null && age < minEnrolmentAge) ||
						(maxEnrolmentAge != null && age > maxEnrolmentAge)) {
					String message = ageRequirementsNotMet.getMessage(getController().getMessages(), enrolment.getStudent().getFullName());
					getController().getModel().setErrorFor(enrolment, message);
					if (showErrors)
						getController().getErrors().put(ageRequirementsNotMet.name(), message);
					return false;
				}
			}
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
