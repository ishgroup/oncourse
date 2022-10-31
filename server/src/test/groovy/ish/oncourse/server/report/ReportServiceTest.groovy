/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.report

import groovy.transform.CompileStatic
import ish.DatabaseSetup
import ish.TestWithDatabase
import ish.oncourse.cayenne.PersistentObjectI
import ish.oncourse.common.ResourceType
import ish.oncourse.common.ResourcesUtil
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.Report
import ish.oncourse.server.cayenne.Site
import ish.oncourse.server.document.DocumentService
import ish.oncourse.server.integration.PluginService
import ish.oncourse.server.preference.UserPreferenceService
import ish.oncourse.server.print.PrintWorker
import ish.oncourse.server.upgrades.DataPopulationUtils
import ish.print.PrintRequest
import ish.print.PrintResult
import net.sf.jasperreports.engine.DefaultJasperReportsContext
import net.sf.jasperreports.engine.JRException
import net.sf.jasperreports.engine.JRPropertiesUtil
import org.apache.cayenne.access.DataContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SortOrder
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.yaml.snakeyaml.Yaml

@CompileStatic
@DatabaseSetup
/**
 * Note: all reports from test resources must have keycodes, that start with test.
 */
class ReportServiceTest extends TestWithDatabase {
    private static final Logger logger = LogManager.getLogger()


    private static Collection<? extends Map<String, Object>> getPropsByPath(String path){
        InputStream resourceAsStream = null
        try {
            resourceAsStream = ResourcesUtil.getResourceAsInputStream(path)
            Yaml yaml = new Yaml();
            def loaded = yaml.load(resourceAsStream);
            if(loaded instanceof LinkedHashMap<?,?>)
                return List.of((Map<String, Object>) loaded);
            else
                return (Collection<? extends Map<String, Object>>) loaded;
        } catch (IOException ex) {
            Assertions.fail("Failed to import file: "+ path + " due to "+ex.getMessage());
        } finally {
            if(resourceAsStream != null)
                resourceAsStream.close()
        }
        return null
    }


    private static Report loadRepost(String schemeFile, DataContext context){
        try {
            def propsList = getPropsByPath(schemeFile)
            if(!propsList.isEmpty()){
                DataPopulationUtils.updateReport(context, propsList.first())
                return ObjectSelect.query(Report.class)
                        .orderBy(Report.ID.name, SortOrder.DESCENDING)
                        .selectFirst(context)
            }
        } catch (Exception e) {
            Assertions.fail("could not import the report " + schemeFile+" "+e.getMessage())
        }
    }

    private static List<Map<String, Object>> loadReports(){
        List<Map<String, Object>> resourcesList = new ArrayList<>();

        def type = ResourceType.REPORT
        String filePattern = type.getFilePattern();
        String resourcePath = type.getResourcePath();
        Set<String> filePaths = PluginService.getPluggableResources(resourcePath, filePattern);
        InputStream resourceAsStream = null
        for (def path : filePaths) {
            try {
                resourceAsStream = ResourcesUtil.getResourceAsInputStream(path)
                Yaml yaml = new Yaml();
                def loaded = yaml.load(resourceAsStream);
                if(loaded instanceof LinkedHashMap<?,?>)
                    resourcesList.add((Map<String, Object>) loaded);
                else
                    resourcesList.addAll((Collection<? extends Map<String, Object>>) loaded);
            } catch (IOException ex) {
                logger.warn("Failed to import file {}: {}", type.getDisplayName(), path);
            } finally {
                if(resourceAsStream != null)
                    resourceAsStream.close()
            }
        }
        return resourcesList;
    }
    
