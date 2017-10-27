/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.usi;

import au.gov.usi._2015.ws.VerifyUSIResponseType;
import au.gov.usi._2015.ws.VerifyUSIType;
import au.gov.usi._2015.ws.servicepolicy.IUSIService;
import ish.common.types.USIFieldStatus;
import ish.common.types.USIVerificationRequest;
import ish.common.types.USIVerificationResult;
import ish.common.types.USIVerificationStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class USIService {
	
	private static final Logger logger = LogManager.getLogger();
	
	private IUSIService endpoint;
	
	private USIService() {
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
			result.setDateOfBirthStatus(USIFieldStatus.fromString(response.getDateOfBirth().value()));

			if (verifyUSIType.getSingleName() != null) {
				result.setFirstNameStatus(USIFieldStatus.fromString(response.getSingleName().value()));
				result.setLastNameStatus(USIFieldStatus.fromString(response.getSingleName().value()));
			} else {
				result.setFirstNameStatus(response.getFirstName() != null ? USIFieldStatus.fromString(response.getFirstName().value()) : USIFieldStatus.NO_MATCH);
				result.setLastNameStatus(response.getFirstName() != null ? USIFieldStatus.fromString(response.getFamilyName().value()) : USIFieldStatus.NO_MATCH);
			}

			return result;
		} catch (Exception e) {
			logger.error("Unable to verify USI code for {} {} in organisation code {}.", verifyUSIType.getFirstName(), 
					verifyUSIType.getFamilyName(), verifyUSIType.getOrgCode(), e);

			return USIVerificationResult.valueOf("Error verifying USI.");
		}
	}

	public static USIService valueOf(IUSIService endpoint) {
		USIService usiService = new USIService();
		usiService.endpoint = endpoint;
		return usiService;
	}
}
