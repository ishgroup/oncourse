package ish.oncourse.willow.editor.service

import ish.oncourse.api.cayenne.CayenneService
import ish.oncourse.api.cayenne.WillowApiCayenneModule
import ish.oncourse.test.LoadDataSet
import ish.oncourse.test.TestContext
import ish.oncourse.willow.editor.services.RequestService
import org.apache.cayenne.configuration.server.ServerRuntime
import org.eclipse.jetty.server.Request
import org.junit.After
import org.junit.Before

abstract class AbstractEditorTest {

    protected ServerRuntime cayenneRuntime
    protected CayenneService  cayenneService
    protected RequestService requestService
    private TestContext testContext

    @Before
    void setup() throws Exception {
        testContext = new TestContext().shouldCreateTables(false).open()
        new LoadDataSet().dataSetFile(dataSetResource).replacements(["[null]":null]).load(testContext.DS)
        cayenneRuntime = new ServerRuntime("cayenne-oncourse.xml", new WillowApiCayenneModule())
        cayenneService = new CayenneService(cayenneRuntime)
        Request request = [getServerName: 'mamoth.oncourse.cc'] as Request
        requestService = [getRequest: request] as RequestService
    }

    @After
    void after() {
        cayenneRuntime.shutdown()
        testContext.close()
    }

    protected abstract String getDataSetResource()
}
