/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.services;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import ish.totp.TOTPKey;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;

public class TOTPService {

	public TOTPKey generateKey(String login) {
		var totpKey = new TOTPKey();

		var gAuth = new GoogleAuthenticator();
		var key = gAuth.createCredentials();

		totpKey.setSecretKey(key.getKey());
		totpKey.setScratchCodes(key.getScratchCodes());

		// return the key as a QR code
		var url = GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL("onCourse", login, key);
		totpKey.setUrl(url);

		var qrCode = QRCode.from(url).to(ImageType.PNG).withSize(250, 250).stream().toByteArray();
		totpKey.setQrCode(qrCode);

		return totpKey;
	}

	public boolean checkToken(String secretKey, String token) {
		var gAuth = new GoogleAuthenticator();

		var intToken = Integer.parseInt(token);

		return gAuth.authorize(secretKey, intToken);
	}

	public boolean checkToken(String secretKey, Integer token) {
		return new GoogleAuthenticator().authorize(secretKey, token);
	}
}
