package ish.oncourse.webservices.usi;

import au.gov.usi._2018.ws.VerifyUSIType;
import ish.common.types.USIVerificationRequest;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class VerifyUSITypeBuilderTest {

	@Test
	public void test(){
		USIVerificationRequest request = new USIVerificationRequest();
		request.setStudentLastName("StudentLastName");
		request.setStudentFirstName("StudentFirstName");
		request.setStudentBirthDate(new Date());
		request.setOrgCode("OrgCode");
		request.setUsiCode("UsiCode");

		VerifyUSIType type = VerifyUSITypeBuilder.valueOf(request, false).build();
		assertEquals(request.getStudentFirstName(), type.getFirstName());
		assertEquals(request.getStudentLastName(), type.getFamilyName());
		assertEquals(request.getOrgCode(), type.getOrgCode());
		assertEquals(request.getUsiCode(), type.getUSI());
		assertEquals(request.getStudentBirthDate(), type.getDateOfBirth().toGregorianCalendar().getTime());


		type = VerifyUSITypeBuilder.valueOf(request, true).build();
		assertEquals(request.getStudentFirstName(), type.getSingleName());
		assertNull(type.getFirstName());
		assertNull(type.getFamilyName());

		assertEquals(request.getOrgCode(), type.getOrgCode());
		assertEquals(request.getUsiCode(), type.getUSI());
		assertEquals(request.getStudentBirthDate(), type.getDateOfBirth().toGregorianCalendar().getTime());

	}
}
