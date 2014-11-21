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
        result.setUsiStatus(USIVerificationStatus.VALID);
        result.setDateOfBirthStatus(USIFieldStatus.MATCH);
        result.setFirstNameStatus(USIFieldStatus.MATCH);
        result.setLastNameStatus(USIFieldStatus.MATCH);
        return result;
    }
}
