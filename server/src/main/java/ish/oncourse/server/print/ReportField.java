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
package ish.oncourse.server.print;

import ish.oncourse.server.ICayenneService;
import ish.oncourse.server.cayenne.Report;
import ish.oncourse.server.entity.mixins.MixinHelper;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import org.apache.cayenne.query.SelectQuery;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 */
public class ReportField {

	private static final Logger logger = LogManager.getLogger();

	private static final String SUBREPORT_PREFIX = "subreport.";
	private static final String DATASOURCE_PREFIX = "datasource.";
	private static final String IMAGE_PREFIX = "image.";
	private static final String PREFERENCE_PREFIX = "pref.";
	private static final String GLOBAL_PREFIX = "global.";

	private static final String CURRENT_DATASOURCE_POSTFIX = "this";
	private static final String PARENT_DATASOURCE_POSTFIX = "parent";

	private static final String CURRENT_DATASOURCE = DATASOURCE_PREFIX + CURRENT_DATASOURCE_POSTFIX;
	private static final String PARENT_DATASOURCE = DATASOURCE_PREFIX + PARENT_DATASOURCE_POSTFIX;

	private JRField field;
	private JRFieldType type;
	private ReportDataSource report;
	private ICayenneService cayenneService;

	public static JRFieldType getFieldType(JRField field) {
		var name = field.getName();

		if (name == null) {
			throw new IllegalArgumentException("Can't determine type for field without a name.");
		}

		if (name.startsWith(SUBREPORT_PREFIX)) {
			return JRFieldType.SUBREPORT;
		} else if (name.startsWith(DATASOURCE_PREFIX)) {
			return JRFieldType.DATASOURCE;
		} else if (name.startsWith(IMAGE_PREFIX)) {
			return JRFieldType.IMAGE;
		} else if (name.startsWith(PREFERENCE_PREFIX)) {
			return JRFieldType.PREFERENCE;
		} else if (name.startsWith(GLOBAL_PREFIX)) {
			return JRFieldType.GLOBAL;
		}

		return JRFieldType.RECORD;
	}

	public static DataSourceType getDataSourceType(JRField field) {
		if (!JRFieldType.DATASOURCE.equals(getFieldType(field))) {
			throw new IllegalArgumentException("Field is not datasource.");
		}

		var name = field.getName();

		if (CURRENT_DATASOURCE.equals(name)) {
			return DataSourceType.CURRENT;
		} else if (PARENT_DATASOURCE.equals(name)) {
			return DataSourceType.PARENT;
		}

		return DataSourceType.RECORD;
	}

	public ReportField(JRField field, ReportDataSource report, ICayenneService cayenneService) {
		this.field = field;
		this.report = report;
		this.cayenneService = cayenneService;
		this.type = getFieldType(field);
	}

	public JRFieldType getType() {
		return type;
	}

	/**
	 * Retrieves value of the field.
	 *
	 * @return field value
	 * @throws JRException
	 */
	public Object getFieldValue() throws JRException {
		switch (type) {
		case SUBREPORT:
			return getSubreportFieldValue();
		case DATASOURCE:
			return getDataSourceFieldValue();
		case IMAGE:
			return getImageFieldValue();
		case PREFERENCE:
			return getPreferenceFieldValue();
		case GLOBAL:
			return getGlobalFieldValue();
		case RECORD:
			return getRecordFieldValue();
		default:
			throw new IllegalStateException("Unknown field type.");
		}
	}

	private Object getSubreportFieldValue() throws JRException {
		var reportKey = StringUtils.removeStartIgnoreCase(field.getName(), SUBREPORT_PREFIX);
		logger.debug("getting the subreport with reportName: {}", reportKey);

		var query = SelectQuery.query(Report.class);
		query.andQualifier(Report.KEY_CODE.like(reportKey));

		var reports = cayenneService.getSharedContext().select(query);

		if (reports.size() == 1) {
			return report.getPrintWorker().getCompiledReport(reports.get(0));
		}

		throw new IllegalStateException("trying to print not existing sub report :" + field.getName());
	}

	private Object getImageFieldValue() {
		var keyCode = field.getName().replace(IMAGE_PREFIX, "");
		return report.getPrintWorker().getImage(keyCode);
	}

