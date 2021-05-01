package ish.oncourse.server.services


import groovy.transform.CompileStatic
import ish.CayenneIshTestCase
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.FundingUpload
import ish.oncourse.server.cayenne.FundingUploadOutcome
import ish.oncourse.types.FundingStatus
import org.apache.cayenne.access.DataContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.commons.lang3.time.DateUtils
import org.dbunit.dataset.ReplacementDataSet
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@CompileStatic
class FundingContractUpdateJobTest extends CayenneIshTestCase {

    private ICayenneService cayenneService

    private FundingContractUpdateJob fundingContractUpdateJob


    @BeforeEach
    void setup() throws Exception {
        wipeTables()
        // creating date, the object cannot be exactly the same as system time to allow safe comparison of time by delayed Income posting job
        Date date = DateUtils.addHours(DateUtils.truncate(new Date(), Calendar.DATE), 12)
        Date start1 = DateUtils.addDays(date, -30)
        Date start2 = DateUtils.addDays(date, -32)
        Date start3 = DateUtils.addDays(date, -20)
        Date start4 = DateUtils.addDays(date, -1)

        this.cayenneService = injector.getInstance(ICayenneService.class)

        InputStream st = FundingContractUpdateJobTest.class.getClassLoader().getResourceAsStream(
                "ish/oncourse/server/services/fundingContractUpdateJobTestDataSet.xml")
        FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder()
        builder.setColumnSensing(true)
        FlatXmlDataSet dataSet = builder.build(st)
        ReplacementDataSet rDataSet = new ReplacementDataSet(dataSet)

        rDataSet.addReplacementObject("[start_date1]", start1)
        rDataSet.addReplacementObject("[start_date2]", start2)
        rDataSet.addReplacementObject("[start_date3]", start3)
        rDataSet.addReplacementObject("[start_date4]", start4)
        rDataSet.addReplacementObject("[end_date1]", DateUtils.addHours(start1, 2))
        rDataSet.addReplacementObject("[end_date2]", DateUtils.addHours(start2, 2))
        rDataSet.addReplacementObject("[end_date3]", DateUtils.addHours(start3, 2))
        rDataSet.addReplacementObject("[end_date4]", DateUtils.addHours(start4, 2))
        rDataSet.addReplacementObject("[null]", null)

        executeDatabaseOperation(rDataSet)

        fundingContractUpdateJob = new FundingContractUpdateJob(cayenneService)
        super.setup()
    }

    @AfterEach
    void tearDown() {
        wipeTables()
    }

    @Test
    void testFundingContractJob() {
        DataContext newContext = cayenneService.getNewContext()

        Assertions.assertEquals(23, ObjectSelect.query(FundingUpload.class).select(newContext).size())
        Assertions.assertEquals(23, ObjectSelect.query(FundingUploadOutcome.class).select(newContext).size())
        Assertions.assertEquals(9, ObjectSelect.query(FundingUpload.class).where(FundingUpload.STATUS.eq(FundingStatus.EXPORTED)).select(newContext).size())
        Assertions.assertEquals(10, ObjectSelect.query(FundingUpload.class).where(FundingUpload.STATUS.eq(FundingStatus.FAILED)).select(newContext).size())
        Assertions.assertEquals(4, ObjectSelect.query(FundingUpload.class).where(FundingUpload.STATUS.eq(FundingStatus.SUCCESS)).select(newContext).size())

        fundingContractUpdateJob.execute()

        Assertions.assertEquals(9, ObjectSelect.query(FundingUpload.class).select(newContext).size())
        Assertions.assertEquals(9, ObjectSelect.query(FundingUploadOutcome.class).select(newContext).size())
        Assertions.assertEquals(2, ObjectSelect.query(FundingUpload.class).where(FundingUpload.STATUS.eq(FundingStatus.EXPORTED)).select(newContext).size())
        Assertions.assertEquals(3, ObjectSelect.query(FundingUpload.class).where(FundingUpload.STATUS.eq(FundingStatus.FAILED)).select(newContext).size())
        Assertions.assertEquals(4, ObjectSelect.query(FundingUpload.class).where(FundingUpload.STATUS.eq(FundingStatus.SUCCESS)).select(newContext).size())
    }
}
