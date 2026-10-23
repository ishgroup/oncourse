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
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.yaml.snakeyaml.Yaml

@CompileStatic
@DatabaseSetup
class ReportStatusOnStartupTest extends TestWithDatabase {

    private static final String INVOICE_REPORT = "ish.onCourse.invoiceReport"
    private static final String TRIAL_BALANCE_REPORT = "ish.onCourse.trialBalance"
    private static final String TRAINING_PACKAGE_REPORT = "ish.onCourse.trainingPackageReport"

    @Test
    void testDisabledReportStaysDisabledAfterRestart() throws Exception {
        Map<String, Object> props = getCatalogProps(INVOICE_REPORT, true)
        removeReport(INVOICE_REPORT)

        importReport(props)
        Assertions.assertEquals(AutomationStatus.ENABLED, getStatus(INVOICE_REPORT))

        setStatus(INVOICE_REPORT, AutomationStatus.INSTALLED_DISABLED)
        importReport(props)
        Assertions.assertEquals(AutomationStatus.INSTALLED_DISABLED, getStatus(INVOICE_REPORT))

        setStatus(INVOICE_REPORT, AutomationStatus.NOT_INSTALLED)
        importReport(props)
        Assertions.assertEquals(AutomationStatus.NOT_INSTALLED, getStatus(INVOICE_REPORT))
    }

    @Test
    void testEnabledReportStaysEnabledAfterRestart() throws Exception {
        Map<String, Object> props = getCatalogProps(TRIAL_BALANCE_REPORT, true)
        removeReport(TRIAL_BALANCE_REPORT)

        importReport(props)
        Assertions.assertEquals(AutomationStatus.INSTALLED_DISABLED, getStatus(TRIAL_BALANCE_REPORT))

        setStatus(TRIAL_BALANCE_REPORT, AutomationStatus.ENABLED)
        importReport(props)
        Assertions.assertEquals(AutomationStatus.ENABLED, getStatus(TRIAL_BALANCE_REPORT))
    }

    @Test
    void testAusReportGetsCatalogStatusWhenAusReportingTurnedOn() throws Exception {
        removeReport(TRAINING_PACKAGE_REPORT)

        importReport(getCatalogProps(TRAINING_PACKAGE_REPORT, false))
        Assertions.assertEquals(AutomationStatus.HIDDEN, getStatus(TRAINING_PACKAGE_REPORT))

        importReport(getCatalogProps(TRAINING_PACKAGE_REPORT, false))
        Assertions.assertEquals(AutomationStatus.HIDDEN, getStatus(TRAINING_PACKAGE_REPORT))

        importReport(getCatalogProps(TRAINING_PACKAGE_REPORT, true))
        Assertions.assertEquals(AutomationStatus.ENABLED, getStatus(TRAINING_PACKAGE_REPORT))

        setStatus(TRAINING_PACKAGE_REPORT, AutomationStatus.INSTALLED_DISABLED)
        importReport(getCatalogProps(TRAINING_PACKAGE_REPORT, true))
        Assertions.assertEquals(AutomationStatus.INSTALLED_DISABLED, getStatus(TRAINING_PACKAGE_REPORT))
    }

    @Test
    void testUploadedConfigsUpdateStatus() throws Exception {
        Map<String, Object> props = getCatalogProps(INVOICE_REPORT, true)
        removeReport(INVOICE_REPORT)

        importReport(props)
        setStatus(INVOICE_REPORT, AutomationStatus.INSTALLED_DISABLED)

        ObjectContext context = getCayenneService().newContext
        DataPopulationUtils.fillReportWithUploadedConfigs(getReport(context, INVOICE_REPORT), props)
        context.commitChanges()
        Assertions.assertEquals(AutomationStatus.ENABLED, getStatus(INVOICE_REPORT))
    }


    private void importReport(Map<String, Object> props) {
        DataPopulationUtils.updateReport(getCayenneService().newContext, props)
    }

    private void removeReport(String keyCode) {
        ObjectContext context = getCayenneService().newContext
        Report report = getReport(context, keyCode)
        if (report) {
            context.deleteObject(report)
            context.commitChanges()
        }
        Assertions.assertNull(getReport(getCayenneService().newContext, keyCode))
    }

    private void setStatus(String keyCode, AutomationStatus status) {
        ObjectContext context = getCayenneService().newContext
        getReport(context, keyCode).automationStatus = status
        context.commitChanges()
    }

    private AutomationStatus getStatus(String keyCode) {
        return getReport(getCayenneService().newContext, keyCode).automationStatus
    }

    private static Report getReport(ObjectContext context, String keyCode) {
        return ObjectSelect.query(Report).where(Report.KEY_CODE.eq(keyCode)).selectOne(context)
    }

    private static Map<String, Object> getCatalogProps(String keyCode, boolean ausReporting) {
        InputStream stream = ResourcesUtil.getResourceAsInputStream("reports/catalog.yaml")
        try {
            List<Map<String, Object>> catalog = (List<Map<String, Object>>) new Yaml().load(stream)
            Map<String, Object> props = new HashMap<>(catalog.find { it.get("keyCode") == keyCode })
            props.put(ResourceProperty.AUS_REPORTING.displayName, ausReporting)
            return props
        } finally {
            stream.close()
        }
    }
}
