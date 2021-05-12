/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.print

import groovy.transform.CompileStatic
import ish.CayenneIshTestCase
import ish.DatabaseSetup
import ish.oncourse.cayenne.PaymentInterface
import ish.oncourse.cayenne.PersistentObjectI
import ish.oncourse.common.ResourcesUtil
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.cayenne.PaymentIn
import ish.oncourse.server.cayenne.PaymentOut
import ish.oncourse.server.cayenne.Report
import ish.oncourse.server.cayenne.ReportOverlay
import ish.oncourse.server.cayenne.glue.CayenneDataObject
import ish.oncourse.server.document.DocumentService
import ish.oncourse.server.upgrades.DataPopulation
import ish.print.AdditionalParameters
import ish.print.PrintRequest
import ish.print.PrintResult.ResultType
import ish.print.PrintTransformationsFactory
import ish.util.EntityUtil
import org.apache.cayenne.query.SelectQuery
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
/**
 * Report printing test which tries to print all existing reports using mock entities.
 */
@Disabled
@CompileStatic
@DatabaseSetup(value = "ish/oncourse/server/sampleData.xml")
class ReportPrintingTest extends CayenneIshTestCase {
    protected static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd")
    protected static final Integer TEST_BUNCH_SIZE = 70

    private static File overlay2pagePortrait = getResourceAsFile("resources/schema/referenceData/reports/transparencyTestPortrait.pdf")
    private static File overlay2pageLandscape = getResourceAsFile("resources/schema/referenceData/reports/transparencyTestLandscape.pdf")

    private static Map<String, List<String>> AVAILABLE_TRANSFORMATIONS
    static {
        Map<String, List<String>> aMap = new HashMap<>()
        // map, what report entity can be printed from what source:ie.
        // AccountTransaction can be printed from Account and AccountTransaction
        aMap.put("AccountTransaction", Arrays.asList("Account", "AccountTransaction"))
        aMap.put("InvoiceLine", Arrays.asList("Invoice"))
        aMap.put("ClassCost", Arrays.asList("CourseClass"))
        aMap.put("Outcome", Arrays.asList("CourseClass"))
        aMap.put("Enrolment", Arrays.asList("CourseClass", "Enrolment"))
        aMap.put("Session", Arrays.asList("CourseClass", "Session"))
        AVAILABLE_TRANSFORMATIONS = Collections.unmodifiableMap(aMap)
    }

    private static final List<String> EXTRA_REPORTS = Arrays.asList("ish.onCourse.trialBalance", "ish.onCourse.shedule", "ish.onCourse.trainingPlanDetailsReport")

    @BeforeEach
    void init() throws Exception {
        DataPopulation dataPopulation = injector.getInstance(DataPopulation.class)

        try {
            // can only really test export templates, the other imports require window server...
            dataPopulation.run()
        } catch (Exception e) {
            Assertions.fail("could not import one of the resources " + e)
        }

        //add backgrounds
        ReportOverlay overlayPortrait = cayenneContext.newObject(ReportOverlay.class)
        overlayPortrait.setOverlay(FileUtils.readFileToByteArray(overlay2pagePortrait))
        overlayPortrait.setName("Test overlay portrait")

        ReportOverlay overlayLandscape = cayenneContext.newObject(ReportOverlay.class)
        overlayLandscape.setOverlay(FileUtils.readFileToByteArray(overlay2pageLandscape))
        overlayLandscape.setName("Test overlay landscape")
        cayenneContext.commitChanges()
    }

    static Collection<Arguments> values() throws Exception {

        List<String> reportsList = IOUtils.readLines(ResourcesUtil.getResourceAsInputStream("reports/manifest"))
        List<Arguments> keyCodeList = new ArrayList<>()

        for (int i = 0; i < reportsList.size() && i < TEST_BUNCH_SIZE; i++) {
            String reportFile = reportsList.get(i)
            prepareReport(reportFile, keyCodeList)
        }

        return keyCodeList
    }

