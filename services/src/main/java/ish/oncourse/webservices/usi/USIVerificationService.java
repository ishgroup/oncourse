/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.usi;

import ish.common.types.LocateUSIRequest;
import ish.common.types.LocateUSIResult;
import ish.common.types.USIVerificationRequest;
import ish.common.types.USIVerificationResult;
import ish.oncourse.services.usi.IUSIVerificationService;
import ish.oncourse.services.usi.IUsiRestService;
import ish.oncourse.services.usi.UsiRestService;
import org.apache.tapestry5.ioc.annotations.Inject;

public class USIVerificationService implements IUSIVerificationService {

	@Inject
	private IUsiRestService restService;

	@Inject
	public USIVerificationService() {
	}

	@Override
	public USIVerificationResult verifyUsi(USIVerificationRequest request) {
		return restService.verify(request);
	}

	@Override
	public LocateUSIResult locateUsi(LocateUSIRequest request) {
		return restService.locate(request);
	}
}
