/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.usi;

import ish.common.types.USIVerificationRequest;
import ish.common.types.USIVerificationResult;
import ish.oncourse.services.usi.IUSIVerificationService;
import ish.oncourse.util.ContextUtil;

public class USIVerificationService implements IUSIVerificationService {
	
	private USIService usiService;
	
	public USIVerificationService() {
		this.usiService = new USIService(
				ContextUtil.getAuskeyKeystorePath(),
				ContextUtil.getAuskeyAlias(),
				ContextUtil.getAuskeyPassword(),
				ContextUtil.getUsiStsEndpoint());
	}

	@Override
	public USIVerificationResult verifyUsi(USIVerificationRequest request) {
		return usiService.verifyUsi(request);
	}
}
