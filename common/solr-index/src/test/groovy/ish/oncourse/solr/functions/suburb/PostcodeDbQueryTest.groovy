package ish.oncourse.solr.functions.suburb

import ish.oncourse.model.PostcodeDb
import ish.oncourse.test.TestContext
import ish.oncourse.test.context.CPostcodeDb
import org.apache.cayenne.ObjectContext
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
    private ObjectContext context

    @Before
    void before() {

        testContext = new TestContext()
        testContext.open()
        context = testContext.getRuntime().newContext()
    }

    @Test
    void test() {
        CPostcodeDb.instance(context, 1).build()
        CPostcodeDb.instance(context, 3).build()
        CPostcodeDb.instance(context, 2).build()

        List<PostcodeDb> actualOrderedList = Functions.PostcodesQuery.select(context)
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
