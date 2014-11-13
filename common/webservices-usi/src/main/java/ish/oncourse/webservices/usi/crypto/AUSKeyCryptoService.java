/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.usi.crypto;

import ish.oncourse.services.preference.PreferenceController;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.ws.security.WSPasswordCallback;
import org.apache.ws.security.WSSecurityException;
import org.apache.ws.security.components.crypto.CryptoBase;
import org.apache.ws.security.components.crypto.CryptoType;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.beans.factory.annotation.Autowired;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class AUSKeyCryptoService extends CryptoBase implements CallbackHandler {

	@Inject
	@Autowired
	private PreferenceController preferenceController;

	@Override
	public String getDefaultX509Identifier() throws WSSecurityException {
		return preferenceController.getServicesSecurityKey();
	}

	@Override
	public X509Certificate[] getX509Certificates(CryptoType cryptoType) throws WSSecurityException {
		try {
			return CryptoUtils.getCertificateChain(Base64.decode(preferenceController.getAuskeyCertificate()));
		} catch (IOException | CertificateException e) {
			throw new WSSecurityException("Error parsing certificate.", e);
		}
	}

	@Override
	public String getX509Identifier(X509Certificate cert) throws WSSecurityException {
		throw new UnsupportedOperationException();
	}

	@Override
	public PrivateKey getPrivateKey(X509Certificate certificate, CallbackHandler callbackHandler) throws WSSecurityException {
		throw new UnsupportedOperationException();
	}

	@Override
	public PrivateKey getPrivateKey(String identifier, String password) throws WSSecurityException {
		try {
			return CryptoUtils.decryptPrivateKey(
					Base64.decode(preferenceController.getAuskeyPrivateKey()),
					password.toCharArray(),
					Base64.decode(preferenceController.getAuskeySalt()));
		} catch (Exception e) {
			throw new WSSecurityException("Error decrypting private key.", e);
		}
	}

	@Override
	public boolean verifyTrust(X509Certificate[] certs) throws WSSecurityException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean verifyTrust(X509Certificate[] certs, boolean enableRevocation) throws WSSecurityException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean verifyTrust(PublicKey publicKey) throws WSSecurityException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
		for (Callback callback : callbacks) {
			if (callback instanceof WSPasswordCallback) {
				WSPasswordCallback passwordCallback = (WSPasswordCallback) callback;

				if (preferenceController.getServicesSecurityKey().equals(passwordCallback.getIdentifier())) {
					passwordCallback.setPassword(preferenceController.getAuskeyPassword());
				}
			}
		}
	}
}
