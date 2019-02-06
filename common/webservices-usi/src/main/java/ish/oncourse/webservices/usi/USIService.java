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

	public LocateUSIResult locateUSI(LocateUSIRequest intRq) {
		LocateUSIType extRq = LocateUSITypeBuilder.valueOf(intRq).build();
		LocateUSIResult intRs = null;
		if (extRq != null) {
			LocateUSIResponseType extRs = sendLocateRequest(extRq);
			intRs = castToInternalLocateRs(extRs);
		} else {
			intRs = LocateUSIResult.valueOf(String.format("USI can not be located for orgCode: %s, firstName: %s, lastName: %s. Internal request has incomplete field set.",
					intRq.getOrgCode(), intRq.getFirstName(), intRq.getFamilyName()));
		}
		return intRs;
	}

	private LocateUSIResponseType sendLocateRequest(LocateUSIType rq) {
		LocateUSIResponseType rs = null;
		try {
			rs = endpoint.locateUSI(rq);
		} catch (Exception e) {
			logger.error("Locate USI service request failed. OrgCode: {}, firstName: {}, lastName: {}", e);
		}
		return rs;
	}

	private LocateUSIResult castToInternalLocateRs(LocateUSIResponseType extRs) {
		LocateUSIResult intRs = new LocateUSIResult();

		switch (extRs.getResult()) {
			case EXACT: {
				intRs.setResultType(ish.common.types.LocateUSIType.MATCH);
				intRs.setUsi(extRs.getUSI());
				intRs.setMessage(extRs.getContactDetailsMessage());
			}
			break;
			case MULTIPLE_EXACT: {
				intRs.setResultType(ish.common.types.LocateUSIType.MORE_DETAILS_EXPECTED);
			}
			break;
			case NO_MATCH: {
				intRs.setResultType(ish.common.types.LocateUSIType.NO_MATCH);
			}
			break;
			default: {
				intRs.setError(StringUtils.join(extRs.getErrors().getError(), ';'));
			}
		}
		return intRs;
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
