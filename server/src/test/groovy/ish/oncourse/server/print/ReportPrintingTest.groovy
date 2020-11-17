/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.print

import groovy.transform.CompileStatic
import ish.CayenneIshTestCase
import ish.oncourse.cayenne.PaymentInterface
import ish.oncourse.cayenne.PersistentObjectI
import ish.oncourse.common.ResourcesUtil
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.cayenne.PaymentIn
import ish.oncourse.server.cayenne.PaymentOut
import ish.oncourse.server.cayenne.Report
import ish.oncourse.server.cayenne.ReportOverlay
import ish.oncourse.server.cayenne.glue.CayenneDataObject
import ish.oncourse.server.upgrades.DataPopulation
import ish.print.AdditionalParameters
import ish.print.PrintRequest
import ish.print.PrintResult.ResultType
import ish.print.PrintTransformationsFactory
import ish.util.EntityUtil
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.access.DataContext
import org.apache.cayenne.query.SelectQuery
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.dbunit.dataset.ReplacementDataSet
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.fail
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate

/**
 * Report printing test which tries to print all existing reports using mock entities.
 */
@Ignore
@RunWith(Parameterized.class)
class ReportPrintingTest extends CayenneIshTestCase {
	private static final Logger logger = LogManager.getLogger()

    protected static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd")
    protected static final Integer TEST_BUNCH_SIZE = 70

    private static File overlay2pagePortrait = getResourceAsFile("resources/schema/referenceData/reports/transparencyTestPortrait.pdf")
    private static File overlay2pageLandscape = getResourceAsFile("resources/schema/referenceData/reports/transparencyTestLandscape.pdf")

    protected ICayenneService cayenneService

    private String reportCode
    private String reportFolder
    private String sourceEntity

    private static Map<String, List<String>> AVAILABLE_TRANSFORMATIONS
    static {
		Map<String, List<String>> aMap = new HashMap<>()
        // map, what report entity can be printed from what source:ie.
		// AccountTransaction can be printed from Accoutn and AccountTransaction
		aMap.put("AccountTransaction", Arrays.asList("Account", "AccountTransaction"))
        aMap.put("InvoiceLine", Arrays.asList("Invoice"))
        aMap.put("ClassCost", Arrays.asList("CourseClass"))
        aMap.put("Outcome", Arrays.asList("CourseClass"))
        aMap.put("Enrolment", Arrays.asList("CourseClass", "Enrolment"))
        aMap.put("Session", Arrays.asList("CourseClass", "Session"))
        AVAILABLE_TRANSFORMATIONS = Collections.unmodifiableMap(aMap)
    }

	private static final List<String> EXTRA_REPORTS = Arrays.asList("ish.onCourse.trialBalance","ish.onCourse.shedule","ish.onCourse.trainingPlanDetailsReport")

    @Before
    void init() throws Exception {
		wipeTables()
        this.cayenneService = injector.getInstance(ICayenneService.class)

        InputStream st = ReportPrintingTest.class.getClassLoader().getResourceAsStream("ish/oncourse/server/sampleData.xml")

        FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder()
        builder.setColumnSensing(true)
        FlatXmlDataSet dataSet = builder.build(st)
        ReplacementDataSet rDataSet = new ReplacementDataSet(dataSet)
        rDataSet.addReplacementObject("[null]", null)

        executeDatabaseOperation(rDataSet)

        DataPopulation dataPopulation = injector.getInstance(DataPopulation.class)

        try {
			// can only really test export templates, the other imports require window server...
			dataPopulation.run()
        } catch (Exception e) {
			logger.warn("fail", e)
            fail("could not import one of the resources " + e)
        }

		//add backgrounds
		DataContext cc = injector.getInstance(ICayenneService.class).getNewNonReplicatingContext()
        ReportOverlay overlayPortrait = cc.newObject(ReportOverlay.class)
        overlayPortrait.setOverlay(FileUtils.readFileToByteArray(overlay2pagePortrait))
        overlayPortrait.setName("Test overlay portrait")

        ReportOverlay overlayLandscape = cc.newObject(ReportOverlay.class)
        overlayLandscape.setOverlay(FileUtils.readFileToByteArray(overlay2pageLandscape))
        overlayLandscape.setName("Test overlay landscape")
        cc.commitChanges()
    }