    protected static void prepareReport(String reportFile, List<Arguments> keyCodeList) {

        if (reportFile.endsWith(".jrxml")) {
            StringBuffer buffer = new StringBuffer(ResourcesUtil.readFile(reportFile))

            String isVisible = DataPopulation.getPropertyFromXml(buffer, Report.IS_VISIBLE_PROPERTY)
            String keycode = DataPopulation.getPropertyFromXml(buffer, Report.KEY_CODE_PROPERTY)

            if (Boolean.parseBoolean(isVisible) || EXTRA_REPORTS.contains(keycode)) {

                String[] temp = reportFile.split("/")

                String entity = DataPopulation.getPropertyFromXml(buffer, Report.ENTITY_PROPERTY)

                if (AVAILABLE_TRANSFORMATIONS.get(entity) != null) {
                    for (String s : AVAILABLE_TRANSFORMATIONS.get(entity)) {
                        keyCodeList.add(Arguments.of(keycode, s, temp[temp.length - 2]))
                    }
                } else if ("PaymentInterface" == entity) {
                    keyCodeList.add(Arguments.of(keycode, "PaymentIn", temp[temp.length - 2]))
                    keyCodeList.add(Arguments.of(keycode, "PaymentOut", temp[temp.length - 2]))
                } else {
                    keyCodeList.add(Arguments.of(keycode, entity, temp[temp.length - 2]))
                }
            }
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("values")
    void testReportWithRealData(String reportCode, String sourceEntity, String reportFolder) throws Exception {
        Report report = cayenneContext.selectOne(SelectQuery.query(Report.class, Report.KEY_CODE.eq(reportCode)))

        final PrintRequest request = new PrintRequest()
        request.setReportCode(report.getKeyCode())
        request.setEntity(sourceEntity)

        request.setValueForKey(AdditionalParameters.DATERANGE_FROM.toString(), DATE_FORMAT.parse("2012-01-01"))
        request.setValueForKey(AdditionalParameters.DATERANGE_TO.toString(), DATE_FORMAT.parse("2013-01-01"))
        request.setValueForKey(AdditionalParameters.LOCALDATERANGE_FROM.toString(), LocalDate.of(2012, 1, 1))
        request.setValueForKey(AdditionalParameters.LOCALDATERANGE_TO.toString(), LocalDate.of(2013, 1, 1))

        request.addPrintTransformation(sourceEntity,
                PrintTransformationsFactory.getPrintTransformationFor(sourceEntity, report.getEntity(), report.getKeyCode()))

        Map<String, List<Long>> mapOfIds = new HashMap<>()

        Class<? extends PersistentObjectI> entityClass = EntityUtil.entityClassForName(sourceEntity)

        List<PersistentObjectI> list = new ArrayList<>()
        if (entityClass instanceof PaymentInterface) {
            list.addAll(cayenneContext.select(SelectQuery.query(PaymentIn.class)))
            list.addAll(cayenneContext.select(SelectQuery.query(PaymentOut.class)))
        } else {
            list.addAll(cayenneContext.select(SelectQuery.query(entityClass)))
        }
        if (list.size() == 0) {
            Assertions.fail("Printing of ${report.getKeyCode()} failed, there is no records to print.")
            return
        }

        for (Object o : list) {
            if (mapOfIds.get(o.getClass().getSimpleName()) == null) {
                List<Long> idsToPrint = new ArrayList<>()
                mapOfIds.put(o.getClass().getSimpleName(), idsToPrint)
            }
            if (o instanceof CayenneDataObject) {
                mapOfIds.get(o.getClass().getSimpleName()).add(((CayenneDataObject) o).getPrimaryKeyValue().longValue())
            }
        }

        request.setIds(mapOfIds)

        PrintWorker worker = new PrintWorker(request, cayenneService, injector.getInstance(PreferenceController.class) as DocumentService)
        worker.run()

        while (ResultType.IN_PROGRESS == worker.getResult().getResultType()) {
            Thread.sleep(200)
        }

        Assertions.assertEquals(ResultType.SUCCESS, worker.getResult().getResultType(), String.format("Printing failed for %s", report.getName()))
        Assertions.assertNotNull(worker.getResult().getResult(), String.format("Empty printing result for %s", report.getName()))

        FileUtils.writeByteArrayToFile(new File("build/test-data/printing/" + reportFolder + "/" + report.getName() + "-" + sourceEntity + ".pdf"), worker.getResult().getResult())
    }
}
