package ish.oncourse.portal.usi;

import ish.common.types.*;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Student;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class WaitLocateHandler extends AbstractStepHandler {

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

    public WaitLocateHandler handle(Map<String, Value> input) {
        if (nextStep.getAndSet(Step.waitLocate) == Step.usi) {
                locateUsi();

        }
        return this;
    }

    private void locateUsi() {
        Contact contact = getUsiController().getContact();
        Student student = contact.getStudent();

        if (isUsiCanLocated(contact)) {
            LocateUSIRequest rq = prepareLocateUsiRequest(contact);
            LocateUSIResult usiResult = getUsiController().getUsiVerificationService().locateUsi(rq);
            if (LocateUSIType.MATCH.equals(usiResult.getResultType())) {
                student.setUsi(usiResult.getUsi());
                student.setUsiStatus(UsiStatus.VERIFIED);
                student.getObjectContext().commitChanges();
                nextStep.set(Step.done);
            } else {
                nextStep.set(Step.usi);
            }
        } else {
            nextStep.set(Step.usi);
        }
    }

    private boolean isUsiCanLocated(Contact c) {
        Student s = c.getStudent();
        if (s != null && !s.getUsiStatus().equals(UsiStatus.VERIFIED)) {
            if (c.getGivenName() != null &&
                    c.getFamilyName() != null &&
                    c.getGender() != null &&
                    c.getDateOfBirth() != null &&
                    s.getTownOfBirth() != null) {
                return true;
            }
        }
        return false;
    }

    private LocateUSIRequest prepareLocateUsiRequest(Contact c) {

        LocateUSIRequest rq = new LocateUSIRequest();

        String billingCode = c.getCollege().getBillingCode().length() <=  40 ? c.getCollege().getBillingCode() : c.getCollege().getBillingCode().substring(0, 40);

        rq.setOrgCode(getUsiController().getPreferenceController().getAvetmissID());
        rq.setUserReference(billingCode);
        rq.setFamilyName(c.getFamilyName());
        rq.setGender(c.getIsMale() ? USIGender.MALE : USIGender.FEMALE);
        rq.setDateOfBirth(c.getDateOfBirth());
        rq.setFirstName(c.getGivenName());
        rq.setTownCityOfBirth(c.getStudent().getTownOfBirth());
        return rq;
    }
}
