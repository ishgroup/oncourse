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
package ish.export;

import java.io.Serializable;

/**
 * This class holds the results of the XSL transformation on the server which has to be returned to the client
 *
 */
public class ExportResult implements Serializable {

	private static final long serialVersionUID = 1L;

	byte[] result = null;

	/**
	 * @return the result
	 */
	public byte[] getResult() {
		return this.result;
	}

	/**
	 * @param result the result to set
	 */
	public void setResult(byte[] result) {
		if (result != null) {
			this.result = result.clone();
		}
	}
}
