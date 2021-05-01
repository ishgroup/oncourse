package ish.oncourse.server.print

import groovy.transform.CompileStatic
import ish.oncourse.common.ResourcesUtil
import org.apache.commons.io.IOUtils
import org.junit.Ignore
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@Ignore
@RunWith(Parameterized.class)
class ReportPrintingPart2Test extends ReportPrintingTest {

    @CompileStatic
    ReportPrintingPart2Test(String reportCode, String sourceEntity, String reportFolder) {
        super(reportCode, sourceEntity, reportFolder)
    }

    @CompileStatic
    @Parameterized.Parameters(name = "{0}")
    static Collection<String[]> reportCodes() throws Exception {

        List<String> reportsList = IOUtils.readLines(ResourcesUtil.getResourceAsInputStream("reports/manifest"))
        List<String[]> keyCodeList = new ArrayList<>()

        for (int i = TEST_BUNCH_SIZE; i < reportsList.size(); i++) {
            String reportFile = reportsList.get(i)
            prepareReport(reportFile, keyCodeList)
        }

        return keyCodeList
    }

}
