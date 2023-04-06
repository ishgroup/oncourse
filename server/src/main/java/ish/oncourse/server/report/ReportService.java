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

	public ImportReportResult importReport(String reportXml) {
		var context = this.cayenneService.getNewContext();
		var validationError = ReportValidator.valueOf(reportXml, context).validate();
		if (validationError != null) {
			return  ImportReportResult.valueOf(null, validationError);
		}

		Report newReport = null;
		try {
			newReport = ReportBuilder.valueOf(reportXml).build();
		} catch (Exception e) {
			logger.error("Report building error", e);
		}
		var updatedReport = ObjectSelect.query(Report.class)
				.where(Report.KEY_CODE.eq(newReport.getKeyCode()))
				.selectOne(context);
		if (updatedReport == null) {
			logger.info("Creating new report {}", newReport.getName());
			updatedReport = context.newObject(Report.class);
			updatedReport.setAutomationStatus(AutomationStatus.ENABLED);
		} else {
			logger.info("Updating {} report", newReport.getName());
		}

		logger.debug("Report body: {} ", reportXml);

		updatedReport.setReport(reportXml);
		updatedReport.setEntity(newReport.getEntity());
		updatedReport.setIsVisible(newReport.getIsVisible());
		updatedReport.setKeyCode(newReport.getKeyCode());
		updatedReport.setName(newReport.getName());
		updatedReport.setDescription(newReport.getDescription());
		if (newReport.getSortOn() != null) {
			updatedReport.setSortOn(newReport.getSortOn());
		} else {
			updatedReport.setSortOn(null);
		}
		logger.info("{} updated", updatedReport.getName());
		logger.info("Saving report {}", updatedReport.getName());
		context.commitChanges();

		return ImportReportResult.valueOf(updatedReport.getId(), validationError);
	}

}
