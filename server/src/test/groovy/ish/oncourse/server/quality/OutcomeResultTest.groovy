package ish.oncourse.server.quality

import ish.oncourse.server.cayenne.QualityRule
import org.apache.cayenne.query.Select
import org.junit.jupiter.api.Test

import static org.junit.Assert.assertEquals
import static org.mockito.Matchers.any
import static org.mockito.Mockito.when

/**
 * Created by akoiro on 17/03/2016.
 */
class OutcomeResultTest {
    @Test
    void test() {
        ScriptTestService testService = new ScriptTestService()
        OutcomeResultData outcomeResultData = new OutcomeResultData()

        def data = outcomeResultData.testData()
        when(testService.context.select(any(Select))).thenReturn(data)

        QualityRule qualityRule = testService.getQualityRuleBy("outcomeResult")

        def result = testService.qualityService.performRuleCheck(qualityRule)
        assertEquals(3, result.size())

        outcomeResultData.assetResults(result)
    }
}
