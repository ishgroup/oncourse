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

import ish.oncourse.cayenne.PersistentObjectI;
import org.apache.cayenne.ObjectContext;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static ish.print.AdditionalParameters.DATERANGE_TO;

public class ContactAmountOwingSqlTransformation extends PrintTransformation {
	public static final String REPORT_CODE = "ish.onCourse.DebtorsCreditorReport.asAtDate";


	public ContactAmountOwingSqlTransformation() {
		PrintTransformationField<Date> from = new PrintTransformationField<>(
				DATERANGE_TO.toString(), "As At Date", Date.class, new Date());
		addFieldDefinition(from);
		setReportCodes(REPORT_CODE);
		acceptingSourceIds  = false;
	}

	@Override
	public List<PersistentObjectI> applyTransformation(ObjectContext context, List<Long> sourceIds, Map<String, Object> additionalValues) {
		//We apply this transformation on server side class ish.oncourse.server.print.transformations.ApplyContactAmountOwingSqlTransformation
		throw new UnsupportedOperationException();
	}
}
