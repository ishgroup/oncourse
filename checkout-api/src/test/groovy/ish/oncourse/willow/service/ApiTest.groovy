package ish.oncourse.willow.service

import ish.oncourse.api.cayenne.WillowApiCayenneModule
import ish.oncourse.test.LoadDataSet
import ish.oncourse.test.TestContext
import ish.oncourse.api.cayenne.CayenneService
import org.apache.cayenne.configuration.server.ServerRuntime
import org.junit.After
import org.junit.Before

abstract class ApiTest {

    protected ServerRuntime cayenneRuntime
    protected CayenneService cayenneService
    private TestContext testContext

    @Before
    void setup() throws Exception {
        testContext = new TestContext().shouldCreateTables(true).open()
        new LoadDataSet().dataSetFile(dataSetResource).replacements(["[null]":null]).load(testContext.DS)
        cayenneRuntime = new ServerRuntime("cayenne-oncourse.xml", new WillowApiCayenneModule())
        cayenneService = new CayenneService(cayenneRuntime)
    }

    @After
    void after() {
        cayenneRuntime.shutdown()
        testContext.close()
    }

    protected abstract String getDataSetResource()

}
