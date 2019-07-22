package ish.oncourse.willow.service

import ish.oncourse.api.cayenne.CayenneService
import ish.oncourse.cayenne.WillowCayenneModuleBuilder
import ish.oncourse.test.LoadDataSet
import ish.oncourse.test.TestContext
import ish.oncourse.willow.FinancialService
import ish.oncourse.willow.filters.RequestFilter
import ish.oncourse.willow.service.impl.CollegeService
import org.apache.cayenne.configuration.server.ServerRuntime
import org.junit.After
import org.junit.Before

abstract class ApiTest {

    protected ServerRuntime cayenneRuntime
    protected CayenneService cayenneService
    private TestContext testContext
    protected FinancialService financialService
    protected CollegeService collegeService


    @Before
    void setup() throws Exception {
        RequestFilter.ThreadLocalSiteKey.set('mammoth')

        testContext = new TestContext().shouldCreateTables(true).open()
        new LoadDataSet().dataSetFile(dataSetResource).replacements(["[null]":null]).load(testContext.DS)
        cayenneRuntime = new ServerRuntime("cayenne-oncourse.xml", new WillowCayenneModuleBuilder().build())
        cayenneService = new CayenneService(cayenneRuntime)
        collegeService = new CollegeService(cayenneService)
        financialService = new FinancialService(cayenneService, collegeService)
    }

    @After
    void after() {
        cayenneRuntime.shutdown()
        testContext.close()
    }

    protected abstract String getDataSetResource()

}
