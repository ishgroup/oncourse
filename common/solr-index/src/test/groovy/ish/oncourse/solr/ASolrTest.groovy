package ish.oncourse.solr

import com.carrotsearch.randomizedtesting.annotations.ThreadLeakScope
import io.reactivex.schedulers.Schedulers
import ish.oncourse.test.TestContext
import ish.oncourse.test.context.CCollege
import ish.oncourse.test.context.DataContext
import org.apache.cayenne.ObjectContext
import org.apache.solr.SolrTestCaseJ4
import org.junit.After
import org.junit.Before

/**
 * User: akoiro
 * Date: 4/1/18
 */
@ThreadLeakScope(ThreadLeakScope.Scope.NONE)
abstract class ASolrTest extends SolrTestCaseJ4 {
    static {
        System.setProperty("test.solr.allowed.securerandom", "NativePRNG")
        System.setProperty("tests.timezone", "Australia/Sydney")
        System.setProperty("tests.locale", "en-AU")
    }

    TestContext testContext
    ObjectContext objectContext
    InitSolr initSolr
    CCollege cCollege

    @Before
    void before() throws Exception {
        initSolr()
        testContext = new TestContext()
        testContext.open()
        objectContext = testContext.getServerRuntime().newContext()
        DataContext dataContext = new DataContext(objectContext: objectContext)
        cCollege = dataContext.newCollege()
    }

    protected void initSolr() throws Exception {
        initSolr = InitSolr.coursesCore()
        initSolr.init()
    }

    @After
    void after() {
        Schedulers.shutdown()

        // Can't drop DB cause 2 mariaDB threads is still working.
        // TODO: define mariaDB daemon threads and shut them down (OD-11304)
        testContext.close(false)
    }
}
