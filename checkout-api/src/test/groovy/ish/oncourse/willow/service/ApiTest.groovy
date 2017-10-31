package ish.oncourse.willow.service

import ish.oncourse.test.TestContext
import ish.oncourse.willow.WillowApiModule
import ish.oncourse.willow.cayenne.CayenneService
import org.apache.cayenne.configuration.server.ServerRuntime
import org.junit.After
import org.junit.Before

abstract class ApiTest {

    protected ServerRuntime cayenneRuntime
    protected CayenneService cayenneService
    private TestContext testContext

    @Before
    void setup() throws Exception {
        testContext = new TestContext().params([ (SHOULD_CREATE_SCHEMA) : false, (SHOULD_DROP_SCHEMA) : false]).open()
        testContext.cleanInsert(dataSetResource)
        cayenneRuntime = new ServerRuntime("cayenne-oncourse.xml", new WillowApiModule.WillowApiCayenneModule())
        cayenneService = new CayenneService(cayenneRuntime)
    }

    @After
    void after() {
        cayenneRuntime.shutdown()
        testContext.close()
    }

    protected abstract String getDataSetResource()

}