	private Object getPreferenceFieldValue() {
		var keyCode = field.getName().replace(PREFERENCE_PREFIX, "");
		return report.getPrintWorker().getPreferenceValue(keyCode);
	}

	private Object getGlobalFieldValue() {
		var keyCode = field.getName().replace(GLOBAL_PREFIX, "");
		return report.getAdditionalValueForKey(keyCode);
	}

	private Object getRecordFieldValue() throws JRException {

		// if field name matches the report entity name then return current record
		if (field.getName().equals(StringUtils.uncapitalize(report.getCurrentRecord().getClass().getSimpleName()))) {
			return report.getCurrentRecord();
		}

		try {
			var result = getValueForKey(field.getName(), report.getCurrentRecord());
			if (result == null) {
				logger.debug("Empty result for key {} --> {}", field.getName(), report.getCurrentRecord().toString());
			} else {
				logger.debug("Result for key {} --> {} {}", field.getName(), result, result.getClass().getSimpleName());
			}

			return result;
		} catch (Exception ex) {
			throw new JRException("Could not materialise value for key '" + field.getName()+"'", ex);
		}
	}

	public Object getValueForKey(String key, Object record) {
		Object result = null;
		if (key != null && key.contains(".")) {
			var intermediateKey = key.substring(0, key.indexOf('.'));
			Object intermediateObject;

			intermediateObject = MixinHelper.getProperty(record, intermediateKey);

			if (intermediateObject instanceof List) {
				throw new RuntimeException("following through multiple to many relations in reports hasn't been implemented.");
			} else if (intermediateObject != null) {
				result = getValueForKey(key.substring(key.indexOf('.') + 1), intermediateObject);
			}
		} else {
			result = MixinHelper.getProperty(record, key);
		}

		return result;
	}

	private ReportDataSource getDataSourceFieldValue() throws JRException {
		switch (getDataSourceType(field)) {
		case CURRENT:
			return getCurrentDataSource();
		case PARENT:
			return getParentDataSource();
		case RECORD:
			return getRecordDataSource();
		default:
			throw new IllegalStateException("Unknown data source type.");
		}
	}

	private ReportDataSource getCurrentDataSource() {
		return report;
	}

	private ReportDataSource getParentDataSource() {
		List<Object> oneRecordList = new ArrayList<>();
		oneRecordList.add(report.getCurrentRecord());
		return report.buildChildDataSource(oneRecordList);
	}

	private ReportDataSource getRecordDataSource() throws JRException {
		var fieldName = field.getName();
		List<Object> subRecords = null;
		var endName = fieldName.indexOf("|") > 0 ? fieldName.indexOf("|") : fieldName.length();
		var expressionName = fieldName.substring(fieldName.indexOf(".") + 1, endName);
		logger.debug("expressionName '{}' out of {}",  expressionName, this);
		try {
			// try to get the data source from the object:
			if (report.getCurrentRecord() != null) {
				var value = getValueForKey(expressionName, report.getCurrentRecord());

				if (value != null) {
					if (value instanceof List) {
						subRecords = (List<Object>) value;
					} else {
						subRecords = new Vector<>();
						subRecords.add(value);
					}
				}
			}
			if (subRecords == null) {
				subRecords = (List<Object>) report.getAdditionalValueForKey(expressionName);
			}

			if (subRecords == null) {
				// this allows to retrieve copy of current datasource and pass it to subreport without
				// interfering with main report datasource cursor position
				if ("copy".equals(expressionName)) {
					subRecords = new Vector<>(report.getRecords());
				} else {
					subRecords = new Vector<>();
				}
			}

			logger.debug("{} subRecords size .. = {}", expressionName, (subRecords == null ? "null" : "" + subRecords.size()));
		} catch (Exception e) {
			throw new JRException(e);
		}

		return report.buildChildDataSource(subRecords);
	}

	/**
	 * Report field types enumeration.
	 *
	 */
	public enum JRFieldType {
		RECORD,
		SUBREPORT,
		DATASOURCE,
		IMAGE,
		PREFERENCE,
		GLOBAL
	}

	/**
	 * Report data source types enumeration.
	 *
	 */
	public enum DataSourceType {
		CURRENT,
		PARENT,
		RECORD
	}
}
