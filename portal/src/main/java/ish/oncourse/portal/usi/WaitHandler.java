package ish.oncourse.portal.usi;

import ish.common.types.USIFieldStatus;
import ish.common.types.USIVerificationRequest;
import ish.common.types.USIVerificationResult;
import ish.common.types.UsiStatus;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Student;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class WaitHandler extends AbstractStepHandler {

    private static final long USI_SERVICE_TIMEOUT = 1000 * 60;

    private static final Logger logger = LogManager.getLogger();

    private AtomicReference<Step> nextStep = new AtomicReference<>(Step.usi);

    @Override
    public Result getValue() {
        if (result.isEmpty()) {
            Student student = getUsiController().getContact().getStudent();
            addValue(Value.valueOf(Student.USI.getName(), student.getUsi()));
            addValue(Value.valueOf(Student.USI_STATUS.getName(), student.getUsiStatus() != null ? student.getUsiStatus().name() :
                    UsiStatus.DEFAULT_NOT_SUPPLIED));
        }
        return result;
    }

    public Step getNextStep() {
        return nextStep.get();
    }

    public WaitHandler handle(Map<String, Value> input) {
        if (nextStep.getAndSet(Step.wait) == Step.usi) {
            verifyUsi();
        }
        return this;
    }

    private void verifyUsi() {
        Contact contact = getUsiController().getContact();
        try {
            String avetmissID = getUsiController().getPreferenceController().getAvetmissID();
            if (avetmissID == null) {
                getUsiController().getValidationResult().addError("messaget-avetmissIdentifierNotSet", getUsiController().getMessages());
                nextStep.set(Step.usi);
                return;
            }

            String certificate = getUsiController().getPreferenceController().getAuskeyCertificate();
            if (certificate == null)
            {
                getUsiController().getValidationResult().addError("messaget-auskeyCertificateNotSet", getUsiController().getMessages());
                nextStep.set(Step.usi);
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
                        result.addValue(Value.valueOf(Contact.FAMILY_NAME.getName(), contact.getFamilyName(), getUsiController().getMessages().format("message-fieldNotMatch")));
                    }
                    if (verificationResult.getFirstNameStatus() == USIFieldStatus.NO_MATCH) {
                        match = false;
                        result.addValue(
                                Value.valueOf(Contact.GIVEN_NAME.getName(), contact.getGivenName(), getUsiController().getMessages().format("message-fieldNotMatch")));
                    }
                    if (verificationResult.getDateOfBirthStatus() == USIFieldStatus.NO_MATCH) {
                        match = false;
                        result.addValue(
                                Value.valueOf(Contact.DATE_OF_BIRTH.getName(), contact.getDateOfBirth(), getUsiController().getMessages().format("message-fieldNotMatch")));
                    }
                    if (match) {
                        contact.getStudent().setUsiStatus(UsiStatus.VERIFIED);
                        nextStep.set(Step.done);
                    } else {
                        nextStep.set(Step.usiFailed);
                        result.addValue(Value.valueOf(Student.USI.getName(), contact.getStudent().getUsi()));
                        contact.getStudent().setUsiStatus(UsiStatus.NON_VERIFIED);
                        getUsiController().getValidationResult().addError("message-personalDetailsNotMatch", getUsiController().getMessages());
                    }
                    break;
                case INVALID:
                case DEACTIVATED:
                    contact.getStudent().setUsiStatus(UsiStatus.NON_VERIFIED);
                    nextStep.set(Step.usi);
                    result.addValue(
                            Value.valueOf(Student.USI.getName(), contact.getStudent().getUsi(), getUsiController().getMessages().format("message-fieldNotMatch")));
                    getUsiController().getValidationResult().addError("message-invalidUsi", getUsiController().getMessages());
                    break;
            }

        } catch (Exception e) {
            contact.getStudent().setUsiStatus(UsiStatus.NON_VERIFIED);
            logger.catching(e);
            nextStep.set(Step.usi);
            getUsiController().getValidationResult().addError("message-usiServiceUnexpectedException", getUsiController().getMessages());
        }
    }
}