    @Test
    void testImportAndCompileAllReportsBasedOnManifest() throws IOException {
        logger.warn("performing testImportAndCompileAllReportsBasedOnManifest")
        def context = injector.getInstance(ICayenneService.class).getNewNonReplicatingContext()

        def reports = loadReports()
        long id = 1;
        for (def reportConfigs : reports) {
            Report report = null
            try {
                DataPopulationUtils.updateReport(context, reportConfigs)
                report = ObjectSelect.query(Report.class)
                        .where(Report.ID.eq(id++))
                        .selectOne(cayenneContext)
            } catch (Exception e) {
                logger.catching(e)
                Assertions.fail("could not import the report " + reportConfigs)
            }

            if (report != null) {
                try {
                    ReportService.compileReport(report.getReport())
                } catch (Exception e) {
                    logger.catching(e)
                    Assertions.fail(String.format("could not compile the report %s : %s", reportConfigs, e.getMessage()))
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

        for (File reportFile : reports) {
            Report report = null
            try {
                report = loadRepost(reportFile.getAbsolutePath(), cayenneContext)
                ReportService.compileReport(report.body)
            } catch (Exception e) {
                Assertions.fail("could not import the report " + reportFile.getAbsolutePath())
            }

        }
    }

    
    @Test
    void testImportUpgradeReport() {
        logger.warn("performing testImportUpgradeReport")

        String oldReportScheme = "resources/schema/referenceData/reports/testReportV1.yaml"
        String newReportScheme = "resources/schema/referenceData/reports/testReportV2.yaml"

        def context = injector.getInstance(ICayenneService.class).getNewNonReplicatingContext()
        // import report
        long id = 0
        try {
            def propsList = getPropsByPath(oldReportScheme)
            if(!propsList.isEmpty()){
                DataPopulationUtils.updateReport(context, propsList.first())
                Report report = ObjectSelect.query(Report.class)
                        .orderBy(Report.ID.name, SortOrder.DESCENDING)
                        .selectFirst(cayenneContext)
                id = report.id
                Assertions.assertEquals("CourseClass", report.getEntity(), "Report entity should be 'CourseClass'")
            }
        } catch (Exception e) {
            Assertions.fail("could not import the report " + oldReportScheme)
        }

        // import new version of the report
        try {
            def propsList = getPropsByPath(newReportScheme)
            if(!propsList.isEmpty()){
                DataPopulationUtils.updateReport(context, propsList.first())
                Report report = ObjectSelect.query(Report.class)
                        .where(Report.ID.eq(id))
                        .selectFirst(cayenneContext)
                Assertions.assertEquals("Enrolment", report.getEntity(), "Report entity should be 'Enrolment'")
            }
        } catch (Exception e) {
            Assertions.fail("could not import the report " + newReportScheme)
        }

        // try to downgrade report by importing the old version
        try {
            def propsList = getPropsByPath(oldReportScheme)
            if(!propsList.isEmpty()) {
                DataPopulationUtils.updateReport(context, propsList.first())
            }
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

        ICayenneService cayenneService = injector.getInstance(ICayenneService.class)

        String reportScheme = "resources/schema/referenceData/reports/TestFontsReport.yaml"

        Report report = loadRepost(reportScheme, cayenneService.newNonReplicatingContext)

        final PrintRequest request = new PrintRequest()
        request.setReportCode(report.getKeyCode())

        final List<PersistentObjectI> recordsToPrint = new ArrayList<>()
        Map<String, List<Long>> mapOfIds = new HashMap<>()

        Site s = new Site()
        recordsToPrint.add(s)

        mapOfIds.put("Site", Arrays.asList(1L))

        request.setIds(mapOfIds)

        PrintWorker worker = new PrintWorker(request, cayenneService, injector.getInstance(DocumentService.class), injector.getInstance(UserPreferenceService.class)) {

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
        ICayenneService cayenneService = injector.getInstance(ICayenneService.class)

        def context = cayenneService.newNonReplicatingContext

        String schemeFile = "resources/schema/referenceData/reports/TestNonexistingFontsReport.yaml"

        Report report = loadRepost(schemeFile, context)

        final PrintRequest request = new PrintRequest()
        request.setReportCode(report.getKeyCode())

        final List<PersistentObjectI> recordsToPrint = new ArrayList<>()
        Map<String, List<Long>> mapOfIds = new HashMap<>()

        Site s = new Site()
        recordsToPrint.add(s)

        mapOfIds.put("Site", Arrays.asList(1L))

        request.setIds(mapOfIds)

        PrintWorker worker = new PrintWorker(request, cayenneService, injector.getInstance(DocumentService.class), injector.getInstance(UserPreferenceService.class)) {

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
        ICayenneService cayenneService = injector.getInstance(ICayenneService.class)

        def context = cayenneService.newNonReplicatingContext

        String schemeFile = "resources/schema/referenceData/reports/NonCompilableReport.yaml"

        def report = loadRepost(schemeFile, context)
        Assertions.assertThrows(JRException, { it -> ReportService.compileReport(report.body) })
    }

    
    @Test
    void testNoDataReport() throws InterruptedException, IOException {
        logger.warn("performing testNoDataReport")
        ICayenneService cayenneService = injector.getInstance(ICayenneService.class)

        String schemeFile = "resources/schema/referenceData/reports/SimpleFakeReport.yaml"

        def context = cayenneService.newNonReplicatingContext
        Report report = loadRepost(schemeFile, context)

        final PrintRequest request = new PrintRequest()
        request.setReportCode(report.getKeyCode())

        final List<PersistentObjectI> recordsToPrint = new ArrayList<>()
        Map<String, List<Long>> mapOfIds = new HashMap<>()

        mapOfIds.put("Site", Arrays.asList(1L))

        request.setIds(mapOfIds)

        PrintWorker worker = new PrintWorker(request, cayenneService, injector.getInstance(DocumentService.class), injector.getInstance(UserPreferenceService.class)) {

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
        ICayenneService cayenneService = injector.getInstance(ICayenneService.class)

        String schemeFile = "resources/schema/referenceData/reports/InvalidFieldReport.yaml"

        Report report = loadRepost(schemeFile, cayenneService.newNonReplicatingContext)

        final PrintRequest request = new PrintRequest()
        request.setReportCode(report.getKeyCode())

        final List<PersistentObjectI> recordsToPrint = new ArrayList<>()
        Map<String, List<Long>> mapOfIds = new HashMap<>()

        Site s = new Site()
        recordsToPrint.add(s)

        mapOfIds.put("Site", Arrays.asList(1L))

        request.setIds(mapOfIds)

        PrintWorker worker = new PrintWorker(request, cayenneService, injector.getInstance(DocumentService.class), injector.getInstance(UserPreferenceService.class))

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
            if (filename.endsWith(".yaml")) {
                result.add(file)
            } else if (file.isDirectory()) {
                result.addAll(getAllReportsFromFolder(file))
            }
        }
        return result
    }

}
