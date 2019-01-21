/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.usi;

import au.gov.usi._2018.ws.*;
import au.gov.usi._2018.ws.LocateUSIType;
import au.gov.usi._2018.ws.servicepolicy.IUSIService;
import ish.common.types.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class USIService {

	private static final Logger logger = LogManager.getLogger();

	private IUSIService endpoint;

	private USIService() {
	}

	public USIVerificationResult verifyUsi(USIVerificationRequest request) {
		USIVerificationResult result = sendVerifyRequest(VerifyUSITypeBuilder.valueOf(request, false).build());
		if (needSendSingleNameRequest(request, result)) {
			return sendVerifyRequest(VerifyUSITypeBuilder.valueOf(request, true).build());
		} else {
			return result;
		}
	}

	public LocateUSIResult locateUSI(LocateUSIRequest internalRq) {
		try {
			LocateUSIType serviceRq = LocateUSITypeBuilder.valueOf(internalRq).build();
			LocateUSIResponseType serviceRs = endpoint.locateUSI(serviceRq);

			LocateUSIResult internalRs = new LocateUSIResult();

			switch (serviceRs.getResult()) {
				case EXACT: {
					internalRs.setResultType(ish.common.types.LocateUSIType.MATCH);
					internalRs.setUsi(serviceRs.getUSI());
					internalRs.setMessage(serviceRs.getContactDetailsMessage());
				}
				break;
				case MULTIPLE_EXACT: {
					internalRs.setResultType(ish.common.types.LocateUSIType.MORE_DETAILS_EXPECTED);
				}
				break;
				case NO_MATCH: {
					internalRs.setResultType(ish.common.types.LocateUSIType.NO_MATCH);
				}
				break;
				default: {
					internalRs.setError(StringUtils.join(serviceRs.getErrors().getError(),';'));
				}
			}
			return internalRs;
		} catch (Exception e) {
			logger.error("Unable to locate USI code for {} {} in organisation code {}.", internalRq.getFirstName(),
					internalRq.getFamilyName(), internalRq.getOrgCode(), e);

			return LocateUSIResult.valueOf("Error verifying USI.");
		}
	}

	private boolean needSendSingleNameRequest(USIVerificationRequest request, USIVerificationResult result) {
		return (result.getFirstNameStatus() == USIFieldStatus.NO_MATCH || result.getLastNameStatus() == USIFieldStatus.NO_MATCH) &&
				request.getStudentFirstName().equalsIgnoreCase(request.getStudentLastName());
	}

	private USIVerificationResult sendVerifyRequest(VerifyUSIType verifyUSIType) {
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
