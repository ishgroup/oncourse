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

package ish.print;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 */
public enum AdditionalParameters {

	DATERANGE_FROM("dateRange_from", "From"),
	DATERANGE_TO("dateRange_to", "To"),
	LOCALDATERANGE_FROM("localdateRange_from", "From"),
	LOCALDATERANGE_TO("localdateRange_to", "To"),
	BOOLEAN_FLAG("flag", "Active enrolments"),
	PRINT_QR_CODE("print_qr_code", "Print QR code");

	String value;
	String label;

	public static List<String> NAMES = Arrays.stream(values()).map(it -> it.value).collect(Collectors.toList());

	AdditionalParameters(String value, String label) {
		this.value = value;
		this.label = label;
	}

	@Override
	public String toString() {
		return value;
	}


	public String getLabel() {
		return label;
	}
}
