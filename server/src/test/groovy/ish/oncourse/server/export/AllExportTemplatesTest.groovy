/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.server.export

import groovy.transform.CompileStatic
import ish.DatabaseSetup
import ish.TestWithDatabase
import ish.TestWithDatabaseExtension
import ish.export.ExportParameter
import ish.export.ExportResult
import ish.oncourse.common.ResourceProperty
import ish.oncourse.common.ResourceType
import ish.oncourse.common.ResourcesUtil
import ish.oncourse.server.integration.PluginService
import ish.oncourse.server.upgrades.DataPopulation
import ish.oncourse.server.upgrades.DataPopulationUtils
import org.apache.cayenne.exp.ExpressionFactory
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.dbunit.database.IDatabaseConnection
import org.dbunit.dataset.ReplacementDataSet
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.dbunit.operation.DatabaseOperation
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.yaml.snakeyaml.Yaml

import java.time.LocalDate

/**
 * This test depends on your computer locale (for example, it can fail with money format, pm/PM, am/AM and so on)
 * It checks only printing of exports for all of them, which are into /exports folder of server resources
 */
@CompileStatic
@DatabaseSetup(value = "ish/oncourse/server/export/initialDataSet.xml")
class AllExportTemplatesTest extends TestWithDatabase {
    private static TimeZone previousTimezone

    private static final String PAYSLIP_MICROPAY_KEYCODE = "ish.onCourse.payslipMicropay.csv"
    private static final String SS_BULK_UPLOAD_KEYCODE = "ish.onCourse.ssBulkUpload.csv"
    private static final String LINE_SEPARATOR = StringUtils.LF

    private static final Logger logger = LogManager.getLogger()

    // We need to save previous data set to delete data related to it only, because in other case we will catch exception with foreign keys
    private ReplacementDataSet lastDataset

    // Are needed to clean additional data, received from direct SQL queries into statements
    private List<String> additionalTablesToClean = new ArrayList<>()
    // Additional required parameters for scripts
    private static Map<String, Map<String, Object>> additionalVariables = [
            "ish.oncourse.agedDebtorCsvExport.csv": ["atDate": LocalDate.of(2013, 03, 04) as Object, "detail": null] as Map<String, Object>
    ]

    void setup(String testDataFile) {
        try {
            InputStream st = AllExportTemplatesTest.class.getClassLoader().getResourceAsStream("ish/oncourse/server/export/" + testDataFile)
            FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder()
            builder.setColumnSensing(true)
            FlatXmlDataSet dataSet = builder.build(st)

            ReplacementDataSet rDataSet = new ReplacementDataSet(dataSet)
            rDataSet.addReplacementObject("[null]", null)

            IDatabaseConnection testDatabaseConnection = (dbExtension as TestWithDatabaseExtension).getTestDatabaseConnection()
            def statement = testDatabaseConnection.connection.createStatement()

            if (lastDataset != null) {
                if (!additionalTablesToClean.empty) {
                    additionalTablesToClean.each { statement.execute("DELETE from ${it}") }
                    statement.connection.commit()
                    additionalTablesToClean = new ArrayList<>()
                }
                try {
                    DatabaseOperation.DELETE_ALL.execute(testDatabaseConnection, lastDataset)
                } catch (Exception e) {
                    Assertions.fail(e.getMessage() + " : " + testDataFile)
                }

            }

            DatabaseOperation.CLEAN_INSERT.execute(testDatabaseConnection, rDataSet)
            testDatabaseConnection.connection.commit()

            /* We need this code because we cannot create xml data node with `Lead` name and cannot use
               Lead name into MySQL queries because it is reserved word */
            if (testDataFile.equals("leadDataSet.xml")) {
                statement.execute("insert into `Lead` (id, studentCount, createdOn, modifiedOn, contactId, studentNotes, status, assignedToUserId) values (1, 8, '2012-05-10 00:00:00', '2012-05-10 00:00:00', 1, 'First student notes', 1, 1)")
                statement.execute("insert into `Lead` (id, studentCount, createdOn, modifiedOn, contactId, studentNotes, status, assignedToUserId) values (2, 3, '2012-05-10 00:00:00', '2012-05-10 00:00:00', 2, 'Second student notes', 0, 1)")
                statement.execute("insert into LeadItem (id, leadId, createdOn, modifiedOn, courseId, productId) values (1, 1, '2015-01-24 18:00:00', '2015-01-24 18:00:00', 1, NULL)")
                statement.execute("insert into LeadItem (id, leadId, createdOn, modifiedOn, courseId, productId) values (2, 2, '2015-01-24 18:00:00', '2015-01-24 18:00:00', 2, 1)")
                statement.execute("insert into LeadItem (id, leadId, createdOn, modifiedOn, courseId, productId) values (3, 1, '2015-01-24 18:00:00', '2015-01-24 18:00:00', 1, NULL)")
                statement.connection.commit()

                additionalTablesToClean = List.of("LeadItem", "Lead")
            }

            lastDataset = rDataSet

            DataPopulation dataPopulation = injector.getInstance(DataPopulation.class)
            dataPopulation.run()
            cayenneContext.commitChanges()

        } catch (Exception e) {
            Assertions.fail(e.getMessage() + " : " + testDataFile)
        }
    }

