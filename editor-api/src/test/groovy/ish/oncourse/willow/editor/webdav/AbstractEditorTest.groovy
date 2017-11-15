package ish.oncourse.willow.editor.webdav

import ish.oncourse.api.cayenne.CayenneService
import ish.oncourse.api.cayenne.WillowApiCayenneModule
import ish.oncourse.test.LoadDataSet
import ish.oncourse.test.TestContext
import org.apache.cayenne.configuration.server.ServerRuntime
import org.junit.After
import org.junit.Before

abstract class AbstractEditorTest {

    protected ServerRuntime cayenneRuntime
    protected CayenneService  cayenneService
    private TestContext testContext

    @Before
    void setup() throws Exception {
        testContext = new TestContext().shouldCreateTables(false).open()
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
