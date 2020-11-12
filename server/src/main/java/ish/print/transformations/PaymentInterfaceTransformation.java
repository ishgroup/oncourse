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
import ish.print.AdditionalParameters;
import ish.util.DateTimeUtil;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.QueryCacheStrategy;
import org.apache.cayenne.query.SortOrder;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class PaymentInterfaceTransformation extends PrintTransformation {

	private static Logger logger = LogManager.getLogger();

	public PaymentInterfaceTransformation(String inputEnity) {
		PrintTransformationField<Date> from = new PrintTransformationField<>(AdditionalParameters.DATERANGE_FROM.toString(), "from", Date.class, DateTimeUtil.getFinancialYearStart());
		PrintTransformationField<Date> to = new PrintTransformationField<>(AdditionalParameters.DATERANGE_TO.toString(), "to", Date.class, new Date());

		addFieldDefinition(from);
		addFieldDefinition(to);

		setTransformationFilter("id in $sourceIds and createdOn > $" + from.getFieldCode() + " and createdOn < $" + to.getFieldCode());
		setOutputEntityName(inputEnity);
	}

	public List<PersistentObjectI> applyTransformation(ObjectContext context, List<Long> sourceIds, Map<String, Object> additionalValues) {

		logger.info("applying transform {} on {} with {}", this, sourceIds, additionalValues);
		if (sourceIds == null || sourceIds.isEmpty()) {
			throw new IllegalArgumentException("id list is null or empty.");
		}

		List<PersistentObjectI> records = new ArrayList<>();
		for (int offset = 0; offset < sourceIds.size(); offset += getBatchSize()) {
			Map<String, Object> params = new HashMap<>(additionalValues);

			params.put("sourceIds", sourceIds.subList(offset, Math.min(offset + getBatchSize(), sourceIds.size())));

			logger.debug("params {}", params);
			logger.debug("filter {}", transformationFilter);

			ObjectSelect<PersistentObjectI> objectSelect = ObjectSelect.query(PersistentObjectI.class, getOutputEntityName())
					.cacheStrategy(QueryCacheStrategy.LOCAL_CACHE_REFRESH);

			if (!StringUtils.isEmpty(transformationFilter)) {
				objectSelect.where(ExpressionFactory.exp(transformationFilter).params(params, true));
			}

			if ("PaymentIn".equals(getOutputEntityName())) {
				objectSelect
						.orderBy("accountIn.accountCode", SortOrder.ASCENDING)
						.orderBy("accountIn.description", SortOrder.ASCENDING);
			} else if ("PaymentOut".equals(getOutputEntityName())) {
				objectSelect
						.orderBy("accountOut.accountCode", SortOrder.ASCENDING)
						.orderBy("accountOut.description", SortOrder.ASCENDING);
			}
			objectSelect.orderBy("createdOn", SortOrder.ASCENDING);

			logger.info("performing query {}", objectSelect.getWhere());
			records.addAll(objectSelect.select(context));
		}

		return records;
	}
}
