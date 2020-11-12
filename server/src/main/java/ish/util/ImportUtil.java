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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImportUtil {

	private static final Logger logger = LogManager.getLogger();

	public static Map<String, String> getInputFileSpec(String importScript) {
		Matcher m = Pattern.compile("(?s)\\/\\*\\((.*)\\)\\*\\/").matcher(importScript);

		try {
			if (m.find()) {
				String propertyData = m.group(1);

				Properties properties = new Properties();
				properties.load(new StringReader(propertyData));

				Map<String, String> propertyMap = new LinkedHashMap<>();

				for (String name : properties.stringPropertyNames()) {
					propertyMap.put(name, properties.getProperty(name));
				}

				return propertyMap;
			}
		} catch (IOException e) {
			logger.warn("Can't parse import file specification.", e);
		}

		return null;
	}

}
