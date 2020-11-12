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
package ish.print.transformations;

import ish.print.AdditionalParameters;
import ish.util.DateTimeUtil;

import java.util.Date;

public class DiscountDiscountTransformation extends PrintTransformation {

	public static final String[] REPORT_CODES = {
			"ish.oncourse.discount.takeUpSummary",
			"ish.oncourse.discount.takeUp"
	};

	public DiscountDiscountTransformation() {
		PrintTransformationField<Date> from = new PrintTransformationField<>(
				AdditionalParameters.DATERANGE_FROM.toString(), "From", Date.class, DateTimeUtil.getFirstDayOfLastMonth());
		PrintTransformationField<Date> to = new PrintTransformationField<>(
				AdditionalParameters.DATERANGE_TO.toString(), "To", Date.class, DateTimeUtil.getFirstDayOfCurrentMonth());

		addFieldDefinition(from);
		addFieldDefinition(to);

		setInputEntityName("Discount");
		setOutputEntityName("Discount");

		setTransformationFilter("id in $sourceIds");

		setReportCodes(REPORT_CODES);
	}
}
