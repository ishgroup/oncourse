package ish.oncourse.webservices.usi;

import au.gov.usi._2018.ws.*;
import au.gov.usi._2018.ws.servicepolicy.*;
import ish.common.types.USIVerificationStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class TestUSIServiceEndpoint implements IUSIService {

	private static final Logger logger = LogManager.getLogger();

	public static final String USI_INVALID = "SYWQ7D4YBT";
	public static final String USI_NOT_MATCH_LAST_NAME = "ZTCP6RPU6M";
	public static final String USI_NOT_MATCH_FIRST_NAME = "KLP2ZP6NCW";
	public static final String USI_NOT_MATCH_DOB = "G2RN4G89SD";
	public static final String USI_VALID = "PCVA64XU3D";

	public static final String USI_NOT_MATCH_SINGLE_NAME = "CVPZXK7N9H";
	public static final String USI_NOT_MATCH_SINGLE_DOB = "QBK9UZYQNJ";
	public static final String USI_VALID_SINGLE = "W2NBT2FW66";
	public static final String USI_INVALID_SINGLE = "TSXEDK8HVP";

	public static final String NO_MATCH = "NO_MATCH";

	public static final String USI_TEST_MODE = "test.usi.endpoint";

    public static boolean useTestUSIEndpoint() {
		return Boolean.valueOf(System.getProperty(USI_TEST_MODE));
	}

	@Override
	public VerifyUSIResponseType verifyUSI(VerifyUSIType in) throws IUSIServiceVerifyUSIErrorInfoFaultFaultMessage {
		VerifyUSIResponseType result = new VerifyUSIResponseType();
		switch (in.getUSI()) {
			case USI_INVALID:
				result.setUSIStatus(USIVerificationStatus.INVALID.getStringValue());
				result.setFamilyName(MatchResultType.MATCH);
				result.setFirstName(MatchResultType.MATCH);
				result.setDateOfBirth(MatchResultType.MATCH);
				break;
			case USI_NOT_MATCH_LAST_NAME:
				result.setUSIStatus(USIVerificationStatus.VALID.getStringValue());
				result.setFamilyName(MatchResultType.NO_MATCH);
				result.setFirstName(MatchResultType.MATCH);
				result.setDateOfBirth(MatchResultType.MATCH);
				break;
			case USI_NOT_MATCH_FIRST_NAME:
				result.setUSIStatus(USIVerificationStatus.VALID.getStringValue());
				result.setFamilyName(MatchResultType.MATCH);
				result.setFirstName(MatchResultType.NO_MATCH);
				result.setDateOfBirth(MatchResultType.MATCH);
				break;
			case USI_NOT_MATCH_DOB:
				result.setUSIStatus(USIVerificationStatus.VALID.getStringValue());
				result.setFamilyName(MatchResultType.MATCH);
				result.setFirstName(MatchResultType.MATCH);
				result.setDateOfBirth(MatchResultType.NO_MATCH);
				break;
			case USI_VALID:
				result.setUSIStatus(USIVerificationStatus.VALID.getStringValue());
				result.setFamilyName(MatchResultType.MATCH);
				result.setFirstName(MatchResultType.MATCH);
				result.setDateOfBirth(MatchResultType.MATCH);
				break;
			case USI_INVALID_SINGLE:
				result.setUSIStatus(USIVerificationStatus.INVALID.getStringValue());
				result.setSingleName(MatchResultType.MATCH);
				result.setDateOfBirth(MatchResultType.MATCH);
				break;
			case USI_NOT_MATCH_SINGLE_NAME:
				result.setUSIStatus(USIVerificationStatus.VALID.getStringValue());
				result.setSingleName(MatchResultType.NO_MATCH);
				result.setDateOfBirth(MatchResultType.MATCH);
				break;
			case USI_NOT_MATCH_SINGLE_DOB:
				result.setUSIStatus(USIVerificationStatus.VALID.getStringValue());
				result.setSingleName(MatchResultType.MATCH);
				result.setDateOfBirth(MatchResultType.NO_MATCH);
				break;
			case USI_VALID_SINGLE:
				result.setUSIStatus(USIVerificationStatus.VALID.getStringValue());
				result.setSingleName(MatchResultType.MATCH);
				result.setDateOfBirth(MatchResultType.MATCH);
				break;
			default:
				throw new RuntimeException("USI service is unavailable.");
		}

		try {
			Thread.sleep(3 * 1000);
		} catch (InterruptedException e) {
			logger.debug(e);
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
	public GetCountriesResponseType getCountries(GetCountriesType in) throws IUSIServiceGetCountriesErrorInfoFaultFaultMessageSingle, IUSIServiceGetCountriesErrorInfoFaultFaultMessage {
		throw new UnsupportedOperationException();
	}

	@Override
	public BulkUploadRetrieveResponseType bulkUploadRetrieve(BulkUploadRetrieveType in) throws IUSIServiceBulkUploadRetrieveErrorInfoFaultFaultMessage {
		throw new UnsupportedOperationException();
	}

	@Override
	public LocateUSIResponseType locateUSI(LocateUSIType in) throws IUSIServiceLocateUSIErrorInfoFaultFaultMessageSingle, IUSIServiceLocateUSIErrorInfoFaultFaultMessage {
    	LocateUSIResponseType response = null;

    	PersonalDetailsLocateType personal = in. getPersonalDetails();
		if (personal.getFirstName() != null && personal.getFamilyName() != null && personal.getGender() != null && personal.getDateOfBirth() != null) {
			if ("Reiel".equals(personal.getFirstName()) && "Draz".equals(personal.getFamilyName()) && "M".equals(personal.getGender()) && dateToXML(new Date(2001, 01, 01)).equals(personal.getDateOfBirth())) {
				if ("Melbourne".equals(personal.getTownCityOfBirth())) {
					response = createResponse(LocateResultType.EXACT, "HEY5DBYZHX", "The USI is HEY5DBYZHX for Reiel Draz. This USI account\nis currently Activated.");
				} else {
					response = createResponse(LocateResultType.MULTIPLE_EXACT, NO_MATCH, NO_MATCH);
				}
			} else {
				response = createResponse(LocateResultType.NO_MATCH, NO_MATCH, NO_MATCH);
			}
		} else {
			response.setErrors(new LocateUSIResponseType.Errors());
		}
		pause(5);
		return response;
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

	private LocateUSIResponseType createResponse(LocateResultType resultType, String usi, String contactDetailMessage) {
		LocateUSIResponseType response = new LocateUSIResponseType();
		response.setResult(LocateResultType.NO_MATCH);
		response.setUSI(usi);
		response.setContactDetailsMessage(contactDetailMessage);
		return response;
	}

	private XMLGregorianCalendar dateToXML(Date date) {
		try {
			GregorianCalendar cal = new GregorianCalendar();
			cal.setTime(date);
			XMLGregorianCalendar xmlDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
			xmlDate.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
			return xmlDate;
		} catch (DatatypeConfigurationException ex) {
			throw new IllegalArgumentException(ex);
		}
	}

	private void pause(int seconds) {
		try {
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
