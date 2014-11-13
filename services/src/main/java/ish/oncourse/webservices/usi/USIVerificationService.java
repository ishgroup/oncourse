/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.usi;

import ish.common.types.USIVerificationRequest;
import ish.common.types.USIVerificationResult;
import ish.oncourse.services.usi.IUSIVerificationService;
import org.apache.tapestry5.ioc.annotations.Inject;

public class USIVerificationService implements IUSIVerificationService {
	
	private USIService usiService;
	
	@Inject
	public USIVerificationService() {
		this.usiService = new USIService();
	}

	@Override
	public USIVerificationResult verifyUsi(USIVerificationRequest request) {
		return usiService.verifyUsi(request);
	}
}
