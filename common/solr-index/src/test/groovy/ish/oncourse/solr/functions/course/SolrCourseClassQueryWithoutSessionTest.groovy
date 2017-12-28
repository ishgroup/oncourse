package ish.oncourse.solr.functions.course

import com.carrotsearch.randomizedtesting.annotations.ThreadLeakScope
import io.reactivex.schedulers.Schedulers
import ish.oncourse.solr.InitSolr
import ish.oncourse.solr.model.DataContext
import ish.oncourse.solr.model.SCourse
import ish.oncourse.solr.query.SolrQueryBuilder
import ish.oncourse.solr.reindex.ReindexCoursesJob
import ish.oncourse.test.TestContext
import ish.oncourse.test.context.CCollege
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
 * Created by alex on 12/26/17.
 */
@ThreadLeakScope(ThreadLeakScope.Scope.NONE)
class SolrCourseClassQueryWithoutSessionTest extends SolrTestCaseJ4{
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
        cCollege = dataContext.college("College-Australia/Sydney", "Australia/Sydney")
    }
    
    @Test
    void testGetCourseClassesWithoutSessions() throws IOException, SolrServerException {
        SolrClient solrClient = new EmbeddedSolrServer(h.getCore())
        Date now = new Date()
        
        cCollege.cCourse("course1").withClass("past", now - 11).build()
        cCollege.cCourse("course2").withClass("pastStartsFirst", now - 12).build()
        cCollege.cCourse("course3").withClass("pastEndsLast", now - 10).build()
        
        cCollege.cCourse("course4").withClass("current", now - 5).build()
        cCollege.cCourse("course5").withClass("curStartsFirst", now - 6).build()
        cCollege.cCourse("course6").withClass("currentEndsLast", now - 4).build()
        
        cCollege.cCourse("course7").withClass("future", now + 5).build()
        cCollege.cCourse("course8").withClass("futureStartsFirst", now + 4).build()
        cCollege.cCourse("course9").withClass("futureEndsLast", now + 6).build()
        
        
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
