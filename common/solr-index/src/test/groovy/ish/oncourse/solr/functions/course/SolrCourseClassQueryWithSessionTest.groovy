package ish.oncourse.solr.functions.course

import com.carrotsearch.randomizedtesting.annotations.ThreadLeakScope
import io.reactivex.schedulers.Schedulers
import ish.oncourse.solr.InitSolr
import ish.oncourse.solr.model.SCourse
import ish.oncourse.solr.query.SolrQueryBuilder
import ish.oncourse.solr.reindex.ReindexCoursesJob
import ish.oncourse.test.TestContext
import ish.oncourse.test.context.CCollege
import ish.oncourse.test.context.DataContext
import org.apache.cayenne.ObjectContext
import org.apache.solr.SolrTestCaseJ4
import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.SolrQuery
import org.apache.solr.client.solrj.SolrServerException
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Created by alex on 12/27/17.
 */
@ThreadLeakScope(ThreadLeakScope.Scope.NONE)
class SolrCourseClassQueryWithSessionTest extends SolrTestCaseJ4{
    private TestContext testContext
    private ObjectContext objectContext
    private InitSolr initSolr
    private CCollege cCollege

    @Before
    void before() throws Exception {
        initSolr = InitSolr.coursesCore()
        initSolr.init()

        testContext = new TestContext()
        testContext.open()
        objectContext = testContext.getServerRuntime().newContext()
        DataContext dataContext = new DataContext(objectContext: objectContext)
        cCollege = dataContext.newCollege()
    }

    @Test
    void testGetCourseClassesWithSessions() throws IOException, SolrServerException {
        SolrClient solrClient = new EmbeddedSolrServer(h.getCore())
        Date now = new Date()

        cCollege.cCourse("course1").cCourseClass("past")
                .withSession(now - 5).withSession(now - 4).build()
        cCollege.cCourse("course2").cCourseClass("pastStartsFirst")
                .withSession(now - 6).withSession(now - 4).build()
        cCollege.cCourse("course3").cCourseClass("pastEndsLast")
                .withSession(now - 5).withSession(now - 3).build()
        
        cCollege.cCourse("course4").cCourseClass("current")
                .withSession(now - 1).withSession(now + 1).build()
        cCollege.cCourse("course5").cCourseClass("currentStartsFirst")
                .withSession(now - 2).withSession(now + 1).build()
        cCollege.cCourse("course6").cCourseClass("currentEndsLast")
                .withSession(now - 1).withSession(now + 2).build()
        
        cCollege.cCourse("course7").cCourseClass("future")
                .withSession(now + 5).withSession(now + 6).build()
        cCollege.cCourse("course8").cCourseClass("futureStartsFirst")
                .withSession(now + 4).withSession(now + 6).build()
        cCollege.cCourse("course9").cCourseClass("futureEndsLast")
                .withSession(now + 5).withSession(now + 7).build()
                

        ReindexCoursesJob job = new ReindexCoursesJob(objectContext, solrClient)
        job.run()
        while (job.isActive()){
            Thread.sleep(100)
        }

        List<SCourse> actualSClasses = solrClient.query("courses",
                new SolrQuery()
                        .setRequestHandler(SolrQueryBuilder.QUERY_TYPE)
                        .setParam(SolrQueryBuilder.PARAMETER_fl, "*"))
                .getBeans(SCourse.class)
        assertEquals(9, actualSClasses.size())

        solrClient.close()
    }

    @After
    void after(){
        Schedulers.shutdown()
        testContext.close()
    }
}
