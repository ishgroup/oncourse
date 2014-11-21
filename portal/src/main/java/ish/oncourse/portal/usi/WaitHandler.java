package ish.oncourse.portal.usi;

import ish.common.types.USIFieldStatus;
import ish.common.types.USIVerificationRequest;
import ish.common.types.USIVerificationResult;
import ish.oncourse.model.Contact;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.concurrent.*;

import static ish.oncourse.portal.usi.UsiController.Step;
import static ish.oncourse.portal.usi.UsiController.Step.*;

/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class WaitHandler extends AbstractStepHandler {

    private static final Logger LOGGER = Logger.getLogger(WaitHandler.class);

    private Step nextStep = wait;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Future<USIVerificationResult> future;

    private USIVerificationResult verificationResult;

    private int count;

    public Step getNextStep() {
        return nextStep;
    }

    public WaitHandler handle(Map<String, Value> input) {
        if (future == null)
        {
            Callable<USIVerificationResult> callable = new Callable<USIVerificationResult>() {
                @Override
                public USIVerificationResult call() throws Exception {
                    USIVerificationRequest request = new USIVerificationRequest();
                    request.setOrgCode(getUsiController().getPreferenceController().getAvetmissID());
                    request.setStudentBirthDate(getUsiController().getContact().getDateOfBirth());
                    request.setStudentFirstName(getUsiController().getContact().getGivenName());
                    request.setStudentLastName(getUsiController().getContact().getFamilyName());
                    return getUsiController().getUsiVerificationService().verifyUsi(request);
                }
            };
            future =  executorService.submit(callable);
        }

        try {
            verificationResult = future.get(100L, TimeUnit.MILLISECONDS);
            Contact contact = getUsiController().getContact();
            switch (verificationResult.getUsiStatus())
            {

                case VALID:
                    if (verificationResult.getLastNameStatus() == USIFieldStatus.NO_MATCH)
                    {
                        nextStep = step1Failed;
                        result.put(Contact.FAMILY_NAME_PROPERTY,
                                Value.valueOf(Contact.FAMILY_NAME_PROPERTY, contact.getFamilyName(), getUsiController().getMessages().format("message-fieldNotMatch")));
                    }
                    if (verificationResult.getFirstNameStatus() == USIFieldStatus.NO_MATCH)
                    {
                        nextStep = step1Failed;
                        result.put(Contact.FAMILY_NAME_PROPERTY,
                                Value.valueOf(Contact.FAMILY_NAME_PROPERTY, contact.getGivenName(), getUsiController().getMessages().format("message-fieldNotMatch")));
                    }
                    if (verificationResult.getFirstNameStatus() == USIFieldStatus.NO_MATCH)
                    {
                        nextStep = step1Failed;
                        result.put(Contact.DATE_OF_BIRTH_PROPERTY,
                                Value.valueOf(Contact.DATE_OF_BIRTH_PROPERTY, contact.getDateOfBirth(), getUsiController().getMessages().format("message-fieldNotMatch")));
                    }

                    break;
                case INVALID:
                case DEACTIVATED:
                    nextStep = step1;
                    getUsiController().getValidationResult().addError("message-invalidUsi");
                    break;
            }

        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error(e.getMessage(), e);
            hasErrors = true;
        } catch (TimeoutException e) {
            count++;
        }

        return this;
    }
}