    ReportPrintingTest(String reportCode, String sourceEntity, String reportFolder) {
		this.reportCode = reportCode
        this.reportFolder = reportFolder
        this.sourceEntity = sourceEntity
    }

	@Parameters(name = "{0}")
    static Collection<String[]> reportCodes() throws Exception {

		List<String> reportsList = IOUtils.readLines(ResourcesUtil.getResourceAsInputStream("reports/manifest"))
        List<String[]> keyCodeList = new ArrayList<>()

        for (int i = 0; i < reportsList.size() && i < TEST_BUNCH_SIZE; i++) {
			String reportFile = reportsList.get(i)
            prepareReport(reportFile, keyCodeList)
        }

		return keyCodeList
    }

	protected static void prepareReport(String reportFile, List<String[]> keyCodeList) {

		if (reportFile.endsWith(".jrxml")) {
			StringBuffer buffer = new StringBuffer(ResourcesUtil.readFile(reportFile))

            String isVisible = DataPopulation.getPropertyFromXml(buffer, Report.IS_VISIBLE_PROPERTY)
            String keycode = DataPopulation.getPropertyFromXml(buffer, Report.KEY_CODE_PROPERTY)

            if (Boolean.parseBoolean(isVisible) ||  EXTRA_REPORTS.contains(keycode)) {

				String[] temp = reportFile.split("/")

                String entity = DataPopulation.getPropertyFromXml(buffer, Report.ENTITY_PROPERTY)

                if (AVAILABLE_TRANSFORMATIONS.get(entity) != null) {
					for (String s : AVAILABLE_TRANSFORMATIONS.get(entity)) {
						keyCodeList.add([keycode, s, temp[temp.length - 2]] as String[])
                    }
				} else if ("PaymentInterface" == entity) {
					keyCodeList.add([keycode, "PaymentIn", temp[temp.length - 2]] as String[])
                    keyCodeList.add([keycode, "PaymentOut", temp[temp.length - 2]] as String[])
                } else {
					keyCodeList.add([keycode, entity, temp[temp.length - 2]] as String[])
                }
			}
		}
	}

	@Test
    void testReportWithRealData() throws Exception {

		ObjectContext context = cayenneService.getNewNonReplicatingContext()

        Report report = context.selectOne(SelectQuery.query(Report.class, Report.KEY_CODE.eq(reportCode)))

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
			list.addAll(cayenneService.getNewContext().select(SelectQuery.query(PaymentIn.class)))
            list.addAll(cayenneService.getNewContext().select(SelectQuery.query(PaymentOut.class)))
        } else {
			list.addAll(cayenneService.getNewContext().select(SelectQuery.query(entityClass)))
        }
		if (list.size() == 0) {
			logger.warn("Printing of {} failed, there is no records to print.", report.getKeyCode())
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

        PrintWorker worker = new PrintWorker(request, cayenneService, injector.getInstance(PreferenceController.class))
        logger.warn("printing {} from {} {}(s)", reportCode, list.size(), sourceEntity)
        logger.warn("printing {}", request)
        worker.run()

        while (ResultType.IN_PROGRESS == worker.getResult().getResultType()) {
			Thread.sleep(200)
        }

		assertEquals(String.format("Printing failed for %s", report.getName()), ResultType.SUCCESS, worker.getResult().getResultType())
        assertNotNull(String.format("Empty printing result for %s", report.getName()), worker.getResult().getResult())

        FileUtils.writeByteArrayToFile(new File("build/test-data/printing/"+reportFolder+"/"+report.getName()+"-"+sourceEntity+".pdf"), worker.getResult().getResult())
    }
}
