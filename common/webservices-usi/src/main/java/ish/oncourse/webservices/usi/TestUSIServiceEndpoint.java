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
        VerifyUSIResponseType result = null;

        if (in != null && in.getUSI() != null) {
            result = new VerifyUSIResponseType();
            String usi = in.getUSI();
            switch (usi) {
                case "22A99SQAP5": {
                    result.setUSIStatus(USIVerificationStatus.INVALID.getStringValue());
                    result.setFamilyName(MatchResultType.MATCH);
                    result.setFirstName(MatchResultType.MATCH);
                    result.setDateOfBirth(MatchResultType.MATCH);
                }
                    break;
                case "22AR5X7BUG": {
                    result.setUSIStatus(USIVerificationStatus.VALID.getStringValue());
                    result.setFamilyName(MatchResultType.NO_MATCH);
                    result.setFirstName(MatchResultType.MATCH);
                    result.setDateOfBirth(MatchResultType.MATCH);
                }
                    break;
                case "22DNVQFXQ7": {
                    result.setUSIStatus(USIVerificationStatus.VALID.getStringValue());
                    result.setFamilyName(MatchResultType.MATCH);
                    result.setFirstName(MatchResultType.NO_MATCH);
                    result.setDateOfBirth(MatchResultType.MATCH);
                }
                    break;
                case "22GNBFREYT" : {
                    result.setUSIStatus(USIVerificationStatus.VALID.getStringValue());
                    result.setFamilyName(MatchResultType.MATCH);
                    result.setFirstName(MatchResultType.MATCH);
                    result.setDateOfBirth(MatchResultType.NO_MATCH);
                }
                    break;
                case "22LKRAUYEA" : {
                    result.setUSIStatus(USIVerificationStatus.VALID.getStringValue());
                    result.setFamilyName(MatchResultType.MATCH);
                    result.setFirstName(MatchResultType.MATCH);
                    result.setDateOfBirth(MatchResultType.MATCH);
                }
                    break;
                default : {
                    throw new RuntimeException("USI service is unavailable.");
                }
            }
        } else {
            throw new RuntimeException("param for verify is null or USI is null.");
        }

        try {
            Thread.sleep(3*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
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
