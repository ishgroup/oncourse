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

import java.time.LocalDate;

public class StatementLineReportTransformation extends PrintTransformation {

	public static final String STATEMENT_REPORT_CODE = "ish.onCourse.statementReport";

	public StatementLineReportTransformation() {
		LocalDate now = LocalDate.now();
		LocalDate defaultStartDate = now.withDayOfMonth(1).minusMonths(1);
		LocalDate defaultEndDate = now.withDayOfMonth(1);

		PrintTransformationField<LocalDate> from = new PrintTransformationField<>(
				AdditionalParameters.LOCALDATERANGE_FROM.toString(), "From", LocalDate.class, defaultStartDate);
		PrintTransformationField<LocalDate> to = new PrintTransformationField<>(
				AdditionalParameters.LOCALDATERANGE_TO.toString(), "To", LocalDate.class, defaultEndDate);

		addFieldDefinition(from);
		addFieldDefinition(to);

		setInputEntityName("Contact");
		setOutputEntityName("Contact");

		setTransformationFilter("id in $sourceIds");

		setReportCodes(STATEMENT_REPORT_CODE);
	}
}
