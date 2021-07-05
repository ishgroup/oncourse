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

import ish.common.types.EnrolmentStatus;
import ish.oncourse.cayenne.PersistentObjectI;
import ish.print.AdditionalParameters;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.QueryCacheStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 */
public class CourseClassOutcomeTransformation  extends PrintTransformation {

	private static Logger logger = LogManager.getLogger();

	public CourseClassOutcomeTransformation() {
		setInputEntityName("CourseClass");
		setOutputEntityName("Outcome");

		PrintTransformationField<Boolean> flag = new PrintTransformationField<>(AdditionalParameters.BOOLEAN_FLAG.toString(), AdditionalParameters.BOOLEAN_FLAG.getLabel(), Boolean.class, Boolean.TRUE);
		addFieldDefinition(flag);

	}

	@Override
	public List<PersistentObjectI> applyTransformation(ObjectContext context, List<Long> sourceIds, Map<String, Object> additionalValues) {

		logger.info("applying transform {} on {} with {}", this, sourceIds, additionalValues);
		if (sourceIds == null || sourceIds.isEmpty()) {
			throw new IllegalArgumentException("id list is null or empty.");
		}

		List<PersistentObjectI> records = new ArrayList<>();

		for (int offset = 0; offset < sourceIds.size(); offset += getBatchSize()) {
			ObjectSelect<PersistentObjectI> objectSelect = ObjectSelect.query(PersistentObjectI.class, "Outcome")
					.where(ExpressionFactory.inExp("enrolment.courseClass.id", sourceIds.subList(offset, Math.min(offset + getBatchSize(), sourceIds.size()))))
					.cacheStrategy(QueryCacheStrategy.LOCAL_CACHE_REFRESH);

			if (Boolean.TRUE.equals(additionalValues.get(AdditionalParameters.BOOLEAN_FLAG.toString()))) {
				objectSelect.and(ExpressionFactory.inExp("enrolment.status", EnrolmentStatus.STATUSES_LEGIT));
			}

			records.addAll(objectSelect.select(context));
		}

		return records;
	}
}
