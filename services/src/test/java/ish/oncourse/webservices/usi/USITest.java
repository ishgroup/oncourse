/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.usi;

import ish.common.types.USIFieldStatus;
import ish.common.types.USIVerificationRequest;
import ish.common.types.USIVerificationResult;
import ish.common.types.USIVerificationStatus;
import ish.oncourse.webservices.util.GenericParametersMap;
import ish.oncourse.webservices.util.SupportedVersions;
import ish.oncourse.webservices.util.USIVerificationUtil;
import ish.util.UrlUtil;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Date;

import static ish.oncourse.webservices.usi.TestUSIServiceEndpoint.*;
import static org.junit.Assert.assertEquals;

/**
 * User: akoiro
 * Date: 29/7/17
 */
public class USITest {

	private Parent parent;


	USITest(Parent parent) {
		this.parent = parent;
	}

	public void testUSI() throws Exception {

		UrlUtil.createPortalUsiLink("LtLrTbSpCeSdn3Sw", DateUtils.addMonths(new Date(), 1), "kRJNAhJy69YGp5kX");

		parent.authenticate();

		testWrongUSI();
		testWrongLastName();
		testWrongFirstName();
		testWrongDate();
		testWrongSingleName();
	}

	private void testWrongUSI() throws Exception {
		USIVerificationRequest usiRequest = new USIVerificationRequest();
		usiRequest.setUsiCode(USI_INVALID);
		usiRequest.setOrgCode("1234567890");
		usiRequest.setStudentBirthDate(new Date());
		usiRequest.setStudentFirstName("First");
		usiRequest.setStudentLastName("Last");

		GenericParametersMap response = sendRequest(usiRequest);

		USIVerificationResult usiResult = USIVerificationUtil.parseVerificationResult(response);
		assertEquals(USIVerificationStatus.INVALID, usiResult.getUsiStatus());
		assertEquals(USIFieldStatus.MATCH, usiResult.getFirstNameStatus());
		assertEquals(USIFieldStatus.MATCH, usiResult.getLastNameStatus());
		assertEquals(USIFieldStatus.MATCH, usiResult.getDateOfBirthStatus());
	}

	private void testWrongLastName() throws Exception {
		USIVerificationRequest usiRequest = new USIVerificationRequest();
		usiRequest.setUsiCode(USI_NOT_MATCH_LAST_NAME);
		usiRequest.setOrgCode("1234567890");
		usiRequest.setStudentBirthDate(new Date());
		usiRequest.setStudentFirstName("First");
		usiRequest.setStudentLastName("Last");

		GenericParametersMap response = sendRequest(usiRequest);

		USIVerificationResult usiResult = USIVerificationUtil.parseVerificationResult(response);
		assertEquals(USIVerificationStatus.VALID, usiResult.getUsiStatus());
		assertEquals(USIFieldStatus.MATCH, usiResult.getFirstNameStatus());
		assertEquals(USIFieldStatus.NO_MATCH, usiResult.getLastNameStatus());
		assertEquals(USIFieldStatus.MATCH, usiResult.getDateOfBirthStatus());

	}

	private void testWrongFirstName() throws Exception {
		USIVerificationRequest usiRequest = new USIVerificationRequest();
		usiRequest.setUsiCode(USI_NOT_MATCH_FIRST_NAME);
		usiRequest.setOrgCode("1234567890");
		usiRequest.setStudentBirthDate(new Date());
		usiRequest.setStudentFirstName("First");
		usiRequest.setStudentLastName("Last");

		GenericParametersMap response = sendRequest(usiRequest);

		USIVerificationResult usiResult = USIVerificationUtil.parseVerificationResult(response);
		assertEquals(USIVerificationStatus.VALID, usiResult.getUsiStatus());
		assertEquals(USIFieldStatus.NO_MATCH, usiResult.getFirstNameStatus());
		assertEquals(USIFieldStatus.MATCH, usiResult.getLastNameStatus());
		assertEquals(USIFieldStatus.MATCH, usiResult.getDateOfBirthStatus());

	}

	private void testWrongDate() throws Exception {
		USIVerificationRequest usiRequest = new USIVerificationRequest();
		usiRequest.setUsiCode(USI_NOT_MATCH_DOB);
		usiRequest.setOrgCode("1234567890");
		usiRequest.setStudentBirthDate(new Date());
		usiRequest.setStudentFirstName("First");
		usiRequest.setStudentLastName("Last");

		GenericParametersMap response = sendRequest(usiRequest);

		USIVerificationResult usiResult = USIVerificationUtil.parseVerificationResult(response);
		assertEquals(USIVerificationStatus.VALID, usiResult.getUsiStatus());
		assertEquals(USIFieldStatus.MATCH, usiResult.getFirstNameStatus());
		assertEquals(USIFieldStatus.MATCH, usiResult.getLastNameStatus());
		assertEquals(USIFieldStatus.NO_MATCH, usiResult.getDateOfBirthStatus());

	}

	private void testWrongSingleName() throws Exception {
		USIVerificationRequest usiRequest = new USIVerificationRequest();
		usiRequest.setUsiCode(USI_NOT_MATCH_SINGLE_NAME);
		usiRequest.setOrgCode("1234567890");
		usiRequest.setStudentBirthDate(new Date());
		usiRequest.setStudentFirstName("First");
		usiRequest.setStudentLastName("First");

		GenericParametersMap response = sendRequest(usiRequest);

		USIVerificationResult usiResult = USIVerificationUtil.parseVerificationResult(response);
		assertEquals(USIVerificationStatus.VALID, usiResult.getUsiStatus());
		assertEquals(USIFieldStatus.NO_MATCH, usiResult.getFirstNameStatus());
		assertEquals(USIFieldStatus.NO_MATCH, usiResult.getLastNameStatus());
		assertEquals(USIFieldStatus.MATCH, usiResult.getDateOfBirthStatus());

	}


	private GenericParametersMap sendRequest(USIVerificationRequest usiRequest) throws Exception {
		GenericParametersMap request = USIVerificationUtil.createVerificationRequestParametersMap(usiRequest, parent.getVersion());
		return parent.verifyUSI(request);
	}


	interface Parent {
		GenericParametersMap verifyUSI(GenericParametersMap var1) throws Exception;

		void authenticate() throws Exception;

		SupportedVersions getVersion();
	}
}
