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
package ish.imports;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ImportParameter implements Serializable {

	private Map<String, byte[]> data = new HashMap<>();
	private String keyCode;

	/**
	 * @return data to be imported
	 */
	public Map<String, byte[]> getData() {
		return data;
	}

	/**
	 * Set data to be imported.
	 *
	 * @param data data to be imported
	 */
	public void setData(Map<String, byte[]> data) {
		this.data = data;
	}

	/**
	 * @return import script keyCode
	 */
	public String getKeyCode() {
		return keyCode;
	}

	/**
	 * Set import script keyCode.
	 *
	 * @param keyCode import script keyCode
	 */
	public void setKeyCode(String keyCode) {
		this.keyCode = keyCode;
	}
}
