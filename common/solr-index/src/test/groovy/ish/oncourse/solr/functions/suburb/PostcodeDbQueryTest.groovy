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
        List<PostcodeDb> expectedList = new ArrayList<>()
        expectedList.add(postcodeDbContext.postcodeDb(1))
        expectedList.add(postcodeDbContext.postcodeDb(3))
        expectedList.add(postcodeDbContext.postcodeDb(2))

        List<PostcodeDb> actualList = Functions.PostcodesQuery.select(serverRuntime.newContext())
        Assert.assertTrue(expectedList.size() == actualList.size())
        Assert.assertTrue(expectedList.get(0).postcode == actualList.get(0).postcode)
        Assert.assertTrue(expectedList.get(1).postcode == actualList.get(2).postcode)
        Assert.assertTrue(expectedList.get(2).postcode == actualList.get(1).postcode)
    }

    @After
    void after() {
        testContext.close()
    }
}
