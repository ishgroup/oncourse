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

import net.sf.jasperreports.engine.JasperPrint;

/**
 * The super of all report classes.
 */
public abstract class AbstractReportDataSource {

	public static final String REPORT_NAME = "ReportName";

	public static final String FOLLOWING_REPORTS_PROPERTY = "ish.oncourse.reports.following";
	public static final String PRINT_OBJECT_ON_SEPARATE_PAGE_PROPERTY = "ish.oncourse.reports.isObjectOnSeparatePage";

	/** Creates a new instance of Report */
	public AbstractReportDataSource() {
		super();
	}

	/**
	 * Returns the jasper print object representing the print form filled with values.
	 *
	 * @return jasper print object ready to be printed
	 * @throws Exception exception whilst generating the report - to be caught by the printing GUI
	 */
	public JasperPrint getJPrint() throws Exception {
		return null;
	}

	@Override
	public String toString() {
		return "<Report: name: '" + getReportName() + "'>";
	}

	/**
	 * The report name.
	 *
	 * @return the report name
	 */
	public abstract String getReportName();

}
