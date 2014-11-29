package ish.oncourse.portal.usi;

import ish.common.types.USIFieldStatus;
import ish.common.types.USIVerificationRequest;
import ish.common.types.USIVerificationResult;
import ish.common.types.UsiStatus;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Student;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static ish.oncourse.portal.usi.UsiController.Step;
import static ish.oncourse.portal.usi.UsiController.Step.*;

/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class WaitHandler extends AbstractStepHandler {

    private static final long USI_SERVICE_TIMEOUT = 1000 * 60;

    private static final Logger LOGGER = Logger.getLogger(WaitHandler.class);

    private AtomicReference<Step> nextStep = new AtomicReference<>(step1);

    @Override
    public Result getValue() {
        if (result.isEmpty()) {
            Student student = getUsiController().getContact().getStudent();
            addValue(Value.valueOf(Student.USI_PROPERTY, student.getUsi()));
            addValue(Value.valueOf(Student.USI_STATUS_PROPERTY, student.getUsiStatus() != null ? student.getUsiStatus().name() :
                    UsiStatus.DEFAULT_NOT_SUPPLIED));
        }
        return result;
    }

    public Step getNextStep() {
        return nextStep.get();
    }

    public WaitHandler handle(Map<String, Value> input) {
        if (nextStep.getAndSet(wait) == step1) {
            verifyUsi();
        }
        return this;
    }

    private void verifyUsi() {
        Contact contact = getUsiController().getContact();
        try {
            String avetmissID = getUsiController().getPreferenceController().getAvetmissID();
            if (avetmissID == null) {
                getUsiController().getValidationResult().addError("messaget-avetmissIdentifierNotSet");
                nextStep.set(step1);
                return;
            }

            String certificate = getUsiController().getPreferenceController().getAuskeyCertificate();
            if (certificate == null)
            {
                getUsiController().getValidationResult().addError("messaget-auskeyCertificateNotSet");
                nextStep.set(step1);
                return;
            }

            USIVerificationRequest request = new USIVerificationRequest();
            request.setOrgCode(avetmissID);
            request.setStudentBirthDate(getUsiController().getContact().getDateOfBirth());
            request.setStudentFirstName(getUsiController().getContact().getGivenName());
            request.setStudentLastName(getUsiController().getContact().getFamilyName());
            request.setUsiCode(getUsiController().getContact().getStudent().getUsi());
            USIVerificationResult verificationResult = getUsiController().getUsiVerificationService().verifyUsi(request);

            switch (verificationResult.getUsiStatus()) {

                case VALID:
                    boolean match = true;
                    if (verificationResult.getLastNameStatus() == USIFieldStatus.NO_MATCH) {
                        match = false;
                        result.addValue(Value.valueOf(Contact.FAMILY_NAME_PROPERTY, contact.getFamilyName(), getUsiController().getMessages().format("message-fieldNotMatch")));
                    }
                    if (verificationResult.getFirstNameStatus() == USIFieldStatus.NO_MATCH) {
                        match = false;
                        result.addValue(
                                Value.valueOf(Contact.GIVEN_NAME_PROPERTY, contact.getGivenName(), getUsiController().getMessages().format("message-fieldNotMatch")));
                    }
                    if (verificationResult.getDateOfBirthStatus() == USIFieldStatus.NO_MATCH) {
                        match = false;
                        result.addValue(
                                Value.valueOf(Contact.DATE_OF_BIRTH_PROPERTY, contact.getDateOfBirth(), getUsiController().getMessages().format("message-fieldNotMatch")));
                    }
                    if (match) {
                        contact.getStudent().setUsiStatus(UsiStatus.VERIFIED);
                        nextStep.set(step1Done);
                    } else {
                        nextStep.set(step1Failed);
                        contact.getStudent().setUsiStatus(UsiStatus.NON_VERIFIED);
                        getUsiController().getValidationResult().addError("message-personalDetailsNotMatch");
                    }
                    break;
                case INVALID:
                case DEACTIVATED:
                    contact.getStudent().setUsiStatus(UsiStatus.NON_VERIFIED);
                    nextStep.set(step1);
                    result.addValue(
                            Value.valueOf(Student.USI_PROPERTY, contact.getStudent().getUsi(), getUsiController().getMessages().format("message-fieldNotMatch")));
                    getUsiController().getValidationResult().addError("message-invalidUsi");
                    break;
            }

        } catch (Exception e) {
            contact.getStudent().setUsiStatus(UsiStatus.NON_VERIFIED);
            LOGGER.error(e.getMessage(), e);
            nextStep.set(step1);
            getUsiController().getValidationResult().addError("message-usiServiceUnexpectedException");
        }
    }
}
