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

package ish.util;

import java.text.ParseException;

/**
 * extension of @see javax.swing.text.DefaultFormatter. Disables overwrite mode.
 *
 */
public class DefaultFormatter extends javax.swing.text.DefaultFormatter {
	private static final long serialVersionUID = 1L;

	public DefaultFormatter() {
		super();
		setValueClass(String.class);
		setOverwriteMode(false);
		setAllowsInvalid(false);
		setCommitsOnValidEdit(true);
	}

	@Override
	public Object stringToValue(String string) throws ParseException {
		return string;
	}

	@Override
	public String valueToString(Object value) {
		if (value == null || value.toString() == null) {
			return "";
		}
		return value.toString().trim();
	}
}
