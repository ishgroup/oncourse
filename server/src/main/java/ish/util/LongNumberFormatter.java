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

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.text.NumberFormatter;
import java.text.ParseException;

/**
 *
 */
public class LongNumberFormatter extends NumberFormatter {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger();

	private Long defaultEmptyValue = 0L;

	public static LongNumberFormatter newFormatter() {
		return new LongNumberFormatter();
	}

	public LongNumberFormatter() {
		super();
		logger.debug("init");
		setValueClass(Long.class);
		setAllowsInvalid(false);
		setCommitsOnValidEdit(true);
	}

	@Override
	public Object stringToValue(String string) throws ParseException {
		if (StringUtils.EMPTY.equals(string)) {
			return defaultEmptyValue;
		}

		try {
			return Long.valueOf(string);
		} catch (Exception e) {
			throw new ParseException(String.format("Failed to parse string '%s' to Integer.", string), -1);
		}
	}

	@Override
	public String valueToString(Object value) {
		if (value == null) {
			return "";
		}
		return value.toString();
	}

	public Long getDefaultEmptyValue() {
		return defaultEmptyValue;
	}

	public void setDefaultEmptyValue(Long defaultEmptyValue) {
		this.defaultEmptyValue = defaultEmptyValue;
	}
}
