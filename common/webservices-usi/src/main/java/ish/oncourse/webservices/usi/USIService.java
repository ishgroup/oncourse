/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.usi;

import au.gov.usi._2013.ws.VerifyUSIResponseType;
import au.gov.usi._2013.ws.VerifyUSIType;
import au.gov.usi._2013.ws.servicepolicy.IUSIService;
import org.apache.log4j.Logger;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class USIService {
	
	private static final Logger logger = Logger.getLogger(USIService.class);
	
	private String keystorePath;
	private String auskeyAlias;
	private String auskeyPassword;
	private String stsEndpoint;
	
	public USIService(String keystorePath, String auskeyAlias, String auskeyPassword, String stsEndpoint) {
		this.keystorePath = keystorePath;
		this.auskeyAlias = auskeyAlias;
		this.auskeyPassword = auskeyPassword;
		this.stsEndpoint = stsEndpoint;
	}
	
	public USIVerificationResult verifyUsi(String firstName, String lastName, Date birthDate, String orgCode, String usi) {
		try {
			IUSIService endpoint = AUSKeyUtil.createUSIService(keystorePath, auskeyAlias, auskeyPassword, stsEndpoint);

			VerifyUSIType verifyUSI = new VerifyUSIType();

			GregorianCalendar cal = new GregorianCalendar();
			cal.setTime(birthDate);
			XMLGregorianCalendar xmlBirthDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);

			verifyUSI.setFirstName(firstName);
			verifyUSI.setFamilyName(lastName);
			verifyUSI.setDateOfBirth(xmlBirthDate);
			verifyUSI.setOrgCode(orgCode);
			verifyUSI.setUSI(usi);

			VerifyUSIResponseType response = endpoint.verifyUSI(verifyUSI);

			return new USIVerificationResult(
					USIStatus.fromString(response.getUSIStatus()),
					USIFieldStatus.fromString(response.getFirstName().value()),
					USIFieldStatus.fromString(response.getFamilyName().value()),
					USIFieldStatus.fromString(response.getDateOfBirth().value()));

		} catch (Exception e) {
			logger.error(String.format("Unable to verify USI code for %s %s.", firstName, lastName), e);
			
			throw new RuntimeException("Error verifying USI.", e);
		}
	}
}
