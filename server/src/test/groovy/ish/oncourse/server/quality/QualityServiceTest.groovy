package ish.oncourse.server.quality


import groovy.transform.CompileStatic
import ish.TestWithDatabase
import ish.quality.QualityResult
import org.apache.commons.lang3.time.DateUtils
import org.dbunit.dataset.ReplacementDataSet
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@CompileStatic
class QualityServiceTest extends TestWithDatabase {

    
    @BeforeEach
    void setupTest() throws Exception {
        wipeTables()
        InputStream st = QualityServiceTest.class.getClassLoader().getResourceAsStream("ish/oncourse/server/quality/qualityServiceTest.xml")
        FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st)

        ReplacementDataSet rDataSet = new ReplacementDataSet(dataSet)
        Date start1 = DateUtils.addDays(new Date(), -2)
        Date start2 = DateUtils.addDays(new Date(), -2)
        rDataSet.addReplacementObject("[start_date1]", start1)
        rDataSet.addReplacementObject("[start_date2]", start2)
        rDataSet.addReplacementObject("[end_date1]", DateUtils.addHours(start1, 2))
        rDataSet.addReplacementObject("[end_date2]", DateUtils.addHours(start2, 2))

        executeDatabaseOperation(rDataSet)
    }

    
    @Test
    void testRuleCheckResults() {
        List<QualityResult> results = new ArrayList<>(injector.getInstance(QualityService.class).performRuleCheck("testCode"))

        Assertions.assertEquals(1, results.size())
        Assertions.assertEquals(2, results.get(0).getRecords().size())
        Assertions.assertEquals("testCode", results.get(0).getRuleCode())
        Assertions.assertEquals("Contact", results.get(0).getEntity())
        Assertions.assertEquals("test result", results.get(0).getDescription())
        Assertions.assertTrue(results.get(0).getRecords().containsAll(Arrays.asList(2L, 3L)))
    }

    
    @Test
    void testResultStorage() {
        QualityService qualityService =
                injector.getInstance(QualityService.class)
        qualityService.performRuleCheck("testCode")

        Assertions.assertEquals(1, qualityService.getAllResults().size())
        Assertions.assertEquals(1, qualityService.getEntityQualityCheckResults("Contact").size())
        Assertions.assertEquals(0, qualityService.getEntityQualityCheckResults("Enrolment").size())
        Assertions.assertEquals(1, qualityService.getQualityCheckResults("testCode").size())
        Assertions.assertEquals(0, qualityService.getQualityCheckResults("testCode1").size())
        Assertions.assertEquals(0, qualityService.getQualityCheckResults("Contact", 1L).size())
        Assertions.assertEquals(1, qualityService.getQualityCheckResults("Contact", 2L).size())
        Assertions.assertEquals(1, qualityService.getQualityCheckResults("Contact", 3L).size())

        qualityService.performRuleCheck("testCode1")

        Assertions.assertEquals(2, qualityService.getAllResults().size())
        Assertions.assertEquals(2, qualityService.getEntityQualityCheckResults("Contact").size())
        Assertions.assertEquals(0, qualityService.getEntityQualityCheckResults("Enrolment").size())
        Assertions.assertEquals(1, qualityService.getQualityCheckResults("testCode").size())
        Assertions.assertEquals(1, qualityService.getQualityCheckResults("testCode1").size())
        Assertions.assertEquals(0, qualityService.getQualityCheckResults("Contact", 1L).size())
        Assertions.assertEquals(2, qualityService.getQualityCheckResults("Contact", 2L).size())
        Assertions.assertEquals(1, qualityService.getQualityCheckResults("Contact", 3L).size())
    }
}
