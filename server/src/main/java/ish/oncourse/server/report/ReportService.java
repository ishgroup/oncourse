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
package ish.oncourse.server.report;

import com.google.inject.Inject;
import ish.common.types.AutomationStatus;
import ish.oncourse.server.ICayenneService;
import ish.oncourse.server.cayenne.Report;
import ish.report.ImportReportResult;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;


/**
 */
public class ReportService implements IReportService {

	private static final Logger logger =  LogManager.getLogger();

	private final ICayenneService cayenneService;

	/**
	 * @param cayenneService
	 */
	@Inject
	public ReportService(ICayenneService cayenneService) {
		super();
		this.cayenneService = cayenneService;
	}

	public static byte[] compileReport(String xmlReport) throws UnsupportedEncodingException, JRException {
		var outputStream = new ByteArrayOutputStream();
		JasperCompileManager.compileReportToStream(new ByteArrayInputStream(xmlReport.getBytes(StandardCharsets.UTF_8)), outputStream);
		return outputStream.toByteArray();
	}

}