    @Test
    void testAllExports() {
        previousTimezone = TimeZone.getDefault()
        TimeZone.setDefault(null)

        def pathsList = PluginService.getPluggableResources(ResourceType.EXPORT.getResourcePath(), ResourceType.EXPORT.getFilePattern())
        List<Map<String, Object>> resourcesList = new ArrayList<>()

        int counter = 0;

        for (String propFile : pathsList) {
            def resourceAsStream = ResourcesUtil.getResourceAsInputStream(propFile)
            Yaml yaml = new Yaml()
            def sources = yaml.load(resourceAsStream)
            resourcesList.addAll(sources as List<Map<String, Object>>)

            for (Map<String, Object> props : resourcesList) {
                DataPopulationUtils.updateExport(cayenneContext, props)
                String keyCode = (String) props.get(ResourceProperty.KEY_CODE.getDisplayName())
                String entityName = ((String) props.get(ResourceProperty.ENTITY_CLASS.getDisplayName()))
                String outputExtention = keyCode.split("\\.").last()
                String dataSet = keyCode.split("\\.")[2] + "DataSet.xml"
                String output = keyCode.split("\\.")[2] + "SampleOutput." + outputExtention

                def temp = System.currentTimeMillis()
                testExport(keyCode, entityName, dataSet, output)
                def now = System.currentTimeMillis()
                logger.warn("TIME: " + (now - temp))

                counter++
            }
        }
        TimeZone.setDefault(previousTimezone)
    }


    void testExport(String keyCode, String entityName, String dataSet, String output) throws Exception {
        logger.warn("Testing: " + keyCode + " " + dataSet + " " + output)
        setup(dataSet)

        // exclude exports for Script entity - IshTestCase updates scripts from resources after table wipe
        if (entityName != "Script") {
            ExportParameter param = new ExportParameter()

            param.setEntity(entityName)
            param.setXslKeyCode(keyCode)
            param.setExpression(ExpressionFactory.expTrue())

            ExportService export = injector.getInstance(ExportService.class)

            ExportResult result = export.export(param, additionalVariables.get(keyCode))

            def inputStream = AllExportTemplatesTest.class.getClassLoader().getResourceAsStream("ish/oncourse/server/export/output/" + output)

            byte[] sampleExport = IOUtils.toByteArray(inputStream)

            String sampleExportString = StringUtils.replace(new String(sampleExport), StringUtils.CR, StringUtils.EMPTY)
            String resultExportString = StringUtils.replace(new String(result.getResult()), StringUtils.CR, StringUtils.EMPTY)

            String[] sampleExportSplit = sampleExportString.split(LINE_SEPARATOR)
            String[] resultExportSplit = resultExportString.split(LINE_SEPARATOR)

            Assertions.assertEquals(sampleExportSplit.length, resultExportSplit.length)

            for (int i = 0; i < sampleExportSplit.length; i++) {
                //skipped comparison the first line for payslip Micropay export from the unit test
                //because it contains the current data
                if (SS_BULK_UPLOAD_KEYCODE.equals(keyCode)) {
                    sampleExportSplit[i] = sampleExportSplit[i].replace("diff", (LocalDate.now().getYear() - 2014).toString())
                }
                if (!(PAYSLIP_MICROPAY_KEYCODE.equals(keyCode) && i == 0)) {
                    def resultStr = resultExportSplit[i].trim()
                    def sampleStr = sampleExportSplit[i].trim()
                    resultStr = removeTimezonesFrom(resultStr)
                    sampleStr = removeTimezonesFrom(sampleStr)
                    Assertions.assertEquals(sampleStr, resultStr)
                }
            }
        }
    }

    /**
     * We need this because of git tests timezone and xml datasets. We can set from code only timezone of datetime
     * we print, but cannot configure timezone of string we read from our datasets. So timezone and all values, which
     * contain hours value, are removed from checks. With date everything is ok
     * REGEXES:
     * 1. T12:10:00+05:00 or T12:10:00Z
     * 2. 12:10:00 MSK 2008
     * 3. ' 12:00'
     * @param outputStr
     * @return
     */
    private static String removeTimezonesFrom(String outputStr) {
        outputStr = outputStr.replaceAll("T\\d{1,2}:\\d{1,2}:\\d{1,2}(\\+\\d{1,2}:\\d{1,2}|Z)", "")
        outputStr = outputStr.replaceAll("\\d{1,2}:\\d{1,2}:\\d{1,2} \\w{3,5} \\d{4}", "")
        outputStr.replaceAll(" \\d{1,2}:\\d{1,2}", "")
    }
}
