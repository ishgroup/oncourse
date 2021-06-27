package ish.oncourse.server.report

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import ish.oncourse.common.ResourcesUtil
import ish.oncourse.server.cayenne.Report
import ish.report.ImportReportResult.ReportValidationError
import org.apache.cayenne.access.DataContext
import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito

import static ish.report.ImportReportResult.ReportValidationError.*
import static org.mockito.Matchers.any
import static org.mockito.Mockito.when

@CompileStatic
class ReportValidatorTest {

    
    @Test
    void testEmptyReport() throws Exception {
        DataContext context = Mockito.mock(DataContext.class)

        String reportFileName = "resources/schema/referenceData/reports/EmptyReport.jrxml"
        String reportXml = IOUtils.toString(ResourcesUtil.getResourceAsInputStream(reportFileName))

        ReportValidationError validationError = ReportValidator.valueOf(reportXml, context).validate()
        Assertions.assertEquals(TheParamReportIsEmpty, validationError)
    }

    
    @Test
    void testReportWithoutJasperReportTag() throws Exception {
        DataContext context = Mockito.mock(DataContext.class)

        String reportFileName = "resources/schema/referenceData/reports/ReportWithoutJasperTag.jrxml"
        String reportXml = IOUtils.toString(ResourcesUtil.getResourceAsInputStream(reportFileName))

        ReportValidationError validationError = ReportValidator.valueOf(reportXml, context).validate()
        Assertions.assertEquals(ImportedFileDoesNotContainJasperReportTag, validationError)
    }

    @CompileDynamic
    @Test
    void testMultipleRecords() throws Exception {
        List reports = new ArrayList<>()
        reports.add(Mockito.mock(Report.class))
        reports.add(Mockito.mock(Report.class))

        DataContext context = Mockito.mock(DataContext.class)
        when(context.select(any())).thenReturn(reports)

        String reportFileName = "resources/schema/referenceData/reports/ReportBuilderTest.jrxml"
        String reportXml = IOUtils.toString(ResourcesUtil.getResourceAsInputStream(reportFileName))

        ReportValidationError validationError = ReportValidator.valueOf(reportXml, context).validate()
        Assertions.assertEquals(MultipleReportsWithTheSameKeyCode, validationError)
    }
}