/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.server.export

import groovy.transform.CompileStatic
import ish.TestWithDatabase
import ish.export.ExportParameter
import ish.export.ExportResult
import ish.oncourse.common.ResourceProperty
import ish.oncourse.common.ResourceType
import ish.oncourse.common.ResourcesUtil
import ish.oncourse.server.cayenne.ExportTemplate
import ish.oncourse.server.cayenne.ExportTemplateAutomationBinding
import ish.oncourse.server.integration.PluginService
import ish.oncourse.server.upgrades.DataPopulation
import org.apache.cayenne.exp.ExpressionFactory
import org.apache.cayenne.query.ObjectSelect
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.StringUtils
import org.dbunit.dataset.ReplacementDataSet
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

import java.time.LocalDate

@CompileStatic
class AllExportTemplatesTest extends TestWithDatabase {
    private static final String UTC_TIMEZONE_ID = "UTC"

    private static final String PAYSLIP_MICROPAY_KEYCODE = "ish.onCourse.payslipMicropay.csv"
    private static final String SS_BULK_UPLOAD_KEYCODE = "ish.onCourse.ssBulkUpload.csv"
    private static final String LINE_SEPARATOR = StringUtils.LF

    void setup(String testDataFile) {

        // set default timezone to UTC to receive same export output regardless of
        // default timezone of building machine
        TimeZone.setDefault(TimeZone.getTimeZone(UTC_TIMEZONE_ID))

        try {
            InputStream st = AllExportTemplatesTest.class.getClassLoader().getResourceAsStream("ish/oncourse/server/export/" + testDataFile)
            FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder()
            builder.setColumnSensing(true)
            FlatXmlDataSet dataSet = builder.build(st)

            ReplacementDataSet rDataSet = new ReplacementDataSet(dataSet)
            rDataSet.addReplacementObject("[null]", null)

            executeDatabaseOperation(rDataSet)

            DataPopulation dataPopulation = injector.getInstance(DataPopulation.class)

            dataPopulation.run()

            fillBindingsManually()
        } catch (Exception e) {
            Assertions.fail(e.getMessage())
        }
    }

    void fillBindingsManually() {
        ExportTemplateAutomationBinding agedDebtorsBinding = ObjectSelect
                .query(ExportTemplateAutomationBinding.class).where(ExportTemplateAutomationBinding.RELATED_OBJECT.dot(ExportTemplate.KEY_CODE).eq("ish.oncourse.agedDebtorCsvExport.csv"))
                .selectFirst(cayenneContext)
        agedDebtorsBinding.value = '2019-06-30'

        cayenneContext.commitChanges()
    }

    @AfterEach
    void tearDown() {
        // reset timezone back to JMV's original
        TimeZone.setDefault(null)
    }

    static Collection<Arguments> values() throws Exception {

        List<Arguments> result = new ArrayList<>()
        def resourcesList = PluginService.getPluggableResources(ResourceType.EXPORT.getResourcePath(), ResourceType.EXPORT.getFilePattern())

        for (String propFile : resourcesList) {
            InputStream resourceAsStream = ResourcesUtil.getResourceAsInputStream(propFile)
            Properties props = new Properties()
            props.load(resourceAsStream)

            String keyCode = (String) props.get(ResourceProperty.KEY_CODE.getDisplayName())
            String entityName = ((String) props.get(ResourceProperty.ENTITY_CLASS.getDisplayName()))
            String outputExtention = keyCode.split("\\.").last()
            String dataSet = keyCode.split("\\.")[2] + "DataSet.xml"
            String output = keyCode.split("\\.")[2] + "SampleOutput." + outputExtention

            result.add(Arguments.of(keyCode, entityName, dataSet, output))

        }
        return result
    }

    @ParameterizedTest(name = "{1}-{0}")
    @MethodSource("values")
    void testExport(String keyCode, String entityName, String dataSet, output) throws Exception {
        setup(dataSet)
        // exclude exports for Script entity - IshTestCase updates scripts from resources after table wipe
        if (entityName != "Script") {
            ExportParameter param = new ExportParameter()

            param.setEntity(entityName)
            param.setXslKeyCode(keyCode)
            param.setExpression(ExpressionFactory.expTrue())

            ExportService export = injector.getInstance(ExportService.class)

            ExportResult result = export.export(param)

            byte[] sampleExport = IOUtils.toByteArray(AllExportTemplatesTest.class.getClassLoader().getResourceAsStream("ish/oncourse/server/export/output/" + output))

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
                    Assertions.assertEquals(sampleExportSplit[i].trim(), resultExportSplit[i].trim())
                }
            }
        }
    }
}
