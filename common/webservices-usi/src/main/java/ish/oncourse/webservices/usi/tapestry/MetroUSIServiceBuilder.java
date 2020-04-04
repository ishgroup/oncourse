/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.usi.tapestry;

import au.gov.abr.akm.credential.store.ABRCredential;
import au.gov.abr.akm.credential.store.ABRKeyStore;
import au.gov.abr.akm.credential.store.ABRProperties;
import au.gov.usi._2018.ws.servicepolicy.IUSIService;
//import com.sun.xml.ws.api.security.trust.client.STSIssuedTokenConfiguration;
//import com.sun.xml.ws.client.BindingProviderProperties;
//import com.sun.xml.ws.security.trust.GenericToken;
//import com.sun.xml.ws.security.trust.STSIssuedTokenFeature;
//import com.sun.xml.ws.security.trust.WSTrustVersion;
//import com.sun.xml.ws.security.trust.impl.client.DefaultSTSIssuedTokenConfiguration;
//import com.sun.xml.wss.XWSSConstants;
//import com.sun.xml.wss.XWSSecurityException;
//import com.sun.xml.wss.saml.util.SAMLUtil;
import ish.common.types.USIVerificationRequest;
import ish.common.types.USIVerificationResult;
import ish.oncourse.configuration.Configuration;
import ish.oncourse.webservices.usi.TestUSIServiceEndpoint;
import ish.oncourse.webservices.usi.USIService;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.ioc.ServiceBuilder;
import org.apache.tapestry5.ioc.ServiceResources;
import org.w3c.dom.Element;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.ws.BindingProvider;
import java.io.*;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.util.Map;
import java.util.UUID;

import static ish.oncourse.configuration.Configuration.AppProperty.CREDENTIAL_STORE;
import static ish.oncourse.configuration.Configuration.AppProperty.CREDENTIAL_STORE_PASSWORD;

/**
 * User: akoiro
 * Date: 23/8/17
 */
public class MetroUSIServiceBuilder implements ServiceBuilder<USIService> {
    private static final Logger logger = LogManager.getLogger();


    final private static String ENDPOINT = "https://softwareauthorisations.acc.ato.gov.au/R3.0/S007v1.3/service.svc";
    final private static String WSDL_LOCATION = ENDPOINT;
    final private static String STS_NAMESPACE ="http://schemas.microsoft.com/ws/2008/06/identity/securitytokenservice";
    final private static String STS_SERVICE_NAME = "SecurityTokenService";
    final private static String STS_PORT_NAME = "S007SecurityTokenServiceEndpoint";
//    final private static String STS_PROTOCOL = WSTrustVersion.WS_TRUST_13.getNamespaceURI(); //STSIssuedTokenConfiguration.PROTOCOL_13;



    public static void main(String[] args) throws ParseException {
        System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump", "true");
        System.setProperty("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump", "true");
        System.setProperty("com.sun.xml.ws.transport.http.HttpAdapter.dump", "true");
        System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dump", "true");
        System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dumpTreshold", "999999");
        System.setProperty("com.sun.xml.ws.transport.http.HttpAdapter.dumpTreshold", "999999");

        System.setProperty(CREDENTIAL_STORE.getSystemProperty(), "oncourse-usi-keystore.xml");
        System.setProperty(CREDENTIAL_STORE_PASSWORD.getSystemProperty(), "oncourse-usi-keystore.pass");

        USIService usiService = new MetroUSIServiceBuilder().buildService(null);
        USIVerificationRequest request = new USIVerificationRequest();

        request.setStudentBirthDate(DateUtils.parseDate("1988-05-02", "yyyy-MM-dd"));
        request.setStudentFirstName("Simone");
        request.setStudentLastName("Claudia Stockl");
        request.setUsiCode("DB5PW8SXU6");

        USIVerificationResult result = usiService.verifyUsi(request);
        System.out.println(result);

    }


