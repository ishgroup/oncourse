package ish.oncourse.solr.functions.course

import io.reactivex.schedulers.Schedulers
import ish.oncourse.solr.ASolrTest
import ish.oncourse.solr.InitSolr
import ish.oncourse.solr.model.SCourse
import ish.oncourse.solr.query.SearchParams
import ish.oncourse.solr.query.SolrQueryBuilder
import ish.oncourse.solr.reindex.ReindexCoursesJob
import ish.oncourse.test.TestContext
import ish.oncourse.test.context.CCollege
import ish.oncourse.test.context.DataContext
import org.apache.cayenne.ObjectContext
import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.SolrServerException
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Created by alex on 12/27/17.
 */
class SolrCourseClassQueryWithSessionTest extends ASolrTest{
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

        cCollege.newCourse("course1").newCourseClass("past")
                .withSession(-5).withSession(-4).build()
        cCollege.newCourse("course2").newCourseClass("pastStartsFirst")
                .withSession( -6).withSession( -4).build()
        cCollege.newCourse("course3").newCourseClass("pastEndsLast")
                .withSession( -5).withSession(-3).build()
        
        cCollege.newCourse("course4").newCourseClass("current")
                .withSession(-1).withSession(1).build()
        cCollege.newCourse("course5").newCourseClass("currentStartsFirst")
                .withSession(-2).withSession(1).build()
        cCollege.newCourse("course6").newCourseClass("currentEndsLast")
                .withSession(-1).withSession(2).build()
        
        cCollege.newCourse("course7").newCourseClass("future")
                .withSession(5).withSession(6).build()
        cCollege.newCourse("course8").newCourseClass("futureStartsFirst")
                .withSession(4).withSession(6).build()
        cCollege.newCourse("course9").newCourseClass("futureEndsLast")
                .withSession(5).withSession(7).build()
                

        ReindexCoursesJob job = new ReindexCoursesJob(objectContext, solrClient)
        job.run()
        while (job.isActive()){
            Thread.sleep(100)
        }

        List<SCourse> actualSClasses = solrClient.query("courses",
                SolrQueryBuilder.valueOf(new SearchParams(s: "course"), cCollege.college.id.toString(), 0, 10).build())
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
