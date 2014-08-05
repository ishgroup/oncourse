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
                publishError(duplicatedEnrolment, showErrors, enrolment.getStudent().getFullName(), enrolment.getCourseClass().getCourse().getCode());
                return false;
            }
        }

        boolean hasPlaces = getController().hasAvailableEnrolmentPlaces(enrolment);
        if (!hasPlaces) {

            publishError(noCourseClassPlaces, showErrors, getController().getClassName(enrolment.getCourseClass()),
                    enrolment.getCourseClass().getCourse().getCode());
            return false;
        }
        if (enrolment.getCourseClass().hasEnded()) {
            publishError(courseClassEnded, showErrors, getController().getClassName(enrolment.getCourseClass()),
                    enrolment.getCourseClass().getCourse().getCode());
            return false;
        }

        return validateDateOfBirth(showErrors);

    }

    private boolean validateDateOfBirth(boolean showErrors) {
        Date dateOfBirth = enrolment.getStudent().getContact().getDateOfBirth();
        if (dateOfBirth != null)
        {
            Integer minEnrolmentAge = enrolment.getCourseClass().getMinStudentAge();
            Integer maxEnrolmentAge = enrolment.getCourseClass().getMaxStudentAge();

            Integer globalMinEnrolmentAge = getController().getPreferenceController().getEnrolmentMinAge();

            Integer age = Years.yearsBetween(new DateTime(dateOfBirth.getTime()),
                    new DateTime(new Date().getTime())).getYears();

            // validate age restrictions for the class
            if (minEnrolmentAge != null || maxEnrolmentAge != null) {

                if ((minEnrolmentAge != null && age < minEnrolmentAge) ||
                        (maxEnrolmentAge != null && age > maxEnrolmentAge)) {
                    publishError(ageRequirementsNotMet, showErrors, enrolment.getStudent().getFullName());
                    return false;
                }
            //validate age restrictions for global enrolment age restriction
            } else if (globalMinEnrolmentAge != null && age < globalMinEnrolmentAge) {
                publishError(ageRequirementsNotMet, showErrors, enrolment.getStudent().getFullName());
                return false;
            }
        }
        return true;
    }

    private void publishError(PurchaseController.Message messageKey,boolean showErrors, Object... params) {
        String message = messageKey.getMessage(getController().getMessages(), params);
        getController().getModel().setErrorFor(enrolment, message);
        if (showErrors)
            getController().getErrors().put(messageKey.name(), message);
    }

    public Enrolment getEnrolment() {
        return enrolment;
    }

    public void setEnrolment(Enrolment enrolment) {
        this.enrolment = enrolment;
    }
}
