package ish.oncourse.webservices.usi;

import ish.common.types.USIFieldStatus;
import ish.common.types.USIVerificationRequest;
import ish.common.types.USIVerificationResult;
import ish.common.types.USIVerificationStatus;
import ish.oncourse.webservices.soap.v10.RealWSTransportTest;
import ish.oncourse.webservices.soap.v10.ReplicationFault;
import ish.oncourse.webservices.util.SupportedVersions;
import ish.oncourse.webservices.util.USIVerificationUtil;
import ish.oncourse.webservices.v10.stubs.replication.ParametersMap;
import ish.util.UrlUtil;
import ish.util.UsiUtil;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Date;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class V10USIVerificationServiceTest extends RealWSTransportTest {
    @Test
    public void testUSI() throws Exception {

        UrlUtil.createPortalUsiLink("LtLrTbSpCeSdn3Sw", DateUtils.addMonths(new Date(), 1), "kRJNAhJy69YGp5kX");

        authenticate();

        testWrongUSI();
        testWrongLastName();
        testWrongFirstName();
        testWrongDate();
        testWrongSingleName();
    }

    private void testWrongUSI() throws ReplicationFault {
        USIVerificationRequest usiRequest = new USIVerificationRequest();
        usiRequest.setUsiCode("1234567890_W");
        usiRequest.setOrgCode("1234567890");
        usiRequest.setStudentBirthDate(new Date());
        usiRequest.setStudentFirstName("First");
        usiRequest.setStudentLastName("Last");

        ParametersMap response = sendRequest(usiRequest);

        USIVerificationResult usiResult = USIVerificationUtil.parseVerificationResult(response);
        assertEquals(USIVerificationStatus.INVALID, usiResult.getUsiStatus());
        assertEquals(USIFieldStatus.MATCH, usiResult.getFirstNameStatus());
        assertEquals(USIFieldStatus.MATCH, usiResult.getLastNameStatus());
        assertEquals(USIFieldStatus.MATCH, usiResult.getDateOfBirthStatus());
    }

    private void testWrongLastName() throws Exception {
        USIVerificationRequest usiRequest = new USIVerificationRequest();
        usiRequest.setUsiCode("1234567890_WL");
        usiRequest.setOrgCode("1234567890");
        usiRequest.setStudentBirthDate(new Date());
        usiRequest.setStudentFirstName("First");
        usiRequest.setStudentLastName("Last");

        ParametersMap response = sendRequest(usiRequest);

        USIVerificationResult usiResult = USIVerificationUtil.parseVerificationResult(response);
        assertEquals(USIVerificationStatus.VALID, usiResult.getUsiStatus());
        assertEquals(USIFieldStatus.MATCH, usiResult.getFirstNameStatus());
        assertEquals(USIFieldStatus.NO_MATCH, usiResult.getLastNameStatus());
        assertEquals(USIFieldStatus.MATCH, usiResult.getDateOfBirthStatus());

    }

    private void testWrongFirstName() throws Exception {
        USIVerificationRequest usiRequest = new USIVerificationRequest();
        usiRequest.setUsiCode("1234567890_WF");
        usiRequest.setOrgCode("1234567890");
        usiRequest.setStudentBirthDate(new Date());
        usiRequest.setStudentFirstName("First");
        usiRequest.setStudentLastName("Last");

        ParametersMap response = sendRequest(usiRequest);

        USIVerificationResult usiResult = USIVerificationUtil.parseVerificationResult(response);
        assertEquals(USIVerificationStatus.VALID, usiResult.getUsiStatus());
        assertEquals(USIFieldStatus.NO_MATCH, usiResult.getFirstNameStatus());
        assertEquals(USIFieldStatus.MATCH, usiResult.getLastNameStatus());
        assertEquals(USIFieldStatus.MATCH, usiResult.getDateOfBirthStatus());

    }

    private void testWrongDate() throws Exception {
        USIVerificationRequest usiRequest = new USIVerificationRequest();
        usiRequest.setUsiCode("1234567890_WD");
        usiRequest.setOrgCode("1234567890");
        usiRequest.setStudentBirthDate(new Date());
        usiRequest.setStudentFirstName("First");
        usiRequest.setStudentLastName("Last");

        ParametersMap response = sendRequest(usiRequest);

        USIVerificationResult usiResult = USIVerificationUtil.parseVerificationResult(response);
        assertEquals(USIVerificationStatus.VALID, usiResult.getUsiStatus());
        assertEquals(USIFieldStatus.MATCH, usiResult.getFirstNameStatus());
        assertEquals(USIFieldStatus.MATCH, usiResult.getLastNameStatus());
        assertEquals(USIFieldStatus.NO_MATCH, usiResult.getDateOfBirthStatus());

    }

    private void testWrongSingleName() throws Exception {
        USIVerificationRequest usiRequest = new USIVerificationRequest();
        usiRequest.setUsiCode("1234567890_WSN");
        usiRequest.setOrgCode("1234567890");
        usiRequest.setStudentBirthDate(new Date());
        usiRequest.setStudentFirstName("First");
        usiRequest.setStudentLastName("First");

        ParametersMap response = sendRequest(usiRequest);

        USIVerificationResult usiResult = USIVerificationUtil.parseVerificationResult(response);
        assertEquals(USIVerificationStatus.VALID, usiResult.getUsiStatus());
        assertEquals(USIFieldStatus.NO_MATCH, usiResult.getFirstNameStatus());
        assertEquals(USIFieldStatus.NO_MATCH, usiResult.getLastNameStatus());
        assertEquals(USIFieldStatus.MATCH, usiResult.getDateOfBirthStatus());

    }


    private ParametersMap sendRequest(USIVerificationRequest usiRequest) throws ReplicationFault {
        ParametersMap request = (ParametersMap) USIVerificationUtil.createVerificationRequestParametersMap(usiRequest, SupportedVersions.V10);
        return getPaymentPortType().verifyUSI(request);
    }
}
