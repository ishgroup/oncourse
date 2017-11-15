package ish.oncourse.solr.functions.suburb

import ish.oncourse.model.PostcodeDb
import ish.oncourse.solr.model.PostcodeDbContext
import ish.oncourse.test.TestContext
import org.apache.cayenne.configuration.server.ServerRuntime
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

import static ish.oncourse.test.functions.Functions.createRuntime

/**
 * Created by alex on 11/13/17.
 */
class PostcodeDbQueryTest {
    private TestContext testContext
    private ServerRuntime serverRuntime
    private PostcodeDbContext postcodeDbContext

    @Before
    void before() {

        testContext = new TestContext()
        testContext.open()
        serverRuntime = createRuntime()
        postcodeDbContext = new PostcodeDbContext(context: serverRuntime.newContext())
    }

    @Test
    void test() {
        postcodeDbContext.postcodeDb(1)
        postcodeDbContext.postcodeDb(3)
        postcodeDbContext.postcodeDb(2)

        List<PostcodeDb> actualOrderedList = Functions.PostcodesQuery.select(serverRuntime.newContext())
        Assert.assertEquals(3, actualOrderedList.size())
        for (int i = 1; i < actualOrderedList.size(); i++) {
            Assert.assertTrue("query returns tags ordered by postcode in asc, each item's postcode must be not bigger that next one", actualOrderedList.get(i).postcode >= actualOrderedList.get(i-1).postcode)
        }
    }

    @After
    void after() {
        testContext.close()
    }
}