    @Override
    public USIService buildService(ServiceResources resources) {
//        if (TestUSIServiceEndpoint.useTestUSIEndpoint()) {
//            return USIService.valueOf(new TestUSIServiceEndpoint());
//        } else {
//            try {
//                return USIService.valueOf(getEndpoint());
//            } catch (XMLStreamException e) {
//                e.printStackTrace();
//            } catch (XWSSecurityException e) {
//                e.printStackTrace();
//            }
//        }
        return null;
    }
//
//    private IUSIService getEndpoint() throws XMLStreamException, XWSSecurityException {
//        String xmlCredentialPath = Configuration.getValue(CREDENTIAL_STORE);
//        String passwordPath = Configuration.getValue(CREDENTIAL_STORE_PASSWORD);
//        PrivateKey key = null;
//        X509Certificate certificate = null;
//        try {
//            File keystoreFile = new File(xmlCredentialPath).getAbsoluteFile();
//            char[] pass = new BufferedReader(new FileReader(passwordPath)).readLine().trim().toCharArray();
//
//            ABRProperties.setSoftwareInfo("ish pty ltd", "Ish onCourse", "v1.0", "20-10-2006");
//
//
//            ABRKeyStore keyStore = new ABRKeyStore(new FileInputStream(keystoreFile));
//            String alias = keyStore.getCredentials().get(0).getId();
//            ABRCredential abrCredential = keyStore.getCredential(alias);
//
//            if (abrCredential.isReadyForRenewal()) {
//                abrCredential.renew(pass);
//            }
//            key = abrCredential.getPrivateKey(pass);
//            certificate =  abrCredential.getCertificateChain()[0];
//        } catch (Exception e) {
//            logger.catching(e);
//            logger.error("Cannot read usi certificate");
//            return null;
//        }
//
//
//        au.gov.usi._2018.ws.servicepolicy.USIService service = new au.gov.usi._2018.ws.servicepolicy.USIService();
//        DefaultSTSIssuedTokenConfiguration config = new DefaultSTSIssuedTokenConfiguration();
//        Map<String, Object> otherOptions = config.getOtherOptions();
//        config.setSignatureAlgorithm("SHA256withRSA");
//
//        String uuid  = UUID.randomUUID().toString();
//        String usiSoftwareId = "5176926411";
//        String ishABN = "74073212736";
//        String collegeABN = "53136500313";
//
//        String actAs = String.format(
//                "<v13:RelationshipToken ID=\"%s\" xmlns:v13=\"http://vanguard.business.gov.au/2016/03\"><v13:Relationship v13:Type=\"OSPfor\"><v13:Attribute v13:Name=\"SSID\" v13:Value=\"%s\"/></v13:Relationship> <v13:FirstParty v13:Scheme=\"uri://abr.gov.au/ABN\" v13:Value=\"%s\"/><v13:SecondParty v13:Scheme=\"uri://abr.gov.au/ABN\" v13:Value=\"%s\"/></v13:RelationshipToken>", uuid, usiSoftwareId, ishABN, collegeABN);
//        XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(new StringReader(actAs));
//        Element actAsElt = SAMLUtil.createSAMLAssertion(reader);
//
//        otherOptions.put(STSIssuedTokenConfiguration.ACT_AS, new GenericToken(actAsElt));
//
//        STSIssuedTokenFeature feature = new STSIssuedTokenFeature(config);
//        IUSIService endpoint = service.getWS2007FederationHttpBindingIUSIService(feature);
//
//        Map<String, Object> requestContext = ((BindingProvider)endpoint).getRequestContext();
//
//        requestContext.put(XWSSConstants.CERTIFICATE_PROPERTY, certificate);
//        requestContext.put(XWSSConstants.PRIVATEKEY_PROPERTY, key);
//
//        requestContext.put(STSIssuedTokenConfiguration.LIFE_TIME, 20*60000); // minutes*60000 (milliseconds). This will override the WSDL
//
//        otherOptions.put(STSIssuedTokenConfiguration.STS_ENDPOINT, ENDPOINT);
//        otherOptions.put(STSIssuedTokenConfiguration.STS_WSDL_LOCATION, WSDL_LOCATION);
//        otherOptions.put(STSIssuedTokenConfiguration.STS_NAMESPACE, STS_NAMESPACE);
//        otherOptions.put(STSIssuedTokenConfiguration.STS_SERVICE_NAME, STS_SERVICE_NAME);
//        otherOptions.put(STSIssuedTokenConfiguration.STS_PORT_NAME, STS_PORT_NAME);
//        config.setSTSInfo(STS_PROTOCOL, ENDPOINT, WSDL_LOCATION, STS_SERVICE_NAME, STS_PORT_NAME, STS_NAMESPACE);
//
//        requestContext.put(BindingProviderProperties.REQUEST_TIMEOUT, 60000);
//        requestContext.put(BindingProviderProperties.CONNECT_TIMEOUT, 60000);
//
//        return endpoint;
//
//    }


}
