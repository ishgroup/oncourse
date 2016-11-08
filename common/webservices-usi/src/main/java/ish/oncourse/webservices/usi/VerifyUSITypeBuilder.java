package ish.oncourse.webservices.usi;

import au.gov.usi._2015.ws.VerifyUSIType;
import ish.common.types.USIVerificationRequest;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;
import java.util.GregorianCalendar;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class VerifyUSITypeBuilder {
	private Date dateOfBirth;
	private String firstName;
	private String familyName;
	private String singleName;
	private String orgCode;
	private String usi;

	public VerifyUSIType build() {
		try {
			VerifyUSIType verifyUSI = new VerifyUSIType();

			GregorianCalendar cal = new GregorianCalendar();
			cal.setTime(dateOfBirth);
			XMLGregorianCalendar xmlBirthDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
			xmlBirthDate.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
			verifyUSI.setDateOfBirth(xmlBirthDate);

			if (singleName != null) {
				verifyUSI.setSingleName(singleName);
			} else {
				verifyUSI.setFirstName(firstName);
				verifyUSI.setFamilyName(familyName);
			}

			verifyUSI.setOrgCode(orgCode);
			verifyUSI.setUSI(usi);
			return verifyUSI;
		} catch (DatatypeConfigurationException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static VerifyUSITypeBuilder valueOf(USIVerificationRequest request, boolean useSingleName) {
		VerifyUSITypeBuilder builder = new VerifyUSITypeBuilder();
		builder.dateOfBirth = request.getStudentBirthDate();
		builder.firstName = request.getStudentFirstName();
		builder.familyName = request.getStudentLastName();
		builder.orgCode = request.getOrgCode();
		builder.usi = request.getUsiCode();
		builder.singleName = useSingleName ? builder.firstName : null;
		return builder;
	}

}
