/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.usi;

import ish.common.types.USIVerificationRequest;
import ish.common.types.USIVerificationResult;

public interface IUSIVerificationService {

	USIVerificationResult verifyUsi(USIVerificationRequest request);
}