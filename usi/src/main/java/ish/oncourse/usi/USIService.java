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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Element;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.StringReader;
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
                                           Date studentBirthDate,
                                           String usiCode,
                                           String orgCode,
                                           String collegeABN,
                                           String softwareId) {



        USIVerificationResult result = sendVerifyRequest(VerifyUSITypeBuilder.valueOf(studentFirstName, studentLastName, studentBirthDate,usiCode,orgCode, false).build(), collegeABN, softwareId);
        if (needSendSingleNameRequest(studentFirstName, studentLastName, result)) {
            return sendVerifyRequest(VerifyUSITypeBuilder.valueOf(studentFirstName, studentLastName, studentBirthDate,usiCode,orgCode, true).build(), collegeABN, softwareId );
        } else {
            return result;
        }
    }

    public LocateUSIResult locateUSI(LocateUSIRequest intRq) {
        LocateUSIResult intRs = new LocateUSIResult();

        LocateUSIType extRq = LocateUSITypeBuilder.valueOf(intRq).build();

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
                        String warnMessage = createLocateDetailErrorMessage(intRq);
                        logger.warn(warnMessage.concat(StringUtils.join(extRs.getErrors().getError(), ';')));

                        intRs.setResultType(ish.common.types.LocateUSIType.ERROR);
                        intRs.setError(warnMessage);
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);

                intRs.setResultType(ish.common.types.LocateUSIType.ERROR);
                intRs.setError(createLocateDetailErrorMessage(intRq));
            }
        } else {
            String warnMessage = createLocateDetailErrorMessage(intRq).concat("Internal request has incomplete field set.");
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
                                                    String softwareId) {
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
        } catch (Exception e) {
            logger.error("Unable to verify USI code for {} {} in organisation code {}.", verifyUSIType.getFirstName(),
                    verifyUSIType.getFamilyName(), verifyUSIType.getOrgCode(), e);

            return USIVerificationResult.valueOf("Error verifying USI.");
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

    private String createLocateDetailErrorMessage(LocateUSIRequest intRq) {
        return String.format(ERROR_LOCATE_MESSAGE_TEMPLATE, intRq.getOrgCode(), intRq.getFirstName(), intRq.getFamilyName());
    }

    public static USIService valueOf(IUSIService endpoint, String ishABN, Map<String, Object> options) {
        USIService usiService = new USIService();
        usiService.endpoint = endpoint;
        usiService.ishABN = ishABN;
        usiService.options = options;
        return usiService;
    }
}
