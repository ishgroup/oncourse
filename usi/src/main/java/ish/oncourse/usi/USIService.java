/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.usi;

import au.gov.usi._2018.ws.*;
import au.gov.usi._2018.ws.LocateUSIType;
import au.gov.usi._2018.ws.servicepolicy.IUSIService;
import com.sun.xml.ws.api.security.trust.client.STSIssuedTokenConfiguration;
import com.sun.xml.ws.security.trust.GenericToken;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.saml.util.SAMLUtil;
import ish.common.types.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Element;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.ws.soap.SOAPFaultException;
import java.io.StringReader;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.UUID;


public class USIService {

    private static final String ERROR_LOCATE_MESSAGE_TEMPLATE = "USI service failed during locate request, orgCode: %s, firstName: %s, lastName: %s. ";

    private static final Logger logger = LogManager.getLogger();

    private IUSIService endpoint;

    private String ishABN;

    private Map<String, Object> options;

    private USIService() {
    }

    public USIVerificationResult verifyUsi(String studentFirstName,
                                           String studentLastName,
                                           String studentBirthDate,
                                           String usiCode,
                                           String orgCode,
                                           String collegeABN,
                                           String softwareId,
                                           String collegeKey) throws ParseException {


        Date dob = DateUtils.parseDate(studentBirthDate, "yyyy-MM-dd");
        USIVerificationResult result = sendVerifyRequest(VerifyUSITypeBuilder.valueOf(studentFirstName, studentLastName, dob,usiCode,orgCode, false).build(), collegeABN, softwareId, collegeKey);
        if (needSendSingleNameRequest(studentFirstName, studentLastName, result)) {
            return sendVerifyRequest(VerifyUSITypeBuilder.valueOf(studentFirstName, studentLastName, dob,usiCode,orgCode, true).build(), collegeABN, softwareId, collegeKey);
        } else {
            return result;
        }
    }

    synchronized public LocateUSIResult locateUSI(
                                    String orgCode,
                                    String firstName,
                                    String middleName,
                                    String familyName,
                                    String gender,
                                    String dateOfBirth,
                                    String townCityOfBirth,
                                    String emailAddress,
                                    String userReference,
                                    String collegeABN,
                                    String softwareId) throws ParseException, XWSSecurityException, XMLStreamException {


        Date dob = DateUtils.parseDate(dateOfBirth, "yyyy-MM-dd");

        LocateUSIResult intRs = new LocateUSIResult();
        setActAs(collegeABN, softwareId);
        LocateUSIType extRq = LocateUSITypeBuilder.valueOf(orgCode,
                firstName,
                middleName,
                familyName,
                gender,
                dob,
                townCityOfBirth,
                emailAddress,
                userReference).build();

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
                        String warnMessage = createLocateDetailErrorMessage(orgCode, firstName, familyName);
                        logger.warn(warnMessage.concat(StringUtils.join(extRs.getErrors().getError(), ';')));

                        intRs.setResultType(ish.common.types.LocateUSIType.ERROR);
                        intRs.setError(warnMessage);
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);

                intRs.setResultType(ish.common.types.LocateUSIType.ERROR);
                intRs.setError(createLocateDetailErrorMessage(orgCode, firstName, familyName));
            }
        } else {
            String warnMessage = createLocateDetailErrorMessage(orgCode, firstName, familyName).concat("Internal request has incomplete field set.");
            logger.warn(warnMessage);

            intRs.setResultType(ish.common.types.LocateUSIType.ERROR);
            intRs.setError(warnMessage);
        }
        return intRs;
    }

    private boolean needSendSingleNameRequest(String studentFirstName,
                                              String studentLastName,
                                              USIVerificationResult result) {
        return (result.getFirstNameStatus() == USIFieldStatus.NO_MATCH || result.getLastNameStatus() == USIFieldStatus.NO_MATCH) &&
                studentFirstName.equalsIgnoreCase(studentLastName);
    }

    private USIVerificationResult sendVerifyRequest(VerifyUSIType verifyUSIType, String collegeABN,
                                                    String softwareId, String collegeKey) {
        try {

            setActAs(collegeABN, softwareId);

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
        } catch (SOAPFaultException e) {

            logger.error(String.format("Unable to verify USI code for %s %s in organisation code, college ABN: %s, college key: %s", verifyUSIType.getFirstName(),
                    verifyUSIType.getFamilyName(), collegeABN, collegeKey), e);

            String  error = StringUtils.substringBetween(e.getLocalizedMessage(), "Event Description: [", "].");
            String  advice = StringUtils.substringBetween(e.getLocalizedMessage(), "User Advice: [", "].");

            String message = "Error verifying USI.";
            if (error != null) {
                message += " " + error + " ";
            }
            if (advice != null) {
                message += " " + advice + " ";
            }
            return USIVerificationResult.valueOf(message);

        } catch (Exception e) {

            logger.error("Unable to verify USI code for {} {} in organisation code {}.", verifyUSIType.getFirstName(),
                    verifyUSIType.getFamilyName(), verifyUSIType.getOrgCode());
            logger.catching(e);
            return USIVerificationResult.valueOf("The government USI verification service is not responding. You may need to wait a little while and try again.");
        }
    }

    private void setActAs( String collegeABN,
                           String softwareId) throws XWSSecurityException, XMLStreamException {
        String uuid = UUID.randomUUID().toString();
        String actAs = String.format(
                "<v13:RelationshipToken ID=\"%s\" xmlns:v13=\"http://vanguard.business.gov.au/2016/03\"><v13:Relationship v13:Type=\"OSPfor\"><v13:Attribute v13:Name=\"SSID\" v13:Value=\"%s\"/></v13:Relationship> <v13:FirstParty v13:Scheme=\"uri://abr.gov.au/ABN\" v13:Value=\"%s\"/><v13:SecondParty v13:Scheme=\"uri://abr.gov.au/ABN\" v13:Value=\"%s\"/></v13:RelationshipToken>",
                uuid, softwareId, ishABN, collegeABN);
        XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(new StringReader(actAs));
        Element actAsElt = SAMLUtil.createSAMLAssertion(reader);

        options.put(STSIssuedTokenConfiguration.ACT_AS, new GenericToken(actAsElt));
    }

    private String createLocateDetailErrorMessage(String orgCode, String firstName, String familyName) {
        return String.format(ERROR_LOCATE_MESSAGE_TEMPLATE, orgCode, firstName,familyName);
    }

    public static USIService valueOf(IUSIService endpoint, String ishABN, Map<String, Object> options) {
        USIService usiService = new USIService();
        usiService.endpoint = endpoint;
        usiService.ishABN = ishABN;
        usiService.options = options;
        return usiService;
    }
}
