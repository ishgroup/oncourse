package ish.oncourse.willow.service

import ish.oncourse.api.cayenne.CayenneService
import ish.oncourse.cayenne.WillowCayenneModuleBuilder
import ish.oncourse.test.TestContext
import org.apache.cayenne.configuration.server.ServerRuntime
import org.junit.After
import org.junit.Before

abstract class NoDbApiTest {

    protected ServerRuntime cayenneRuntime
    protected CayenneService cayenneService
    private TestContext testContext

    @Before
    void setup() throws Exception {
        testContext = new TestContext().shouldCreateTables(false).open()
        cayenneRuntime = new ServerRuntime("cayenne-oncourse.xml", new WillowCayenneModuleBuilder().build())
        cayenneService = new CayenneService(cayenneRuntime)
    }

    @After
    void after() {
        cayenneRuntime.shutdown()
        testContext.close()
    }
}
