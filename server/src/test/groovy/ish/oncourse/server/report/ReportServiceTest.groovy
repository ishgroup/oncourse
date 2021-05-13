/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.report


import groovy.transform.CompileStatic
import ish.TestWithDatabase
import ish.oncourse.cayenne.PersistentObjectI
import ish.oncourse.common.ResourceType
import ish.oncourse.common.ResourcesUtil
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.Report
import ish.oncourse.server.cayenne.Site
import ish.oncourse.server.document.DocumentService
import ish.oncourse.server.integration.PluginService
import ish.oncourse.server.print.PrintWorker
import ish.print.PrintRequest
import ish.print.PrintResult
import ish.report.ImportReportResult
import net.sf.jasperreports.engine.DefaultJasperReportsContext
import net.sf.jasperreports.engine.JRPropertiesUtil
import org.apache.cayenne.access.DataContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.junit.jupiter.api.*

import java.awt.*
import java.nio.charset.Charset
import java.util.List

import static ish.report.ImportReportResult.ReportValidationError.ReportBuildingError


@CompileStatic
class ReportServiceTest extends TestWithDatabase {
    private static final Logger logger = LogManager.getLogger()

    private DocumentService documentService
    private DataContext context

    @BeforeAll
    static void setuuup() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment()
        String[] fontNames = ge.getAvailableFontFamilyNames()
        for (String w : fontNames) {
            logger.info("available font : {}", w)
        }
        new JRRuntimeConfig().config()
    }

    @BeforeEach
    void setup() throws Exception {
        documentService = injector.getInstance(DocumentService.class)
        context = injector.getInstance(ICayenneService.class).getNewNonReplicatingContext()
    }

    @AfterEach
    void tearDown() {
        wipeTables()
    }

    @Test
    void testImportAndCompileAllReportsBasedOnManifest() throws IOException {
        logger.warn("performing testImportAndCompileAllReportsBasedOnManifest")
        def reportsList = PluginService.getPluggableResources(ResourceType.REPORT.getResourcePath(), ResourceType.REPORT.getFilePattern())
        ReportService reportService = injector.getInstance(ReportService.class)
        injector.getInstance(ICayenneService.class).getNewNonReplicatingContext()
        for (String reportFile : reportsList) {
            if (reportFile.endsWith(".jrxml")) {
                Report report = null
                try {
                    ImportReportResult importReportResult = reportService.importReport(IOUtils.toString(ResourcesUtil.getResourceAsInputStream(reportFile), Charset.defaultCharset()))
                    report = ObjectSelect.query(Report.class)
                            .where(Report.ID.eq(importReportResult.getReportId()))
                            .selectOne(context)
                } catch (Exception e) {
                    logger.catching(e)
                    Assertions.fail("could not import the report " + reportFile)
                }

                if (report != null) {
                    try {
                        ReportService.compileReport(report.getReport())
                    } catch (Exception e) {
                        logger.catching(e)
                        Assertions.fail(String.format("could not compile the report %s : %s", reportFile, e.getMessage()))
                    }
                }
            }
        }
    }

	@Test
    @Disabled
    void testImportAndCompileAllCustomReports() {
        logger.warn("performing testImportAndCompileAllCustomReports")
        File privateResourceFolder = new File("../private-resources")
        Assertions.assertTrue(privateResourceFolder.exists())
        List<File> reports = new ArrayList<>()
        for (String collegeName : privateResourceFolder.list()) {
            File templateFolder = new File(String.format("../private-resources/%s/reports", collegeName))
            if (templateFolder.exists()) {
                reports.addAll(getAllReportsFromFolder(templateFolder))
            }
        }
        ReportService reportService = injector.getInstance(ReportService.class)

        for (File reportFile : reports) {
            Report report = null
            try {
                FileReader is = new FileReader(reportFile)
                ImportReportResult importReportResult = reportService.importReport(IOUtils.toString(is))
                report = ObjectSelect.query(Report.class)
                        .where(Report.ID.eq(importReportResult.getReportId()))
                        .selectOne(context)
            } catch (Exception e) {
                Assertions.fail("could not import the report " + reportFile.getAbsolutePath())
            }

            if (report != null) {
                try {
                    ReportService.compileReport(report.getReport())
                } catch (Exception e) {
                    Assertions.fail("could not compile the report " + report.getName() + " (" + reportFile.getAbsolutePath() + ")")
                }
            }
        }
    }

    
    @Test
    void testImportUpgradeReport() {
        logger.warn("performing testImportUpgradeReport")
        ReportService reportService = injector.getInstance(ReportService.class)

        String oldReportFile = "resources/schema/referenceData/reports/testReportV1.jrxml"
        String newReportFile = "resources/schema/referenceData/reports/testReportV2.jrxml"

        // import report
        try {
            ImportReportResult importReportResult = reportService.importReport(IOUtils.toString(ResourcesUtil.getResourceAsInputStream(oldReportFile), Charset.defaultCharset()))
            Report report = ObjectSelect.query(Report.class)
                    .where(Report.ID.eq(importReportResult.getReportId()))
                    .selectOne(context)
            Assertions.assertEquals("CourseClass", report.getEntity(), "Report entity should be 'CourseClass'")
        } catch (Exception e) {
            Assertions.fail("could not import the report " + oldReportFile)
        }

        // import new version of the report
        try {
            ImportReportResult importReportResult = reportService.importReport(IOUtils.toString(ResourcesUtil.getResourceAsInputStream(newReportFile), Charset.defaultCharset()))
            Report report = ObjectSelect.query(Report.class)
                    .where(Report.ID.eq(importReportResult.getReportId()))
                    .selectOne(context)
            Assertions.assertEquals("Enrolment", report.getEntity(), "Report entity should be 'Enrolment'")
        } catch (Exception e) {
            Assertions.fail("could not import the report " + newReportFile)
        }

        // try to downgrade report by importing the old version
        ImportReportResult importReportResult = null
        try {
            importReportResult = reportService.importReport(IOUtils.toString(ResourcesUtil.getResourceAsInputStream(oldReportFile), Charset.defaultCharset()))
        } catch (IOException e) {
        }

    }

    // This test was disabled because it cannot easily be made to run on FreeBSD
    // where the closest font is actually called "Adobe Helvetica"
    
    @Disabled
    @Test
    void testCustomFontReport() throws InterruptedException, IOException {
        logger.warn("performing testCustomFontReport")
        DefaultJasperReportsContext jasperContext = DefaultJasperReportsContext.getInstance()
        JRPropertiesUtil.getInstance(jasperContext).setProperty("net.sf.jasperreports.extension.registry.factory.custom.font.families",
                "ish.oncourse.server.print.CustomFontExtensionsRegistryFactory")
        JRPropertiesUtil.getInstance(jasperContext).setProperty("net.sf.jasperreports.extension.registry.factory.fonts", "ish.oncourse.server.print.CustomFontExtensionsRegistryFactory")

        ReportService reportService = injector.getInstance(ReportService.class)
        ICayenneService cayenneService = injector.getInstance(ICayenneService.class)

        String reportFile = "resources/schema/referenceData/reports/TestFontsReport.jrxml"

        Report report = null
        try {
            ImportReportResult importReportResult = reportService.importReport(IOUtils.toString(ResourcesUtil.getResourceAsInputStream(reportFile), Charset.defaultCharset()))
            report = ObjectSelect.query(Report.class)
                    .where(Report.ID.eq(importReportResult.getReportId()))
                    .selectOne(context)

        } catch (Exception e) {
            Assertions.fail("could not import the report " + reportFile)
        }

        final PrintRequest request = new PrintRequest()
        request.setReportCode(report.getKeyCode())
        request.setEntity(report.getEntity())

        final List<PersistentObjectI> recordsToPrint = new ArrayList<>()
        Map<String, List<Long>> mapOfIds = new HashMap<>()

        Site s = new Site()
        recordsToPrint.add(s)

        mapOfIds.put("Site", Arrays.asList(1L))

        request.setIds(mapOfIds)

        PrintWorker worker = new PrintWorker(request, cayenneService, documentService) {

            @CompileStatic
            @Override
            protected List<PersistentObjectI> getRecords(Map<String, List<Long>> ids) {
                return recordsToPrint
            }

        }

        worker.run()

        while (PrintResult.ResultType.IN_PROGRESS.equals(worker.getResult().getResultType())) {
            Thread.sleep(200)
        }
        if (worker.getResult().getResultType() != PrintResult.ResultType.SUCCESS) {
            logger.warn("print has failed : {}", worker.getResult())
        }
        Assertions.assertEquals(PrintResult.ResultType.SUCCESS, worker.getResult().getResultType(), String.format("Printing failed for %s", report.getName()))
        Assertions.assertNotNull(worker.getResult().getResult(), String.format("Empty printing result for %s", report.getName()))

        FileUtils.writeByteArrayToFile(new File("build/test-data/ReportPrintServiceTest/testCustomFontReport.pdf"), worker.getResult().getResult())
    }

    
    @Test
    void testNonexisintFontReport() throws InterruptedException, IOException {
        logger.warn("performing testNonexisintFontReport")
        ReportService reportService = injector.getInstance(ReportService.class)
        ICayenneService cayenneService = injector.getInstance(ICayenneService.class)

        String reportFile = "resources/schema/referenceData/reports/TestNonexistingFontsReport.jrxml"

        Report report = null
        try {
            ImportReportResult importReportResult = reportService.importReport(IOUtils.toString(ResourcesUtil.getResourceAsInputStream(reportFile), Charset.defaultCharset()))
            report = ObjectSelect.query(Report.class)
                    .where(Report.ID.eq(importReportResult.getReportId()))
                    .selectOne(context)

        } catch (Exception e) {
            Assertions.fail("could not import the report " + reportFile)
        }

        final PrintRequest request = new PrintRequest()
        request.setReportCode(report.getKeyCode())
        request.setEntity(report.getEntity())

        final List<PersistentObjectI> recordsToPrint = new ArrayList<>()
        Map<String, List<Long>> mapOfIds = new HashMap<>()

        Site s = new Site()
        recordsToPrint.add(s)

        mapOfIds.put("Site", Arrays.asList(1L))

        request.setIds(mapOfIds)

        PrintWorker worker = new PrintWorker(request, cayenneService, documentService) {

            @Override
            protected List<PersistentObjectI> getRecords(Map<String, List<Long>> ids) {
                return recordsToPrint
            }

        }

        worker.run()

        while (PrintResult.ResultType.IN_PROGRESS.equals(worker.getResult().getResultType())) {
            Thread.sleep(200)
        }

        Assertions.assertEquals(PrintResult.ResultType.FAILED, worker.getResult().getResultType(), String.format("Printing failed for %s", report.getName()))
        Assertions.assertNotNull(worker.getResult().getError(), String.format("Empty error for %s", report.getName()))
    }

    
    @Test
    void testInvalidReport() throws InterruptedException, IOException {
        logger.warn("performing testInvalidReport")
        ReportService reportService = injector.getInstance(ReportService.class)

        String reportFile = "resources/schema/referenceData/reports/NonCompilableReport.jrxml"

        ImportReportResult importReportResult = reportService.importReport(IOUtils.toString(ResourcesUtil.getResourceAsInputStream(reportFile), Charset.defaultCharset()))
        Assertions.assertEquals(ReportBuildingError, importReportResult.getReportValidationError())
    }

    
    @Test
    void testNoDataReport() throws InterruptedException, IOException {
        logger.warn("performing testNoDataReport")
        ReportService reportService = injector.getInstance(ReportService.class)
        ICayenneService cayenneService = injector.getInstance(ICayenneService.class)

        String reportFile = "resources/schema/referenceData/reports/SimpleFakeReport.jrxml"

        Report report = null
        try {
            ImportReportResult importReportResult = reportService.importReport(IOUtils.toString(ResourcesUtil.getResourceAsInputStream(reportFile), Charset.defaultCharset()))
            report = ObjectSelect.query(Report.class)
                    .where(Report.ID.eq(importReportResult.getReportId()))
                    .selectOne(context)

        } catch (Exception e) {
            Assertions.fail("could not import the report " + reportFile)
        }

        final PrintRequest request = new PrintRequest()
        request.setReportCode(report.getKeyCode())
        request.setEntity(report.getEntity())

        final List<PersistentObjectI> recordsToPrint = new ArrayList<>()
        Map<String, List<Long>> mapOfIds = new HashMap<>()

        mapOfIds.put("Site", Arrays.asList(1L))

        request.setIds(mapOfIds)

        PrintWorker worker = new PrintWorker(request, cayenneService, documentService) {

            @Override
            protected List<PersistentObjectI> getRecords(Map<String, List<Long>> ids) {
                return recordsToPrint
            }

        }

        worker.run()

        while (PrintResult.ResultType.IN_PROGRESS.equals(worker.getResult().getResultType())) {
            Thread.sleep(200)
        }

        Assertions.assertEquals(PrintResult.ResultType.FAILED, worker.getResult().getResultType(), String.format("Printing failed for %s", report.getName()))
        Assertions.assertNotNull(worker.getResult().getError(), String.format("Empty error for %s", report.getName()))
        context.deleteObject(report)
    }

    
    @Test
    void testInvalidFieldReport() throws InterruptedException, IOException {
        logger.warn("performing testInvalidFieldReport")
        ReportService reportService = injector.getInstance(ReportService.class)
        ICayenneService cayenneService = injector.getInstance(ICayenneService.class)

        String reportFile = "resources/schema/referenceData/reports/InvalidFieldReport.jrxml"

        Report report = null
        try {
            ImportReportResult importReportResult = reportService.importReport(IOUtils.toString(ResourcesUtil.getResourceAsInputStream(reportFile), Charset.defaultCharset()))
            report = ObjectSelect.query(Report.class)
                    .where(Report.ID.eq(importReportResult.getReportId()))
                    .selectOne(context)

        } catch (Exception e) {
            Assertions.fail("could not import the report " + reportFile)
        }

        final PrintRequest request = new PrintRequest()
        request.setReportCode(report.getKeyCode())
        request.setEntity(report.getEntity())

        final List<PersistentObjectI> recordsToPrint = new ArrayList<>()
        Map<String, List<Long>> mapOfIds = new HashMap<>()

        Site s = new Site()
        recordsToPrint.add(s)

        mapOfIds.put("Site", Arrays.asList(1L))

        request.setIds(mapOfIds)

        PrintWorker worker = new PrintWorker(request, cayenneService, documentService)

        worker.run()

        while (PrintResult.ResultType.IN_PROGRESS.equals(worker.getResult().getResultType())) {
            Thread.sleep(200)
        }

        Assertions.assertEquals(PrintResult.ResultType.FAILED, worker.getResult().getResultType(), String.format("Printing failed for %s", report.getName()))
        Assertions.assertNotNull(worker.getResult().getError(), String.format("Empty error for %s", report.getName()))
    }

    static List<File> getAllReportsFromFolder(File folder) {
        ArrayList<File> result = new ArrayList<>()
        String[] listing = folder.list()
        for (String filename : listing) {
            File file = new File(folder, filename)
            if (filename.endsWith(".jrxml")) {
                result.add(file)
            } else if (file.isDirectory()) {
                result.addAll(getAllReportsFromFolder(file))
            }
        }
        return result
    }

}
