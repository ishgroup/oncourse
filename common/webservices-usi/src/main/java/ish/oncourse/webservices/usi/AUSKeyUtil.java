/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.usi;

import au.gov.abr.akm.credential.store.ABRCredential;
import au.gov.abr.akm.credential.store.ABRKeyStore;
import au.gov.usi._2013.ws.servicepolicy.IUSIService;
import au.gov.usi._2013.ws.servicepolicy.USIService;
import org.apache.log4j.Logger;

import javax.xml.ws.BindingProvider;
import java.io.File;
import java.io.FileNotFoundException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Map;

public class AUSKeyUtil {
	
	private static final Logger logger = Logger.getLogger(AUSKeyUtil.class);
	
	private static final String STS_NAMESPACE = "http://schemas.microsoft.com/ws/2008/06/identity/securitytokenservice";
	private static final String STS_SERVICE_NAME = "SecurityTokenService";
	private static final String STS_PORT_NAME = "S007SecurityTokenServiceEndpoint";
	private static final int STS_LIFE_TIME = 30;

	private static final int CONNECT_TIMEOUT = 60000;
	private static final int REQUEST_TIMEOUT = 60000;
	
	public static IUSIService createUSIService(String keyStorePath, String auskeyAlias, String auskeyPassword, String stsEndpoint) {
		PrivateKey privateKey = getAUSKeyPrivateKey(keyStorePath, auskeyAlias, auskeyPassword);
		X509Certificate certificate = getAUSKeyCertificate(keyStorePath, auskeyAlias);
		
		USIService service = new USIService();
		IUSIService endpoint = service.getWS2007FederationHttpBindingIUSIService();

		Map<String, Object> requestContext = ((BindingProvider)endpoint).getRequestContext();

		requestContext.put(Constants.CERTIFICATE_PROPERTY, certificate);
		requestContext.put(Constants.PRIVATEKEY_PROPERTY, privateKey);
		requestContext.put(Constants.STS_ENDPOINT, stsEndpoint);
		requestContext.put(Constants.STS_NAMESPACE, STS_NAMESPACE);
		requestContext.put(Constants.STS_WSDL_LOCATION, stsEndpoint);
		requestContext.put(Constants.STS_SERVICE_NAME, STS_SERVICE_NAME);
		requestContext.put(Constants.LIFE_TIME, STS_LIFE_TIME);
		requestContext.put(Constants.STS_PORT_NAME, STS_PORT_NAME);
		requestContext.put(Constants.REQUEST_TIMEOUT, REQUEST_TIMEOUT);
		requestContext.put(Constants.CONNECT_TIMEOUT, CONNECT_TIMEOUT);
		
		return endpoint;
	}

	private static X509Certificate getAUSKeyCertificate(String keyStorePath, String auskeyAlias) {
		File keyStoreFile = new File(keyStorePath);
		
		try {
			if (!keyStoreFile.exists()) {
				throw new FileNotFoundException(keyStoreFile.getCanonicalPath());
			}

			ABRKeyStore keyStore = ABRKeyStore.getInstance(keyStoreFile);

			ABRCredential abrCredential = keyStore.getCredential(auskeyAlias);
			X509Certificate[] certificate = abrCredential.getX509CertificateChain();
			return certificate[0];

		} catch (Exception e) {
			logger.error(String.format("Unable to load AUSKey certificate from keystore file: %s", keyStoreFile.getAbsolutePath()), e);
			return null;
		}
	}

	private static PrivateKey getAUSKeyPrivateKey(String keyStorePath, String auskeyAlias, String auskeyPassword) {
		File keyStoreFile = new File(keyStorePath);
		
		try {
			if (!keyStoreFile.exists()) {
				throw new FileNotFoundException(keyStoreFile.getAbsolutePath());
			}

			ABRKeyStore keyStore = ABRKeyStore.getInstance(keyStoreFile);

			return keyStore.getPrivateKey(auskeyAlias, auskeyPassword.toCharArray());
		} catch (Exception e) {
			logger.error(String.format("Unable to load AUSKey private key from keystore file: %s", keyStoreFile.getAbsolutePath()), e);
			return null;
		}
	}
}
