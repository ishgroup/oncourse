/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.cayenne

import groovy.transform.CompileStatic
import ish.TestWithBootique
import ish.oncourse.common.ResourceType
import ish.oncourse.common.ResourcesUtil
import ish.oncourse.server.integration.PluginService
import ish.util.RuntimeUtil
import net.sf.jasperreports.engine.JRException
import net.sf.jasperreports.engine.JasperCompileManager
import net.sf.jasperreports.engine.JasperReport
import net.sf.jasperreports.engine.type.OrientationEnum
import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

import java.nio.charset.Charset

@CompileStatic
class ReportTest {


    private static final int A4_WIDTH = 595
    private static final int A4_HEIGHT = 842

    static Collection<Arguments> values() throws IOException {

        def reportsList = PluginService.getPluggableResources(ResourceType.REPORT.getResourcePath(), ResourceType.REPORT.getFilePattern())
        Assertions.assertNotNull(reportsList)

        Collection<Arguments> dataList = new ArrayList<>()
        for (String reportFile : reportsList) {
            if (reportFile.endsWith(".jrxml")) {
                List<String> untrimmedReport = IOUtils.readLines(ResourcesUtil.getResourceAsInputStream(reportFile), Charset.defaultCharset())
                Assertions.assertNotNull(untrimmedReport)
                List<String> report = trim(untrimmedReport)
                Assertions.assertNotNull(report)

                if (shouldVerify(report)) {
                    dataList.add(Arguments.of(reportFile))
                }
            }
        }
        return dataList
    }


    @ParameterizedTest(name = '${0}')
    @MethodSource("values")
    void testDefaultReportsLayout(String reportFile) throws JRException {
        List<String> errorList = new ArrayList<>()

        JasperReport jasperReport = JasperCompileManager.compileReport(ResourcesUtil.getResourceAsInputStream(reportFile))
        boolean isPortrait = jasperReport.getOrientationValue().equals(OrientationEnum.PORTRAIT)

        if (jasperReport.getPageHeight() != (isPortrait ? A4_HEIGHT : A4_WIDTH)) {
            errorList.add(String.format("Page height isn't correct. Actual: %d", jasperReport.getPageHeight()))
        }

        if (jasperReport.getPageWidth() != (isPortrait ? A4_WIDTH : A4_HEIGHT)) {
            errorList.add(String.format("Page width isn't correct. Actual: %d", jasperReport.getPageWidth()))
        }

        if (!errorList.isEmpty()) {
            StringBuilder result = new StringBuilder()
            result.append(String.format("For %s report %s found %d  problems:%s",
                    jasperReport.getOrientationValue().getName(),
                    reportFile,
                    errorList.size(),
                    RuntimeUtil.LINE_SEPARATOR))
            for (String s : errorList) {
                result.append("\t\t")
                result.append(s)
                result.append(RuntimeUtil.LINE_SEPARATOR)
            }
            Assertions.fail(result.toString())
        }
    }

    private static List<String> trim(List<String> list) {
        List<String> result = new ArrayList<>()
        for (String s : list) {
            result.add(s.trim().toLowerCase())
        }
        return result
    }

    private static boolean shouldVerify(List<String> list) {
        List<String> exceptedReports = new ArrayList<>()
        exceptedReports.add("ish.oncourse.certificate.attainment")
        exceptedReports.add("ish.oncourse.nonvetcertificate")
        exceptedReports.add("ish.oncourse.certificate")
        exceptedReports.add("ish.oncourse.certificate.transcript")
        //create tasks for those

        for (String s : list) {
            if (s.contains("<property name=\"keycode\" value=\"")) {
                // <property name="keyCode" value="ish.onCourse.studentListReport"/>
                String keycode = s.replace("<property name=\"keycode\" value=\"", "").replace("\"/>", "")
                if (exceptedReports.contains(keycode.toLowerCase())) {
                    return false
                }
            } else if (s.contains("<property name=\"issubreport\" value=\"true\"/>")) {
                return false
            }
        }

        return true
    }
}
