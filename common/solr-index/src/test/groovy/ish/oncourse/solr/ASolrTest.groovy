package ish.oncourse.solr

import com.carrotsearch.randomizedtesting.annotations.ThreadLeakScope
import io.reactivex.schedulers.Schedulers
import ish.oncourse.solr.reindex.ReindexCourses
import ish.oncourse.test.TestContext
import ish.oncourse.test.context.CCollege
import ish.oncourse.test.context.DataContext
import org.apache.cayenne.ObjectContext
import org.apache.solr.SolrTestCaseJ4
import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
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

    protected TestContext testContext
    protected ObjectContext objectContext
    protected InitSolr initSolr
    protected CCollege cCollege
    protected SolrClient solrClient

    protected void initSolr() throws Exception {
        initSolr = InitSolr.coursesCore()
        initSolr.init()
    }

    @Before
    void before() throws Exception {
        initSolr()
        testContext = new TestContext()
        testContext.open()
        objectContext = testContext.getServerRuntime().newContext()
        DataContext dataContext = new DataContext(objectContext: objectContext)
        cCollege = dataContext.newCollege()
        solrClient = new EmbeddedSolrServer(h.core)
    }


    @After
    void after() {
        Schedulers.shutdown()

        // Can't drop DB cause 2 mariaDB threads is still working.
        // TODO: define mariaDB daemon threads and shut them down (OD-11304)
        testContext.close(false)
    }


    long fullImport() {
        ReindexCourses reindex = new ReindexCourses(testContext.getServerRuntime().newContext(), solrClient)
        reindex.run()
        if (reindex.error != null) throw new RuntimeException(reindex.error)
        return reindex.total
    }

}
