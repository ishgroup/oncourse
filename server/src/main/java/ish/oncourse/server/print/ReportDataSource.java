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

import ish.math.context.MoneyContext;
import ish.oncourse.server.cayenne.Report;
import net.sf.jasperreports.engine.*;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SortOrder;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * ReportDataSource is a container for jaspereport, list of records to print and other parameters.
 */
public class ReportDataSource extends AbstractReportDataSource implements JRRewindableDataSource {
	private static final Logger logger = LogManager.getLogger();

	private PrintWorker printWorker;
	private MoneyContext moneyContext;

	private Report report;
	private int index = -1;
	private List<?> records;
	private Map<String, Object> parameters;


	public ReportDataSource(PrintWorker printWorker, MoneyContext moneyContext, Report report, List<?> records) {
		super();
		this.parameters = new HashMap<>();
		this.report = report;
		this.records = records;
		this.printWorker = printWorker;
		this.moneyContext = moneyContext;
	}

	/**
	 * parent printworker object. ReportDataSource requires it for sourcing the printrequest and compiled report/image/preference cache.
	 */
	public PrintWorker getPrintWorker() {
		return printWorker;
	}

	/**
	 *
	 * @return Report to be printed
	 */
	public Report getReport() {
		return this.report;
	}

	@Override
	public JasperPrint getJPrint() throws Exception {
		logger.debug("Current Currency = {}", moneyContext.getCurrencyCode());

		this.parameters = BuildJasperReportParams.valueOf(getReportName(), moneyContext.getLocale()).get();
		if (printWorker != null && printWorker.getPrintRequest() != null && printWorker.getPrintRequest().getBindings() != null) {
			this.parameters.putAll(printWorker.getPrintRequest().getBindings());
		}
		return JasperFillManager.fillReport(printWorker.getCompiledReport(report), this.parameters, this);
	}

	@Override
	public String getReportName() {
		return this.report.getName();
	}

	/**
	 * Returns the list of records to print in this report.
	 *
	 * @return list of records
	 */
	public List<?> getRecords() {
		return this.records;
	}

	/**
	 * iterates throught the records to print.
	 *
	 * @return true if there is a next element.
	 */
	public boolean next() {
		if (getRecords() == null) {
			return false;
		}
		this.index++;
		return this.index < getRecords().size();
	}

	/**
	 *
	 * @param jRField to evaluate
	 * @return evaluated value
	 */
	public Object getFieldValue(JRField jRField) throws JRException {
		return new ReportField(jRField, this, printWorker.getCayenneService()).getFieldValue();
	}

	@Override
	public String toString() {
		return "<Report: name: '" + this.report.getName() + "' key: '" + this.report.getKeyCode() + ">";
	}

	@Override
	public void moveFirst() {
		this.index = -1;
	}

	/**
	 * produces a cayenne ordering out of a string.
	 * @param sortOn list of propertyKeys separated by ';'. Property key can be prefixed with '-' indicating DESC ordering.
	 * @return List of orderings
	 */
	public static List<Ordering> getOrderingForSortOn(String sortOn) {
		List<Ordering> orderings = new ArrayList<>();
		if (StringUtils.isEmpty(sortOn)) {
			return orderings;
		}

		var pattern = Pattern.compile(";");
		var keys = pattern.split(sortOn);
		// sort, this is required for some reports to work
		for (var key : keys) {
			if (key.startsWith("-")) {
				var order = new Ordering(key.replaceFirst("-", ""), SortOrder.DESCENDING_INSENSITIVE);
				order.setPathExceptionSupressed(true);
				orderings.add(order);
			} else {
				var order = new Ordering(key, SortOrder.ASCENDING_INSENSITIVE);
				order.setPathExceptionSupressed(true);
				orderings.add(order);
			}
		}

		return orderings;
	}

	public ReportDataSource buildChildDataSource(List<?> records) {
		return new ReportDataSource(printWorker, moneyContext, report, records);
	}

	public Object getAdditionalValueForKey(String key) {
		return printWorker.getPrintRequest().getValueForKey(key);
	}


	public Object getCurrentRecord() {
		return getRecords().get(index);
	}
}
