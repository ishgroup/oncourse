package ish.oncourse.server.quality


import ish.oncourse.server.cayenne.QualityRule
import org.apache.cayenne.query.Select
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

import static org.mockito.Matchers.any
import static org.mockito.Mockito.when

class OutcomeResultTest {
    
    @Test
    void test() {
        ScriptTestService testService = new ScriptTestService()
        OutcomeResultData outcomeResultData = new OutcomeResultData()

        def data = outcomeResultData.testData()
        when(testService.context.select(any(Select))).thenReturn(data)

        QualityRule qualityRule = testService.getQualityRuleBy("outcomeResult")

        def result = testService.qualityService.performRuleCheck(qualityRule)
        Assertions.assertEquals(3, result.size())

        outcomeResultData.assetResults(result)
    }
}
