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
package ish.totp;

import java.io.Serializable;
import java.util.List;

public class TOTPKey implements Serializable {

	private String secretKey;
	private String url;
	private byte[] qrCode;
	private List<Integer> scratchCodes;

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setQrCode(byte[] qrCode) {
		this.qrCode = qrCode;
	}

	public byte[] getQrCode() {
		return qrCode;
	}

	public void setScratchCodes(List<Integer> scratchCodes) {
		this.scratchCodes = scratchCodes;
	}

	public List<Integer> getScratchCodes() {
		return scratchCodes;
	}
}
