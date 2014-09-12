package ish.oncourse.enrol.checkout;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import org.joda.time.DateTime;
import org.joda.time.Years;

import java.util.Date;

import static ish.oncourse.enrol.checkout.PurchaseController.Message.*;

public class EnrolmentValidator {

    private PurchaseController purchaseController;
    private Enrolment enrolment;
    private CourseClass courseClass;
    private boolean showErrors;

    public boolean validate() {
        courseClass = purchaseController.reloadObject(enrolment.getCourseClass());

        /**
         * If enrolment was committed so we should not check these conditions
         */
        if (enrolment.getObjectId().isTemporary()) {
            if (enrolment.isDuplicated()) {
                publishError(duplicatedEnrolment, showErrors, enrolment.getStudent().getFullName(), enrolment.getCourseClass().getCourse().getCode());
                return false;
            }
        }

        boolean hasPlaces = purchaseController.hasAvailableEnrolmentPlaces(enrolment);
        if (!hasPlaces) {

            publishError(noCourseClassPlaces, showErrors, purchaseController.getClassName(enrolment.getCourseClass()),
                    enrolment.getCourseClass().getCourse().getCode());
            return false;
        }
        if (enrolment.getCourseClass().hasEnded()) {
            publishError(courseClassEnded, showErrors, purchaseController.getClassName(enrolment.getCourseClass()),
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

            Integer globalMinEnrolmentAge = purchaseController.getPreferenceController().getEnrolmentMinAge();

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
        String message = messageKey.getMessage(purchaseController.getMessages(), params);
        purchaseController.getModel().setErrorFor(enrolment, message);
        if (showErrors)
            purchaseController.getErrors().put(messageKey.name(), message);
    }

    public PurchaseController getPurchaseController() {
        return purchaseController;
    }

    public void setPurchaseController(PurchaseController purchaseController) {
        this.purchaseController = purchaseController;
    }

    public Enrolment getEnrolment() {
        return enrolment;
    }

    public void setEnrolment(Enrolment enrolment) {
        this.enrolment = enrolment;
    }

    public CourseClass getCourseClass() {
        return courseClass;
    }

    public boolean isShowErrors() {
        return showErrors;
    }

    public void setShowErrors(boolean showErrors) {
        this.showErrors = showErrors;
    }

    public static EnrolmentValidator valueOf(Enrolment enrolment, boolean showErrors, PurchaseController purchaseController)
    {
        EnrolmentValidator validator = new EnrolmentValidator();
        validator.setEnrolment(enrolment);
        validator.setShowErrors(showErrors);
        validator.setPurchaseController(purchaseController);
        return validator;
    }

}
