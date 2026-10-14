/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.upgrades

import groovy.transform.CompileStatic
import ish.DatabaseSetup
import ish.TestWithDatabase
import ish.common.types.AutomationStatus
import ish.oncourse.common.ResourceProperty
import ish.oncourse.common.ResourcesUtil
import ish.oncourse.server.cayenne.Report
import org.apache.cayenne.query.ObjectSelect
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.yaml.snakeyaml.Yaml

@CompileStatic
@DatabaseSetup
class ReportStatusOnStartupTest extends TestWithDatabase {

    private static final String INVOICE_REPORT = "ish.onCourse.invoiceReport"
    private static final String TRIAL_BALANCE_REPORT = "ish.onCourse.trialBalance"

    @Test
    void testDisabledReportStaysDisabledAfterRestart() throws Exception {
        Map<String, Object> props = getCatalogProps(INVOICE_REPORT)

        DataPopulationUtils.updateReport(cayenneContext, props)
        Report report = getReport(INVOICE_REPORT)
        Assertions.assertEquals(AutomationStatus.ENABLED, report.automationStatus)

        report.automationStatus = AutomationStatus.INSTALLED_DISABLED
        cayenneContext.commitChanges()

        DataPopulationUtils.updateReport(cayenneContext, props)
        Assertions.assertEquals(AutomationStatus.INSTALLED_DISABLED, getReport(INVOICE_REPORT).automationStatus)

        report.automationStatus = AutomationStatus.NOT_INSTALLED
        cayenneContext.commitChanges()

        DataPopulationUtils.updateReport(cayenneContext, props)
        Assertions.assertEquals(AutomationStatus.NOT_INSTALLED, getReport(INVOICE_REPORT).automationStatus)
    }

    @Test
    void testEnabledReportStaysEnabledAfterRestart() throws Exception {
        Map<String, Object> props = getCatalogProps(TRIAL_BALANCE_REPORT)

        DataPopulationUtils.updateReport(cayenneContext, props)
        Report report = getReport(TRIAL_BALANCE_REPORT)
        Assertions.assertEquals(AutomationStatus.INSTALLED_DISABLED, report.automationStatus)

        report.automationStatus = AutomationStatus.ENABLED
        cayenneContext.commitChanges()

        DataPopulationUtils.updateReport(cayenneContext, props)
        Assertions.assertEquals(AutomationStatus.ENABLED, getReport(TRIAL_BALANCE_REPORT).automationStatus)
    }

    @Test
    void testUploadedConfigsUpdateStatus() throws Exception {
        Map<String, Object> props = getCatalogProps(INVOICE_REPORT)

        DataPopulationUtils.updateReport(cayenneContext, props)
        Report report = getReport(INVOICE_REPORT)
        report.automationStatus = AutomationStatus.INSTALLED_DISABLED
        cayenneContext.commitChanges()

        DataPopulationUtils.fillReportWithCommonFields(report, props)
        cayenneContext.commitChanges()
        Assertions.assertEquals(AutomationStatus.ENABLED, getReport(INVOICE_REPORT).automationStatus)
    }


    private Report getReport(String keyCode) {
        return ObjectSelect.query(Report).where(Report.KEY_CODE.eq(keyCode)).selectOne(cayenneContext)
    }

    private static Map<String, Object> getCatalogProps(String keyCode) {
        InputStream stream = ResourcesUtil.getResourceAsInputStream("reports/catalog.yaml")
        try {
            List<Map<String, Object>> catalog = (List<Map<String, Object>>) new Yaml().load(stream)
            Map<String, Object> props = new HashMap<>(catalog.find { it.get("keyCode") == keyCode })
            props.put(ResourceProperty.AUS_REPORTING.displayName, true)
            return props
        } finally {
            stream.close()
        }
    }
}
