/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.usi;

import au.gov.usi._2013.ws.VerifyUSIResponseType;
import au.gov.usi._2013.ws.VerifyUSIType;
import au.gov.usi._2013.ws.servicepolicy.IUSIService;
import ish.common.types.USIFieldStatus;
import ish.common.types.USIVerificationRequest;
import ish.common.types.USIVerificationResult;
import ish.common.types.USIVerificationStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class USIService {
	
	private static final Logger logger = LogManager.getLogger();
	
	private IUSIService endpoint;
	
	public USIService() {
		au.gov.usi._2013.ws.servicepolicy.USIService service = new au.gov.usi._2013.ws.servicepolicy.USIService();
		this.endpoint = service.getWS2007FederationHttpBindingIUSIService();
	}
	
	public USIVerificationResult verifyUsi(USIVerificationRequest request) {
		USIVerificationResult result = sendRequest(VerifyUSITypeBuilder.valueOf(request, false).build());
		if (needSendSingleNameRequest(request, result)) {
			return sendRequest(VerifyUSITypeBuilder.valueOf(request, true).build());
		} else {
			return result;
		}
	}

	private boolean needSendSingleNameRequest(USIVerificationRequest request, USIVerificationResult result) {
		return (result.getFirstNameStatus() == USIFieldStatus.NO_MATCH || result.getLastNameStatus() == USIFieldStatus.NO_MATCH) &&
		request.getStudentFirstName().equalsIgnoreCase(request.getStudentLastName());
	}

	private USIVerificationResult sendRequest(VerifyUSIType verifyUSIType) {
		try {
			VerifyUSIResponseType response = endpoint.verifyUSI(verifyUSIType);

			USIVerificationResult result = new USIVerificationResult();

			result.setUsiStatus(USIVerificationStatus.fromString(response.getUSIStatus()));
			result.setFirstNameStatus(USIFieldStatus.fromString(response.getFirstName().value()));
			result.setLastNameStatus(USIFieldStatus.fromString(response.getFamilyName().value()));
			result.setDateOfBirthStatus(USIFieldStatus.fromString(response.getDateOfBirth().value()));

			return result;
		} catch (Exception e) {
			logger.error("Unable to verify USI code for {} {}.", verifyUSIType.getFirstName(), verifyUSIType.getFamilyName(), e);

			throw new RuntimeException("Error verifying USI.", e);
		}
	}
}
