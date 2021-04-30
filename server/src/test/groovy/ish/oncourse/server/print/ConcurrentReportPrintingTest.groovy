/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.server.print

import ish.CayenneIshTestCase
import ish.oncourse.cayenne.PaymentInterface
import ish.oncourse.cayenne.PersistentObjectI
import ish.oncourse.common.ResourceType
import ish.oncourse.common.ResourcesUtil
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.cayenne.PaymentIn
import ish.oncourse.server.cayenne.PaymentOut
import ish.oncourse.server.cayenne.Report
import ish.oncourse.server.cayenne.glue.CayenneDataObject
import ish.oncourse.server.integration.PluginService
import ish.oncourse.server.report.JRRuntimeConfig
import ish.oncourse.server.upgrades.DataPopulation
import ish.print.AdditionalParameters
import ish.print.PrintRequest
import ish.print.PrintResult
import ish.print.PrintTransformationsFactory
import ish.util.EntityUtil
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.SelectQuery
import org.apache.commons.io.FileUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.dbunit.dataset.ReplacementDataSet
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.junit.Ignore
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate

import static org.junit.Assert.*

/**
 * executes multiple print jobs at the same time simulating multiple users
 */
@Ignore
class ConcurrentReportPrintingTest extends CayenneIshTestCase {
	private static final Logger logger = LogManager.getLogger()

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd")

    private ICayenneService cayenneService

    List<String> keyCodeList = new ArrayList<>()


    @BeforeEach
    void setup() throws Exception {
		wipeTables()

        InputStream st = ConcurrentReportPrintingTest.class.getClassLoader().getResourceAsStream("ish/oncourse/server/sampleData.xml")

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
		new JRRuntimeConfig().config()


        this.cayenneService = injector.getInstance(ICayenneService.class)

        def reportsList = PluginService.getPluggableResources(ResourceType.REPORT.getResourcePath(), ResourceType.REPORT.getFilePattern())

        for (String reportFile : reportsList) {
			if (reportFile.endsWith(".jrxml")) {
				StringBuffer buffer = new StringBuffer(ResourcesUtil.readFile(reportFile))

                String isVisible = DataPopulation.getPropertyFromXml(buffer, Report.IS_VISIBLE_PROPERTY)

                if (Boolean.parseBoolean(isVisible)) {
					String keycode = DataPopulation.getPropertyFromXml(buffer, Report.KEY_CODE_PROPERTY)
                    if (keycode.equals("ish.onCourse.banking2")) {
						continue
                    }
					keyCodeList.add(keycode)
                }
			}
		}
	}

	/**
	 * this test method can eb simplified (ie. testing PrintService instead), but its requried for now to test the inner doings of the classes.
	 * @throws Exception
	 */
	@Test
    void testReport() throws Exception {
		//semi randomised list of records
		int[] reportsToTest = [4, 7, 8, 11, 15, 17, 20]

        Map<PrintRequest, PrintWorker> reportsToRun = new HashMap<>()
        ObjectContext context = cayenneService.getNewNonReplicatingContext()

        for (int counter: reportsToTest) {
			String reportCode = keyCodeList.get(counter)
            Report report = context.selectOne(SelectQuery.query(Report.class, Report.KEY_CODE.eq(reportCode)))

            final PrintRequest request = new PrintRequest()
            request.setReportCode(report.getKeyCode())
            request.setEntity(report.getEntity())

            request.setValueForKey(AdditionalParameters.LOCALDATERANGE_FROM.toString(), LocalDate.parse("2012-01-01"))
            request.setValueForKey(AdditionalParameters.LOCALDATERANGE_TO.toString(), LocalDate.parse("2013-01-01"))

            Map<String, List<Long>> mapOfIds = new HashMap<>()

            String entityName = report.getEntity()
            if (entityName.indexOf('.') > 0) {
				entityName = entityName.substring(0, entityName.indexOf("."))
            }
			Class<? extends PersistentObjectI> entityClass = EntityUtil.entityClassForName(entityName)

            List<PersistentObjectI> list = new ArrayList<>()
            if (PaymentInterface.class.equals(entityClass)) {
				list.addAll(cayenneService.getNewContext().select(SelectQuery.query(PaymentIn.class)))
                list.addAll(cayenneService.getNewContext().select(SelectQuery.query(PaymentOut.class)))

                request.addPrintTransformation("PaymentIn", PrintTransformationsFactory.getPrintTransformationFor(
						"PaymentIn", "PaymentInterface", report.getKeyCode()))
                request.addPrintTransformation("PaymentOut", PrintTransformationsFactory.getPrintTransformationFor(
						"PaymentOut", "PaymentInterface", report.getKeyCode()))
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

            reportsToRun.put(request, worker)
        }

		for (PrintWorker w : reportsToRun.values()) {
			new Thread(w).start()
        }

		boolean workersFinished = false
        while (!workersFinished) {
			int workersRunning = 0
            Thread.sleep(200)
            for (PrintWorker w : reportsToRun.values()) {
				if (PrintResult.ResultType.IN_PROGRESS.equals(w.getResult().getResultType())) {
					workersRunning = workersRunning + 1
                }
			}
			workersFinished = workersRunning == 0
        }

            for (Map.Entry<PrintRequest, PrintWorker> e : reportsToRun.entrySet()) {
			assertEquals(String.format("Printing failed for %s", e.getKey().getReportCode()), PrintResult.ResultType.SUCCESS, e.getValue().getResult().getResultType())
            assertNotNull(String.format("Empty printing result for %s",  e.getKey().getReportCode()), e.getValue().getResult().getResult())


            FileUtils.writeByteArrayToFile(new File("build/test-data/concurrentReportTestOutput/"+e.getKey().getReportCode()+".pdf"), e.getValue().getResult().getResult())
        }
	}
}
