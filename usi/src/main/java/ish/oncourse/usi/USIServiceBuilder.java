/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.usi;

import au.gov.abr.akm.credential.store.ABRCredential;
import au.gov.abr.akm.credential.store.ABRKeyStore;
import au.gov.abr.akm.credential.store.ABRProperties;
import au.gov.usi._2018.ws.servicepolicy.IUSIService;
import com.sun.xml.ws.api.security.trust.client.STSIssuedTokenConfiguration;
import com.sun.xml.ws.client.BindingProviderProperties;
import com.sun.xml.ws.security.trust.GenericToken;
import com.sun.xml.ws.security.trust.STSIssuedTokenFeature;
import com.sun.xml.ws.security.trust.WSTrustVersion;
import com.sun.xml.ws.security.trust.impl.client.DefaultSTSIssuedTokenConfiguration;
import com.sun.xml.wss.XWSSConstants;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.saml.util.SAMLUtil;
import com.sun.xml.ws.security.trust.WSTrustVersion;
import com.sun.xml.wss.XWSSecurityException;
import ish.common.types.USIVerificationRequest;
import ish.common.types.USIVerificationResult;
import ish.oncourse.configuration.Configuration;
import ish.oncourse.usi.USIService;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
public class USIServiceBuilder {
    private static final Logger logger = LogManager.getLogger();


    final private static String ENDPOINT = "https://softwareauthorisations.acc.ato.gov.au/R3.0/S007v1.3/service.svc";
    final private static String WSDL_LOCATION = ENDPOINT;
    final private static String STS_NAMESPACE ="http://schemas.microsoft.com/ws/2008/06/identity/securitytokenservice";
    final private static String STS_SERVICE_NAME = "SecurityTokenService";
    final private static String STS_PORT_NAME = "S007SecurityTokenServiceEndpoint";
    final private static String STS_PROTOCOL = WSTrustVersion.WS_TRUST_13.getNamespaceURI(); //STSIssuedTokenConfiguration.PROTOCOL_13;

    PrivateKey key;
    X509Certificate certificate;
    String ishABN;
    Map<String, Object> otherOptions;

    public USIService buildService() throws XMLStreamException, XWSSecurityException {
        String xmlCredentialPath = Configuration.getValue(CREDENTIAL_STORE);
        String passwordPath = Configuration.getValue(CREDENTIAL_STORE_PASSWORD);

        try {
            File keystoreFile = new File(xmlCredentialPath).getAbsoluteFile();
            char[] pass = new BufferedReader(new FileReader(passwordPath)).readLine().trim().toCharArray();

            ABRProperties.setSoftwareInfo("ish pty ltd", "Ish onCourse", "v1.0", "20-10-2006");


            ABRKeyStore keyStore = new ABRKeyStore(new FileInputStream(keystoreFile));
            String alias = keyStore.getCredentials().get(0).getId();
            ABRCredential abrCredential = keyStore.getCredential(alias);

            if (abrCredential.isReadyForRenewal()) {
                abrCredential.renew(pass);
            }
            key = abrCredential.getPrivateKey(pass);
            certificate =  abrCredential.getCertificateChain()[0];
            ishABN = abrCredential.getABN();

        } catch (Exception e) {
            logger.catching(e);
            logger.error("Cannot read usi certificate");
            return null;
        }


        return USIService.valueOf(getEndpoint(), ishABN, otherOptions);

    }

    private IUSIService getEndpoint() throws XMLStreamException, XWSSecurityException {

        au.gov.usi._2018.ws.servicepolicy.USIService service = new au.gov.usi._2018.ws.servicepolicy.USIService();
        DefaultSTSIssuedTokenConfiguration config = new DefaultSTSIssuedTokenConfiguration();
        Map<String, Object> otherOptions = config.getOtherOptions();
        config.setSignatureAlgorithm("SHA256withRSA");



        STSIssuedTokenFeature feature = new STSIssuedTokenFeature(config);
        IUSIService endpoint = service.getWS2007FederationHttpBindingIUSIService(feature);
        Map<String, Object> requestContext = ((BindingProvider)endpoint).getRequestContext();

        requestContext.put(XWSSConstants.CERTIFICATE_PROPERTY, certificate);
        requestContext.put(XWSSConstants.PRIVATEKEY_PROPERTY, key);

        requestContext.put(STSIssuedTokenConfiguration.LIFE_TIME, 20*60000); // minutes*60000 (milliseconds). This will override the WSDL

        otherOptions.put(STSIssuedTokenConfiguration.STS_ENDPOINT, ENDPOINT);
        otherOptions.put(STSIssuedTokenConfiguration.STS_WSDL_LOCATION, WSDL_LOCATION);
        otherOptions.put(STSIssuedTokenConfiguration.STS_NAMESPACE, STS_NAMESPACE);
        otherOptions.put(STSIssuedTokenConfiguration.STS_SERVICE_NAME, STS_SERVICE_NAME);
        otherOptions.put(STSIssuedTokenConfiguration.STS_PORT_NAME, STS_PORT_NAME);
        config.setSTSInfo(STS_PROTOCOL, ENDPOINT, WSDL_LOCATION, STS_SERVICE_NAME, STS_PORT_NAME, STS_NAMESPACE);

        requestContext.put(BindingProviderProperties.REQUEST_TIMEOUT, 60000);
        requestContext.put(BindingProviderProperties.CONNECT_TIMEOUT, 60000);

        return endpoint;

    }


}
