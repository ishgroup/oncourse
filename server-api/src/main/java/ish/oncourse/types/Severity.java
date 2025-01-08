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
package ish.oncourse.types;

import ish.oncourse.API;

/**
 * Collection of predefined {ish.oncourse.server.quality.api.QualityResultSpec} severity levels.
 * You can use these values, or just any value between 0 and 100.
 */
@API
public enum Severity {

	/**
	 * Info.
	 * Value: 0
	 *
	 * Severity between 0 and 19 are info level, of the lowest importance.
	 */
	@API
	INFO(0),

	/**
	 * Advice.
	 * Value: 20
	 *
	 * Advice is a severity for quality results which can be ignored without any significant effect.
	 */
	@API
	ADVICE(20),

	/**
	 * Warning.
	 * Value: 40
	 *
	 * A warning severity is for issues which should be fixed for smooth operation.
	 */
	@API
	WARNING(40),

	/**
	 * Error.
	 * Value: 60
	 *
	 * Error severities between 60 and 79 are for issues which should be fixed right away.
	 */
	@API
	ERROR(60),

	/**
	 * Setup.
	 * Value: 80
	 *
	 * Setup severity levels of 80 and above are reserved for issued in onCourse configuration
	 * which prevent proper use of the system.
	 */
	@API
	SETUP(80);

	private final int level;

	Severity(int level) {
		this.level = level;
	}

	/**
	 * Returns integer value for current severity level.
	 */
	public int getLevel() {
		return level;
	}
}
