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
import ish.persistence.CommonExpressionFactory;
import ish.print.AdditionalParameters;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.QueryCacheStrategy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class CourseClassSessionTransformation extends PrintTransformation {

	public CourseClassSessionTransformation() {
		setInputEntityName("CourseClass");
		setOutputEntityName("Session");

		PrintTransformationField<Date> dateFrom = new PrintTransformationField<>(AdditionalParameters.DATERANGE_FROM.toString(), "From", Date.class, null);
		PrintTransformationField<Date> dateTo = new PrintTransformationField<>(AdditionalParameters.DATERANGE_TO.toString(), "To", Date.class, null);

		addFieldDefinition(dateFrom);
		addFieldDefinition(dateTo);
	}

	@Override
	public List<PersistentObjectI> applyTransformation(ObjectContext context, List<Long> sourceIds, Map<String, Object> additionalValues) {

		if (sourceIds == null || sourceIds.isEmpty()) {
			throw new IllegalArgumentException("id list is null or empty.");
		}

		List<PersistentObjectI> records = new ArrayList<>();

		for (int offset = 0; offset < sourceIds.size(); offset += getBatchSize()) {
			ObjectSelect<PersistentObjectI> objectSelect = ObjectSelect.query(PersistentObjectI.class, "Session")
					.where(ExpressionFactory.inExp("courseClass.id", sourceIds.subList(offset, Math.min(offset + getBatchSize(), sourceIds.size()))))
					.cacheStrategy(QueryCacheStrategy.LOCAL_CACHE_REFRESH);

			if (additionalValues.get(AdditionalParameters.DATERANGE_FROM.toString()) != null) {
				objectSelect.and(ExpressionFactory.greaterOrEqualExp("startDatetime",
						CommonExpressionFactory.previousMidnight((Date) additionalValues.get(AdditionalParameters.DATERANGE_FROM.toString()))));
			}

			if (additionalValues.get(AdditionalParameters.DATERANGE_TO.toString()) != null) {
				objectSelect.and(ExpressionFactory.lessOrEqualExp("endDatetime",
						CommonExpressionFactory.nextMidnight((Date) additionalValues.get(AdditionalParameters.DATERANGE_TO.toString()))));
			}

			records.addAll(objectSelect.select(context));
		}

		return records;
	}
}
