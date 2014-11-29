package ish.oncourse.portal.services;

import ish.common.types.USIFieldStatus;
import ish.common.types.USIVerificationRequest;
import ish.common.types.USIVerificationResult;
import ish.common.types.USIVerificationStatus;
import ish.oncourse.services.usi.IUSIVerificationService;

/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class TestUSISevice implements IUSIVerificationService {
    @Override
    public USIVerificationResult verifyUsi(USIVerificationRequest request) {
        USIVerificationResult result = new USIVerificationResult();

        if (request.getUsiCode().endsWith("_W")) {
            result.setUsiStatus(USIVerificationStatus.INVALID);
        } else {
            result.setUsiStatus(USIVerificationStatus.VALID);
        }

        if (request.getUsiCode().endsWith("_WL")) {
            result.setLastNameStatus(USIFieldStatus.NO_MATCH);
        } else {
            result.setLastNameStatus(USIFieldStatus.MATCH);
        }

        if (request.getUsiCode().endsWith("_WF")) {
            result.setFirstNameStatus(USIFieldStatus.NO_MATCH);
        } else {
            result.setFirstNameStatus(USIFieldStatus.MATCH);
        }

        if (request.getUsiCode().endsWith("_WD")) {
            result.setDateOfBirthStatus(USIFieldStatus.NO_MATCH);
        } else {
            result.setDateOfBirthStatus(USIFieldStatus.MATCH);
        }
        try {
            Thread.sleep(15*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }
}
