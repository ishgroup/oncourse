/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.portal.services;

import ish.common.types.LocateUSIRequest;
import ish.common.types.LocateUSIResult;
import ish.common.types.USIVerificationRequest;
import ish.common.types.USIVerificationResult;
import ish.oncourse.services.usi.IUSIVerificationService;
import ish.oncourse.webservices.usi.USIService;
import org.apache.tapestry5.ioc.annotations.Inject;

public class PortalUSIService implements IUSIVerificationService {

	@Inject
	private USIService usiService;
	
	@Inject
	public PortalUSIService(){
	}

	@Override
	public USIVerificationResult verifyUsi(USIVerificationRequest request) {
		return usiService.verifyUsi(request);
	}

	@Override
	public LocateUSIResult locateUsi(LocateUSIRequest request) { return usiService.locateUSI(request); }
}
