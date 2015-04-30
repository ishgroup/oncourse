package ish.oncourse.webservices.replication.services;

import ish.common.types.PaymentType;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.preference.PreferenceControllerFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class PaymentInModelValidator {
    private static final Logger logger = LogManager.getLogger();
    private PreferenceController prefsController;

    private PaymentInModel model;


    private String errorMessage;
    private Error error;


    public PaymentInModelValidator validate() {
        if (model.getPaymentIn().getType() == PaymentType.CREDIT_CARD && !prefsController.getLicenseCCProcessing()) {
            error = Error.noLicenseCCProcessing;
            errorMessage = error.getMessage();
            logger.info("{} collegeId: {}", errorMessage, model.getPaymentIn().getCollege().getId());
        } else {
            validateEnrolments();
        }
        return this;
    }

    private void validateEnrolments() {
        for (Enrolment enrolment : model.getEnrolments()) {
            CourseClass clazz = enrolment.getCourseClass();
            List<Enrolment> validEnrolments = clazz.getValidEnrolments();
            int availPlaces = clazz.getMaximumPlaces() - validEnrolments.size();
            if (availPlaces < 0) {
                error = Error.noPlacesAvailable;
                errorMessage = String.format(error.getMessage(), clazz.getUniqueIdentifier());
                logger.info("{} courseClassId: {}", errorMessage, clazz.getId());
                break;
            }

            //check that no duplicated enrolments exist
            List<Enrolment> studentEnrolments = Enrolment.STUDENT.eq(enrolment.getStudent()).filterObjects(validEnrolments);
            //we use this flag also to say about duplicated student enrolments
            //see 18618
            if (studentEnrolments.size() > 1) {
                error = Error.activeEnrolmentExists;
                errorMessage = String.format(error.getMessage(), enrolment.getStudent().getContact().getFullName(), clazz.getUniqueIdentifier());
                logger.info("{} courseClassId: {}, contactId: {}", errorMessage, clazz.getId(), enrolment.getStudent().getContact().getId());
                break;
            }
        }
    }

    public static PaymentInModelValidator valueOf(PaymentInModel model, PreferenceControllerFactory prefsFactory) {
        PaymentInModelValidator validator = new PaymentInModelValidator();
        validator.prefsController = prefsFactory.getPreferenceController(model.getPaymentIn().getCollege());
        validator.model = model;
        return validator;
    }

    public Error getError() {
        return error;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public enum Error {
        activeEnrolmentExists("Student %s already has an enrollment in transaction for class %s and he/she cannot be enrolled twice."),
        noPlacesAvailable("No places available for courseClass: %s."),
        noLicenseCCProcessing("The college doesn't have a license for processing credit card payments");

        private String message;

        Error(String message) {
            this.message = message;
        }


        public String getMessage() {
            return message;
        }
    }
}
