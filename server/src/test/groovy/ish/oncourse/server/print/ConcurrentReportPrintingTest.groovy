/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.server.print

import groovy.transform.CompileStatic
import ish.TestWithDatabase
import ish.DatabaseSetup
import ish.oncourse.cayenne.PaymentInterface
import ish.oncourse.cayenne.PersistentObjectI
import ish.oncourse.common.ResourceType
import ish.oncourse.common.ResourcesUtil
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.cayenne.PaymentIn
import ish.oncourse.server.cayenne.PaymentOut
import ish.oncourse.server.cayenne.Report
import ish.oncourse.server.cayenne.glue.CayenneDataObject
import ish.oncourse.server.document.DocumentService
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
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

import java.time.LocalDate
/**
 * executes multiple print jobs at the same time simulating multiple users
 */
@CompileStatic
@Disabled
@DatabaseSetup(value = "ish/oncourse/server/sampleData.xml")
class ConcurrentReportPrintingTest extends TestWithDatabase {
    List<String> keyCodeList = new ArrayList<>()
    
    @BeforeEach
    void dataPopulation() throws Exception {
        DataPopulation dataPopulation = injector.getInstance(DataPopulation.class)

        try {
            // can only really test export templates, the other imports require window server...
            dataPopulation.run()
        } catch (Exception e) {
            Assertions.fail("could not import one of the resources " + e)
        }
        new JRRuntimeConfig().config()

        def reportsList = PluginService.getPluggableResources(ResourceType.REPORT.getResourcePath(), ResourceType.REPORT.getFilePattern())

        for (String reportFile : reportsList) {
            if (reportFile.endsWith(".jrxml")) {
                StringBuffer buffer = new StringBuffer(ResourcesUtil.readFile(reportFile))

                String isVisible = DataPopulation.getPropertyFromXml(buffer, Report.IS_VISIBLE_PROPERTY)

                if (Boolean.parseBoolean(isVisible)) {
                    String keycode = DataPopulation.getPropertyFromXml(buffer, Report.KEY_CODE_PROPERTY)
                    if (keycode == "ish.onCourse.banking2") {
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

        for (int counter : reportsToTest) {
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
            if (entityClass == PaymentInterface) {
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
                if (PrintResult.ResultType.IN_PROGRESS == w.getResult().getResultType()) {
                    workersRunning = workersRunning + 1
                }
            }
            workersFinished = workersRunning == 0
        }

        for (Map.Entry<PrintRequest, PrintWorker> e : reportsToRun.entrySet()) {
            Assertions.assertEquals(PrintResult.ResultType.SUCCESS, e.getValue().getResult().getResultType(), String.format("Printing failed for %s", e.getKey().getReportCode()))
            Assertions.assertNotNull(e.getValue().getResult().getResult(), String.format("Empty printing result for %s", e.getKey().getReportCode()))


            FileUtils.writeByteArrayToFile(new File("build/test-data/concurrentReportTestOutput/" + e.getKey().getReportCode() + ".pdf"), e.getValue().getResult().getResult())
        }
    }
}
