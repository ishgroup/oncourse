/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.usi;

import ish.common.types.LocateUSIRequest;
import ish.common.types.LocateUSIResult;
import ish.common.types.USIVerificationRequest;
import ish.common.types.USIVerificationResult;
import ish.oncourse.services.usi.IUSIVerificationService;
import org.apache.tapestry5.ioc.annotations.Inject;

public class USIVerificationService implements IUSIVerificationService {

	@Inject
	private USIService usiService;
	
	@Inject
	public USIVerificationService() {
	}

	@Override
	public USIVerificationResult verifyUsi(USIVerificationRequest request) {
		return usiService.verifyUsi(request);
	}

	@Override
	public LocateUSIResult locateUsi(LocateUSIRequest request) { return usiService.locateUSI(request); }
}
