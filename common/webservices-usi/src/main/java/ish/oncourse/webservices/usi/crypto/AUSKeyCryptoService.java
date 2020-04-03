/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.usi.crypto;

import au.gov.abr.akm.credential.store.ABRCredential;
import au.gov.abr.akm.credential.store.ABRKeyStore;
import au.gov.abr.akm.credential.store.ABRProperties;
import ish.oncourse.configuration.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.ws.security.WSSecurityException;
import org.apache.ws.security.components.crypto.Crypto;
import org.apache.ws.security.components.crypto.CryptoType;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import java.io.*;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import static ish.oncourse.configuration.Configuration.AppProperty.CREDENTIAL_STORE;
import static ish.oncourse.configuration.Configuration.AppProperty.CREDENTIAL_STORE_PASSWORD;

public class AUSKeyCryptoService implements Crypto, CallbackHandler {

	private static final Logger logger = LogManager.getLogger();
	private X509Certificate[] certificates;
	private  PrivateKey key;
	private String alias;

	public AUSKeyCryptoService() {

		String xmlCredentialPath = Configuration.getValue(CREDENTIAL_STORE);
		String passwordPath = Configuration.getValue(CREDENTIAL_STORE_PASSWORD);

		try {
			File keystoreFile = new File(xmlCredentialPath).getAbsoluteFile();
			char[] pass = new BufferedReader(new FileReader(passwordPath)).readLine().trim().toCharArray();

			ABRProperties.setSoftwareInfo("ish pty ltd", "Ish onCourse", "v1.0", "20-10-2006");


			ABRKeyStore keyStore = new ABRKeyStore(new FileInputStream(keystoreFile));
			alias = keyStore.getCredentials().get(0).getId();
			ABRCredential abrCredential = keyStore.getCredential(alias);

			if (abrCredential.isReadyForRenewal()) {
				abrCredential.renew(pass);
			}
			key = abrCredential.getPrivateKey(pass);
			certificates =  abrCredential.getCertificateChain();

		} catch (Exception e) {
			logger.catching(e);
			logger.error("Cannot read usi certificate");
		}
	}


	@Override
	public void handle(Callback[] callbacks) {
	}

	@Override
	public String getCryptoProvider() {
		return null;
	}

	@Override
	public void setCryptoProvider(String provider) {
	}

	@Override
	public String getDefaultX509Identifier() {
		return alias;
	}

	@Override
	public void setDefaultX509Identifier(String identifier) {
		System.out.println(identifier);
	}

	@Override
	public void setCertificateFactory(String provider, CertificateFactory certFactory) {
	}

	@Override
	public CertificateFactory getCertificateFactory() {
		return null;
	}

	@Override
	public X509Certificate loadCertificate(InputStream in) {
		return certificates[0];
	}

	@Override
	public byte[] getSKIBytesFromCert(X509Certificate cert) {
		return new byte[0];
	}

	@Override
	public byte[] getBytesFromCertificates(X509Certificate[] certs)  {
		return new byte[0];
	}

	@Override
	public X509Certificate[] getCertificatesFromBytes(byte[] data) throws WSSecurityException {
		return certificates;
	}

	@Override
	public X509Certificate[] getX509Certificates(CryptoType cryptoType)  {
		return certificates;
	}

	@Override
	public String getX509Identifier(X509Certificate cert) {
		return alias;
	}

	@Override
	public PrivateKey getPrivateKey(X509Certificate certificate, CallbackHandler callbackHandler) {
		return key;
	}

	@Override
	public PrivateKey getPrivateKey(String identifier, String password) {
		return key;
	}

	@Override
	public boolean verifyTrust(X509Certificate[] certs) {
		return true;
	}

	@Override
	public boolean verifyTrust(X509Certificate[] certs, boolean enableRevocation)  {
		return true;
	}

	@Override
	public boolean verifyTrust(PublicKey publicKey)  {
		return true;
	}
}
