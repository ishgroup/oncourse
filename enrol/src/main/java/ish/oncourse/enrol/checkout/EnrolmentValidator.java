package ish.oncourse.enrol.checkout;

import ish.common.types.CourseEnrolmentType;
import ish.common.types.EnrolmentStatus;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.joda.time.DateTime;
import org.joda.time.Years;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static ish.oncourse.enrol.checkout.PurchaseController.Message.*;

public class EnrolmentValidator {

    private PurchaseController purchaseController;
    private Enrolment enrolment;
    private CourseClass courseClass;
    private boolean showErrors;

    public boolean validate() {
        courseClass = purchaseController.reloadObject(enrolment.getCourseClass());

        if (courseClass.isCancelled()) {
            publishError(courseClassCancelled, showErrors, purchaseController.getClassName(enrolment.getCourseClass()));
            return false;
        }

        if (!courseClass.getIsWebVisible() || !courseClass.getIsActive()) {
            publishError(courseClassIsNotVisible, showErrors, purchaseController.getClassName(enrolment.getCourseClass()));
            return false;
        }

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
		
		if (CourseEnrolmentType.ENROLMENT_BY_APPLICATION.equals(enrolment.getCourseClass().getCourse().getEnrolmentType())) {
			
			// Check that application for course-student is not uses in other enrolment process
			Expression inTransactionEnrolments = ExpressionFactory.matchExp(Enrolment.STATUS_PROPERTY, EnrolmentStatus.IN_TRANSACTION)
					.andExp(ExpressionFactory.matchExp(Enrolment.COURSE_CLASS_PROPERTY + "." + CourseClass.COURSE_PROPERTY, enrolment.getCourseClass().getCourse()))
					.andExp(ExpressionFactory.matchExp(Enrolment.STUDENT_PROPERTY, enrolment.getStudent()));
			
			List<Enrolment> enrolments = getPurchaseController().getCayenneService().sharedContext().performQuery(new SelectQuery(Enrolment.class, inTransactionEnrolments));
			
			if (!enrolments.isEmpty()) {
				publishError(applicationAlreadyInTransaction, showErrors, purchaseController.getClassName(enrolment.getCourseClass()));
				return false;
			}
			
			// Prevent enrol student on multiple classes which has the same course with type ENROLMENT_BY_APPLICATION (in the single enrol process). 
			// In other words prevent use a single offered application for more then one enrolment. 
			// Show validation error in this case
			List<Enrolment> enabledEnrolments = new ArrayList<>(purchaseController.getModel().getEnabledEnrolments(enrolment.getStudent().getContact()));
			enabledEnrolments.remove(this.enrolment);
			for (Enrolment enrolment : enabledEnrolments) {
				if (enrolment.getCourseClass().getCourse().getId().equals(this.enrolment.getCourseClass().getCourse().getId())
						&& enrolment.getStudent().getId().equals(this.enrolment.getStudent().getId())) {
					publishError(applicationAlreadyApplyed, showErrors, purchaseController.getClassName(enrolment.getCourseClass()));
					return false;
				}
			}
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

                if ((minEnrolmentAge != null && age < minEnrolmentAge)) {
					publishError(ageRequirementsUnderMinimum, showErrors, minEnrolmentAge, enrolment.getStudent().getFullName());
					return false;
                } else if ((maxEnrolmentAge != null && age > maxEnrolmentAge)) {
					publishError(ageRequirementsOverMaximum, showErrors, maxEnrolmentAge, enrolment.getStudent().getFullName());
					return false;
				}
                //validate age restrictions for global enrolment age restriction
            } else if (globalMinEnrolmentAge != null && age < globalMinEnrolmentAge) {
                publishError(ageRequirementsUnderMinimum, showErrors, globalMinEnrolmentAge, enrolment.getStudent().getFullName());
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
