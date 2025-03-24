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
import org.yaml.snakeyaml.Yaml

import java.nio.charset.Charset

@CompileStatic
class ReportTest {


    private static final int A4_WIDTH = 595
    private static final int A4_HEIGHT = 842

    private static List<String> exceptedReports = List.of("ish.oncourse.certificate.attainment",
            "ish.oncourse.nonvetcertificate",
            "ish.oncourse.certificate",
            "ish.oncourse.certificate.transcript")

    private static Collection<? extends Map<String, Object>> getPropsByPath(String path) {
        InputStream resourceAsStream = null
        try {
            resourceAsStream = ResourcesUtil.getResourceAsInputStream(path)
            Yaml yaml = new Yaml();
            def loaded = yaml.load(resourceAsStream);
            if (loaded instanceof LinkedHashMap<?, ?>)
                return List.of((Map<String, Object>) loaded);
            else
                return (Collection<? extends Map<String, Object>>) loaded;
        } catch (IOException ex) {
            //ignored
        } finally {
            if (resourceAsStream != null)
                resourceAsStream.close()
        }
        return null
    }

    static Collection<Arguments> values() throws IOException {

        def reportsList = PluginService.getPluggableResources(ResourceType.REPORT.getResourcePath(), ResourceType.REPORT.getFilePattern())
        Assertions.assertNotNull(reportsList)

        Collection<Arguments> dataList = new ArrayList<>()
        for (String reportFile : reportsList) {
            if (reportFile.endsWith(".yaml")) {
                def configs = getPropsByPath(reportFile)
                for (def config : configs) {
                    if (shouldVerify(config)) {
                        def path = config.get("report") as String
                        String fullPath = ResourceType.REPORT.getResourcePath() + path
                        List<String> untrimmedReport = IOUtils.readLines(ResourcesUtil.getResourceAsInputStream(fullPath), Charset.defaultCharset())
                        Assertions.assertNotNull(untrimmedReport)
                        List<String> report = trim(untrimmedReport)
                        Assertions.assertNotNull(report)
                        dataList.add(Arguments.of(fullPath))
                    }
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

    private static boolean shouldVerify(Map<String, Object> props) {
        return !exceptedReports.contains(props.get("keyCode") as String) && (props.get("isVisible") as Boolean).booleanValue()
    }
}
