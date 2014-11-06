/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.usi;

import au.gov.usi._2013.ws.servicepolicy.IUSIService;
import au.gov.usi._2013.ws.servicepolicy.USIService;
import ish.oncourse.webservices.usi.crypto.CryptoUtils;
import org.bouncycastle.util.encoders.Base64;

import javax.xml.ws.BindingProvider;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Map;

public class AUSKeyUtil {
	
	private static final String STS_ENDPOINT = "https://thirdparty.authentication.business.gov.au/R3.0/vanguard/S007v1.2/Service.svc";
	private static final String STS_NAMESPACE = "http://schemas.microsoft.com/ws/2008/06/identity/securitytokenservice";
	private static final String STS_SERVICE_NAME = "SecurityTokenService";
	private static final String STS_PORT_NAME = "S007SecurityTokenServiceEndpoint";
	private static final int STS_LIFE_TIME = 30;

	private static final int CONNECT_TIMEOUT = 60000;
	private static final int REQUEST_TIMEOUT = 60000;
	
	public static IUSIService createUSIService(String auskeyPassword, String cert, String encodedPrivateKey, String salt) {
		
		PrivateKey privateKey = getAUSKeyPrivateKey(encodedPrivateKey, auskeyPassword, salt);
		X509Certificate certificate = getAUSKeyCertificate(cert);
		
		USIService service = new USIService();
		IUSIService endpoint = service.getWS2007FederationHttpBindingIUSIService();

		Map<String, Object> requestContext = ((BindingProvider)endpoint).getRequestContext();

		requestContext.put(Constants.CERTIFICATE_PROPERTY, certificate);
		requestContext.put(Constants.PRIVATEKEY_PROPERTY, privateKey);
		requestContext.put(Constants.STS_ENDPOINT, STS_ENDPOINT);
		requestContext.put(Constants.STS_NAMESPACE, STS_NAMESPACE);
		requestContext.put(Constants.STS_WSDL_LOCATION, STS_ENDPOINT);
		requestContext.put(Constants.STS_SERVICE_NAME, STS_SERVICE_NAME);
		requestContext.put(Constants.LIFE_TIME, STS_LIFE_TIME);
		requestContext.put(Constants.STS_PORT_NAME, STS_PORT_NAME);
		requestContext.put(Constants.REQUEST_TIMEOUT, REQUEST_TIMEOUT);
		requestContext.put(Constants.CONNECT_TIMEOUT, CONNECT_TIMEOUT);
		
		return endpoint;
	}
	
	public static X509Certificate getAUSKeyCertificate(String cert) {
		try {
			X509Certificate[] certificateChain =  CryptoUtils.getCertificateChain(Base64.decode(cert));
			return certificateChain[0];
		} catch (Exception e) {
			throw new RuntimeException("Cannot load AUSKey certificate.", e);
		}
	}
	
	public static PrivateKey getAUSKeyPrivateKey(String encodedKey, String password, String salt) {
		try {
			return CryptoUtils.decryptPrivateKey(
					Base64.decode(encodedKey),
					password.toCharArray(),
					Base64.decode(salt));
		} catch (Exception e) {
			throw new RuntimeException("Cannot decrypt AUSKey private key.", e);
		}
	}
}
