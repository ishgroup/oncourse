package ish.oncourse.webservices.usi;

import au.gov.usi._2015.ws.*;
import au.gov.usi._2015.ws.servicepolicy.*;
import ish.common.types.USIVerificationStatus;

/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class TestUSIServiceEndpoint implements IUSIService {

    public static final String USI_TEST_MODE = "test.usi.endpoint";

    public static boolean useTestUSIEndpoint() {
        return "true".equalsIgnoreCase(System.getProperty(USI_TEST_MODE));
    }

    @Override
    public VerifyUSIResponseType verifyUSI(VerifyUSIType in) throws IUSIServiceVerifyUSIErrorInfoFaultFaultMessage {
        VerifyUSIResponseType result;
        if (in.getUSI().contains("_WS")) {
            result = getSingleNameResult(in);
        } else {
             result = getFirstLastNameResult(in);
        }
        try {
            Thread.sleep(3*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    private VerifyUSIResponseType getSingleNameResult(VerifyUSIType in) {
        VerifyUSIResponseType result = new VerifyUSIResponseType();

        if (in.getUSI().endsWith("_WS")) {
            result.setUSIStatus(USIVerificationStatus.INVALID.getStringValue());
        } else {
            result.setUSIStatus(USIVerificationStatus.VALID.getStringValue());
        }

        if (in.getUSI().endsWith("_WSN")) {
            result.setSingleName(MatchResultType.NO_MATCH);
        } else {
            result.setSingleName(MatchResultType.MATCH);
        }

        if (in.getUSI().endsWith("_WSD")) {
            result.setDateOfBirth(MatchResultType.NO_MATCH);
        } else {
            result.setDateOfBirth(MatchResultType.MATCH);
        }
        return result;
    }

    private VerifyUSIResponseType getFirstLastNameResult(VerifyUSIType in) {
        VerifyUSIResponseType result = new VerifyUSIResponseType();

        if (in.getUSI().endsWith("_W")) {
            result.setUSIStatus(USIVerificationStatus.INVALID.getStringValue());
        } else {
            result.setUSIStatus(USIVerificationStatus.VALID.getStringValue());
        }

        if (in.getUSI().endsWith("_WL")) {
            result.setFamilyName(MatchResultType.NO_MATCH);
        } else {
            result.setFamilyName(MatchResultType.MATCH);
        }

        if (in.getUSI().endsWith("_WF")) {
            result.setFirstName(MatchResultType.NO_MATCH);
        } else {
            result.setFirstName(MatchResultType.MATCH);
        }

        if (in.getUSI().endsWith("_WD")) {
            result.setDateOfBirth(MatchResultType.NO_MATCH);
        } else {
            result.setDateOfBirth(MatchResultType.MATCH);
        }
        return result;
    }

    @Override
    public BulkUploadResponseType bulkUpload(BulkUploadType in) throws IUSIServiceBulkUploadErrorInfoFaultFaultMessage {
        throw new UnsupportedOperationException();
    }

    @Override
    public CreateUSIResponseType createUSI(CreateUSIType in) throws IUSIServiceCreateUSIErrorInfoFaultFaultMessage {
        throw new UnsupportedOperationException();
    }

    @Override
    public BulkVerifyUSIResponseType bulkVerifyUSI(BulkVerifyUSIType in) throws IUSIServiceBulkVerifyUSIErrorInfoFaultFaultMessageSingle, IUSIServiceCreateUSIErrorInfoFaultFaultMessage {
        throw new UnsupportedOperationException();
    }

    @Override
    public BulkUploadRetrieveResponseType bulkUploadRetrieve(BulkUploadRetrieveType in) throws IUSIServiceBulkUploadRetrieveErrorInfoFaultFaultMessage {
        throw new UnsupportedOperationException();
    }

    @Override
    public LocateUSIResponseType locateUSI(LocateUSIType in) throws IUSIServiceLocateUSIErrorInfoFaultFaultMessageSingle, IUSIServiceLocateUSIErrorInfoFaultFaultMessage {
        throw new UnsupportedOperationException();
    }

    @Override
    public GetNonDvsDocumentTypesResponseType getNonDvsDocumentTypes(GetNonDvsDocumentTypesType in) throws IUSIServiceGetNonDvsDocumentTypesErrorInfoFaultFaultMessageSingle, IUSIServiceGetNonDvsDocumentTypesErrorInfoFaultFaultMessage {
        throw new UnsupportedOperationException();
    }

    @Override
    public UpdateUSIPersonalDetailsResponseType updateUSIPersonalDetails(UpdateUSIPersonalDetailsType in) throws IUSIServiceUpdateUSIPersonalDetailsErrorInfoFaultFaultMessageSingle, IUSIServiceUpdateUSIPersonalDetailsErrorInfoFaultFaultMessage {
        throw new UnsupportedOperationException();
    }

    @Override
    public UpdateUSIContactDetailsResponseType updateUSIContactDetails(UpdateUSIContactDetailsType in) throws IUSIServiceUpdateUSIContactDetailsErrorInfoFaultFaultMessageSingle, IUSIServiceUpdateUSIContactDetailsErrorInfoFaultFaultMessage {
        throw new UnsupportedOperationException();
    }
}
