/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.export.avetmiss8

import groovy.transform.CompileStatic
import ish.TestWithDatabase
import ish.DatabaseSetup
import ish.common.types.OutcomeStatus
import ish.oncourse.common.ExportJurisdiction
import ish.oncourse.common.ResourcesUtil
import ish.oncourse.entity.services.CertificateService
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.cayenne.Outcome
import ish.oncourse.server.export.avetmiss.AvetmissExport
import ish.oncourse.server.export.avetmiss.AvetmissExportResult
import ish.util.RuntimeUtil
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import java.time.LocalDate
import java.time.Month

@CompileStatic
@DatabaseSetup(value = "ish/oncourse/server/export/avetmiss8/exportTest.xml")
class Avetmiss8ExportTest extends TestWithDatabase {

    private static LocalDate startYear = LocalDate.of(2013, Month.JANUARY, 1)
    private static LocalDate midYear = LocalDate.of(2013, Month.MARCH, 30)
    private static LocalDate endYear = LocalDate.of(2013, Month.DECEMBER, 31)

    private CertificateService certificateService

    @BeforeEach
    void services() {
        super.setup()
        certificateService = injector.getInstance(CertificateService.class)
    }

    @Test
    void test1() throws Exception {
        AvetmissExportResult runner = createTestRunner(ExportJurisdiction.PLAIN)
        verifyOutput(runner, "test1")
    }

    @Test
    void test2() throws Exception {
        def runner = createTestRunner(ExportJurisdiction.PLAIN, true, OutcomeStatus.STATUS_NO_RESULT_QLD)
        verifyOutput(runner, "test2")
    }

    @Test
    void partialSubmission() throws Exception {
        def runner = createTestRunner(ExportJurisdiction.PLAIN, startYear, midYear, true, OutcomeStatus.STATUS_NO_RESULT_QLD)
        verifyOutput(runner, "partialSubmission")
    }

    @Test
    void testNSW() throws Exception {
        def runner = createTestRunner(ExportJurisdiction.NSW)
        verifyOutput(runner, "NSW")
    }

    @Test
    void testQLD() throws Exception {
        def runner = createTestRunner(ExportJurisdiction.QLD)
        verifyOutput(runner, "QLD")
    }

    @Test
    void testSA() throws Exception {
        def runner = createTestRunner(ExportJurisdiction.SA)
        verifyOutput(runner, "SA")
    }

    @Test
    void testTAS() throws Exception {
        def runner = createTestRunner(ExportJurisdiction.TAS)
        verifyOutput(runner, "TAS")
    }

    @Test
    void testVIC() throws Exception {
        def runner = createTestRunner(ExportJurisdiction.VIC)
        verifyOutput(runner, "VIC")
    }

    @Test
    void testWA() throws Exception {
        def runner = createTestRunner(ExportJurisdiction.WA)
        verifyOutput(runner, "WA")
    }

    @Test
    void testSmartAndSkilled() throws Exception {
        def runner = createTestRunner(ExportJurisdiction.SMART)
        verifyOutput(runner, "smart")
    }

    @Test
    void testOliv() throws Exception {
        def runner = createTestRunner(ExportJurisdiction.OLIV)
        verifyOutput(runner, "oliv")
    }

    @Test
    void testNTVETPP() throws Exception {
        def runner = createTestRunner(ExportJurisdiction.NTVETPP)
        verifyOutput(runner, "test1")
    }

    @Test
    void testAVETARS() throws Exception {
        def runner = createTestRunner(ExportJurisdiction.AVETARS)
        verifyOutput(runner, "test1")
    }

    private AvetmissExportResult createTestRunner(ExportJurisdiction jurisdiction) {
        return createTestRunner(jurisdiction, startYear, endYear, false, null)
    }

    private AvetmissExportResult createTestRunner(ExportJurisdiction jurisdiction, Boolean isVET) {
        return createTestRunner(jurisdiction, startYear, endYear, isVET, null)
    }

    private AvetmissExportResult createTestRunner(ExportJurisdiction jurisdiction, Boolean isVET, OutcomeStatus defaultOutcome) {
        return createTestRunner(jurisdiction, startYear, endYear, isVET, defaultOutcome)
    }

    
    private AvetmissExportResult createTestRunner(ExportJurisdiction jurisdiction, LocalDate localStartDate, LocalDate localEndDate, Boolean isVET, OutcomeStatus defaultOutcome) {

        List<AvetmissExport> fees = AvetmissExport.values()
                .findAll { avetmissExport -> !isVET || AvetmissExport.AVETMISS_NON_VET != avetmissExport } as List

        Set<Outcome> outcomes = Avetmiss8ExportRunner.getOutcomes(
                [] as List<Long>,
                [] as List<Long>,
                [] as List<Long>,
                false,
                localEndDate,
                localStartDate,
                fees,
                jurisdiction,
                cayenneService.newContext)

        return Avetmiss8ExportRunner.export(cayenneService.getNewContext(),
                certificateService,
                injector.getInstance(PreferenceController.class),
                jurisdiction,
                defaultOutcome,
                LocalDate.now().plusDays(7),
                localEndDate,
                outcomes*.id)
    }

    /**
     * tests if any of the ExportJurisdiction/isVetExport combinations throws exception, but does not verify any files.
     */
    
    @Test
    void testNoExceptions() {
        StringBuilder errorMsg = new StringBuilder()
        for (ExportJurisdiction ej : ExportJurisdiction.values()) {
            for (final boolean vetExport : [false, true]) {
                try {
                    createTestRunner(ej, vetExport)
                } catch (Exception e) {
                    errorMsg.append("Avetmiss export for ")
                    errorMsg.append(ej.getDisplayName())
                    if (vetExport) {
                        errorMsg.append(" (VET)")
                    }
                    errorMsg.append(" has failed throwing exception: ")
                    errorMsg.append(e.getMessage())
                    errorMsg.append(RuntimeUtil.LINE_SEPARATOR)
                }
            }
        }
        String error = errorMsg.toString()
        if (!error.isEmpty()) {
            Assertions.fail(error)
        }
    }


    
    private static void verifyOutput(AvetmissExportResult result, String testName) throws Exception {

        String now_plus_7_days = AvetmissLine.localDateFormatter.format(LocalDate.now().plusDays(7))
        File outputFolder = new File("build/test-data/avetmissExportTest/" + testName)
        try {
            FileUtils.deleteDirectory(outputFolder)
        } catch (IOException ignored) {
        }
        outputFolder.mkdirs()

        result.getFiles().each { filename, data ->
            InputStream inputStream = ResourcesUtil.getResourceAsInputStream("ish/oncourse/server/export/avetmiss8/" +
                    testName + "/" + filename)
            List<String> expected = IOUtils.readLines(inputStream)
            List<String> actual = IOUtils.readLines(new ByteArrayInputStream(data.toByteArray()))

            //sort the lists
            Collections.sort(actual)
            Collections.sort(expected)

            // Save the file to disk for further inspection (not needed by the tests, but useful for debugging)
            def f = new FileOutputStream(new File(outputFolder, filename))
            data.writeTo(f)
            f.close()
            LocalDate.now().plusDays(7)
            try {
                Assertions.assertEquals(actual.size(), expected.size(), filename + " row count wrong")
                for (int i = 0; i < expected.size(); i++) {
                    String expectedLine = expected.get(i)
                    expectedLine = expectedLine.replaceAll("now_plus_7_days", now_plus_7_days)
                    Assertions.assertEquals(actual.get(i), expectedLine, filename)
                }
            } catch (Exception e) {
                Assertions.fail(e)
            }
        }
    }

}
