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


import ish.oncourse.server.cayenne.Report;
import ish.report.ImportReportResult.ReportValidationError;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static ish.report.ImportReportResult.ReportValidationError.*;

public class ReportValidator {

    private static final Logger logger = LogManager.getLogger();

    private String reportXml;
    private ReportValidationError error;
    private DataContext context;

    private ReportValidator() {
    }

    public ReportValidationError getError() {
        return error;
    }

    public void setError(ReportValidationError error) {
        this.error = error;
    }

    public static ReportValidator valueOf(String reportXml, DataContext context) {
        var reportValidator = new ReportValidator();
        reportValidator.reportXml = reportXml;
        reportValidator.context = context;

        return reportValidator;
    }

    public ReportValidationError validate() {

        if (reportXml.isEmpty()) {
            logger.debug("Imported report is empty");
            return TheParamReportIsEmpty;
        }

        logger.debug("report length : {}", reportXml.length());

        if (!reportXml.contains("<jasperReport xmlns=\"http://jasperreports.sourceforge.net/jasperreports\"")) {
            logger.debug("Imported file does not contain jasper report tag");
            return ImportedFileDoesNotContainJasperReportTag;
        }

        Report report;
        try {
            report = ReportBuilder.valueOf(reportXml).build();
        } catch (Exception e) {
            logger.error("Report building error", e);
            return ReportBuildingError;
        }

        var existingReports = ObjectSelect.query(Report.class)
                .where(Report.KEY_CODE.eq(report.getKeyCode()))
                .select(context);

        if (existingReports.size() > 1) {
            logger.error("We have multiple reports with the same keyCode[{}]!", report.getKeyCode());
            return MultipleReportsWithTheSameKeyCode;
        }

        return null;
    }
}
