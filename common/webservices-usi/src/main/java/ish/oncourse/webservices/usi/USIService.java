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

	private static final String ERROR_LOCATE_MESSAGE_TEMPLATE = "USI service failed during locate request, orgCode: %s, firstName: %s, lastName: %s. ";

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
		LocateUSIResult intRs = new LocateUSIResult();

		LocateUSIType extRq = LocateUSITypeBuilder.valueOf(intRq).build();

		if (extRq != null) {
			try {
				LocateUSIResponseType extRs = endpoint.locateUSI(extRq);

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
						String warnMessage = createLocateDetailErrorMessage(intRq);
						logger.warn(warnMessage.concat(StringUtils.join(extRs.getErrors().getError(), ';')));

						intRs.setResultType(ish.common.types.LocateUSIType.ERROR);
						intRs.setError(warnMessage);
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);

				intRs.setResultType(ish.common.types.LocateUSIType.ERROR);
				intRs.setError(createLocateDetailErrorMessage(intRq));
			}
		} else {
			String warnMessage = createLocateDetailErrorMessage(intRq).concat("Internal request has incomplete field set.");
			logger.warn(warnMessage);

			intRs.setResultType(ish.common.types.LocateUSIType.ERROR);
			intRs.setError(warnMessage);
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

			USIVerificationResult usiResult = new USIVerificationResult();
			usiResult.setErrorMessage("Error verifying USI.");
			return usiResult;
		}
	}

	private String createLocateDetailErrorMessage(LocateUSIRequest intRq) {
		return String.format(ERROR_LOCATE_MESSAGE_TEMPLATE, intRq.getOrgCode(), intRq.getFirstName(), intRq.getFamilyName());
	}

	public static USIService valueOf(IUSIService endpoint) {
		USIService usiService = new USIService();
		usiService.endpoint = endpoint;
		return usiService;
	}
}
