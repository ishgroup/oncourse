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
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.QueryCacheStrategy;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.*;

/**
 * The class describes a linkage between records, allowing printing of related entities: ie. Enrolments from CourseClass, AccountTransations from Accounts.<br/>
 * Using hardcoded, predefined options isn't very robust, but allows faster and more reliable code.<br/>
 */
public class PrintTransformation implements Serializable {

	private static Logger logger = LogManager.getLogger();

	// batch size for queries due to jtds limitation in 2000 elements in IN clause
	private static final int BATCH_SIZE = 2000;

	// list of report codes to which this transformation is restricted, null here means applicable for all reports
	private List<String> reportCodes;
	// the source entity for the transformation
	private String inputEntityName;
	// the destination entity of the transformation
	private String outputEntityName;
	// the filter used to transform source->destination
	protected String transformationFilter;

	// holds detailed definitions of replacement tokens used in transformation filter
	private List<PrintTransformationField> fields = new ArrayList<>();

	//the field is true if the report can accept source ids, and returns false if the report does not need any source
	protected boolean acceptingSourceIds = true;

    public String getInputEntityName() {
        return inputEntityName;
    }

	public String getOutputEntityName() {
		return outputEntityName;
	}

	public Collection<String> getReportCodes() {
		return reportCodes != null ? Collections.unmodifiableList(reportCodes) : null;
	}

	public final int getBatchSize() {
		return BATCH_SIZE - getTransformationFilterParamsCount();
	}

	public final int getTransformationFilterParamsCount() {
		if (StringUtils.trimToNull(transformationFilter) == null) {
			return 0;
		} else {
			int count = 0;
			int lastParamCharacter = -1;
			while ((lastParamCharacter = transformationFilter.indexOf("$", lastParamCharacter >= 0 ? (lastParamCharacter + 1) : 0)) != -1) {
				count++;
			}
			return count;
		}
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

			ObjectSelect<PersistentObjectI> objectSelect = ObjectSelect.query(PersistentObjectI.class, outputEntityName)
					.cacheStrategy(QueryCacheStrategy.LOCAL_CACHE_REFRESH);

			if (!StringUtils.isEmpty(transformationFilter)) {
				objectSelect.where(ExpressionFactory.exp(transformationFilter).params(params, true));
			}

			logger.info("performing query {}", objectSelect.getWhere());
			records.addAll(objectSelect.select(context));
		}

		return records;
	}

	public void setOutputEntityName(String outputEntityName) {
		this.outputEntityName = outputEntityName;
	}

	public void setInputEntityName(String inputEntityName) {
		this.inputEntityName = inputEntityName;
	}

	public void setReportCodes(String... reportCodes) {
		this.reportCodes = new ArrayList<>(Arrays.asList(reportCodes));
	}

	public void setTransformationFilter(String transformationFilter) {
		try {
			ExpressionFactory.exp(transformationFilter);
		} catch (Exception e) {
			throw new IllegalArgumentException("cannot turn ("+transformationFilter+") into expression");
		}

		this.transformationFilter = transformationFilter;
	}

	public void addFieldDefinition(PrintTransformationField field) {
		this.fields.add(field);
	}

	public List<PrintTransformationField> getFields() {
		return fields;
	}

	@Override
	public String toString() {
		return "PrintTransformation{" +
				"inputEntityName='" + inputEntityName + '\'' +
				", outputEntityName='" + outputEntityName + '\'' +
				", transformationFilter='" + transformationFilter + '\'' +
				'}';
	}

	/**
	 * @return true if this transformation can accept source ids. If the field is false the PrintDialog does not show t
	 * the radio buttons group when we can select a source list type.
	 */
	public boolean isAcceptingSourceIds() {
		return acceptingSourceIds;
	}
}
