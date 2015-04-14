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

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.GregorianCalendar;

public class USIService {
	
	private static final Logger logger = LogManager.getLogger();
	
	private IUSIService endpoint;
	
	public USIService() {
		au.gov.usi._2013.ws.servicepolicy.USIService service = new au.gov.usi._2013.ws.servicepolicy.USIService();
		this.endpoint = service.getWS2007FederationHttpBindingIUSIService();
	}
	
	public USIVerificationResult verifyUsi(USIVerificationRequest request) {
		try {
			VerifyUSIType verifyUSI = new VerifyUSIType();

			GregorianCalendar cal = new GregorianCalendar();
			cal.setTime(request.getStudentBirthDate());
			XMLGregorianCalendar xmlBirthDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
			xmlBirthDate.setTimezone(DatatypeConstants.FIELD_UNDEFINED);

			verifyUSI.setFirstName(request.getStudentFirstName());
			verifyUSI.setFamilyName(request.getStudentLastName());
			verifyUSI.setDateOfBirth(xmlBirthDate);
			verifyUSI.setOrgCode(request.getOrgCode());
			verifyUSI.setUSI(request.getUsiCode());

			VerifyUSIResponseType response = endpoint.verifyUSI(verifyUSI);

			USIVerificationResult result = new USIVerificationResult();

			result.setUsiStatus(USIVerificationStatus.fromString(response.getUSIStatus()));
			result.setFirstNameStatus(USIFieldStatus.fromString(response.getFirstName().value()));
			result.setLastNameStatus(USIFieldStatus.fromString(response.getFamilyName().value()));
			result.setDateOfBirthStatus(USIFieldStatus.fromString(response.getDateOfBirth().value()));

			return result;
		} catch (Exception e) {
			logger.error("Unable to verify USI code for {} {}.", request.getStudentFirstName(), request.getStudentLastName(), e);
			
			throw new RuntimeException("Error verifying USI.", e);
		}
	}
}
