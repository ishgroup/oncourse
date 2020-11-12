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

package ish.report;


public class ImportReportResult {

    private Long reportId;
    private ReportValidationError validationError;

    private ImportReportResult() {
    }

    public Long getReportId() {
        return reportId;
    }

    public ReportValidationError getReportValidationError() {
        return validationError;
    }

    public static ImportReportResult valueOf(Long reportId,  ReportValidationError validationError) {
        ImportReportResult importReportResult = new ImportReportResult();
        importReportResult.reportId = reportId;
        importReportResult.validationError = validationError;

        return importReportResult;
    }

    public enum ReportValidationError {
        ImportedFileDoesNotContainJasperReportTag,
        TheParamReportIsEmpty,
        MultipleReportsWithTheSameKeyCode,
        ReportBuildingError
    }
}
