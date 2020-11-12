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

public class MoneyFormatterWithThousandsDelimeter extends MoneyFormatter {
	private static final long serialVersionUID = 1L;

	protected MoneyFormatterWithThousandsDelimeter(Boolean useBracketsForNegative, boolean useThousandsDelimeter, boolean nullIfBlank) {
		super(useBracketsForNegative, true, nullIfBlank);
	}

	/**
	 * @return shared instance of money formatter with negative values rendered with minus sign
	 */
	public static MoneyFormatter getInstance() {
		return getInstance(false);
	}

	/**
	 * @param useBracketsForNegative whether returned formatter should use minus sign or brackets for negative values
	 * @return shared instance of money formatter
	 */
	public static MoneyFormatter getInstance(boolean useBracketsForNegative) {
		return getInstance(useBracketsForNegative, true);
	}

	public static MoneyFormatterWithThousandsDelimeter getInstance(boolean useBracketsForNegative, boolean useThousandsDelimiter, boolean nullIfBlank) {
		return new MoneyFormatterWithThousandsDelimeter(useBracketsForNegative, useThousandsDelimiter, nullIfBlank);
	}

	public static MoneyFormatterWithThousandsDelimeter getInstance(boolean useBracketsForNegative, boolean useThousandsDelimiter) {
		return new MoneyFormatterWithThousandsDelimeter(useBracketsForNegative, useThousandsDelimiter, false);
	}
}
