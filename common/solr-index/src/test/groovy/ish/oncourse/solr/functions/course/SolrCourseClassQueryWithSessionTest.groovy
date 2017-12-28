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
import ish.oncourse.test.context.CCourse
import ish.oncourse.test.context.CCourseClass
import ish.oncourse.test.context.CRoom
import ish.oncourse.test.context.CSession
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
        cCollege = dataContext.college("College-Australia/Sydney", "Australia/Sydney")
    }

    @Test
    void testGetCourseClassesWithSessions() throws IOException, SolrServerException {
        SolrClient solrClient = new EmbeddedSolrServer(h.getCore())

        CCourse course1 = cCollege.cCourse("course1").detail("course1 Details").build()
        CCourse course2 = cCollege.cCourse("course2").detail("course2 Details").build()
        CCourse course3 = cCollege.cCourse("course3").detail("course3 Details").build()
        CCourse course4 = cCollege.cCourse("course4").detail("course4 Details").build()
        CCourse course5 = cCollege.cCourse("course5").detail("course5 Details").build()
        CCourse course6 = cCollege.cCourse("course6").detail("course6 Details").build()
        CCourse course7 = cCollege.cCourse("course7").detail("course7 Details").build()
        CCourse course8 = cCollege.cCourse("course8").detail("course8 Details").build()
        CCourse course9 = cCollege.cCourse("course9").detail("course9 Details").build()
        
        CCourseClass past = CCourseClass.instance(objectContext, "past", course1.course).build()
        CSession.instance(objectContext, past.courseClass).startDate(new Date() - 20).endDate(new Date() - 19).build()
        CSession.instance(objectContext, past.courseClass).startDate(new Date() - 10).endDate(new Date() - 9).build()

        CCourseClass pastStartsFirst = CCourseClass.instance(objectContext, "pastStartsFirst", course2.course).build()
        CSession.instance(objectContext, pastStartsFirst.courseClass).startDate(new Date() - 30).endDate(new Date() - 29).build()
        CSession.instance(objectContext, pastStartsFirst.courseClass).startDate(new Date() - 10).endDate(new Date() - 9).build()

        CCourseClass pastEndsLast = CCourseClass.instance(objectContext, "pastEndsLast", course3.course).build()
        CSession.instance(objectContext, pastEndsLast.courseClass).startDate(new Date() - 20).endDate(new Date() - 19).build()
        CSession.instance(objectContext, pastEndsLast.courseClass).startDate(new Date() - 5).endDate(new Date() - 4).build()

        CCourseClass current = CCourseClass.instance(objectContext, "current", course4.course).build()
        CSession.instance(objectContext, current.courseClass).startDate(new Date() - 10).endDate(new Date() - 9).build()
        CSession.instance(objectContext, current.courseClass).startDate(new Date() + 10).endDate(new Date() + 11).build()

        CCourseClass currentStartsFirst = CCourseClass.instance(objectContext, "currentStartsFirst", course5.course).build()
        CSession.instance(objectContext, currentStartsFirst.courseClass).startDate(new Date() - 15).endDate(new Date() - 14).build()
        CSession.instance(objectContext, currentStartsFirst.courseClass).startDate(new Date() + 10).endDate(new Date() + 11).build()

        CCourseClass currentEndsLast = CCourseClass.instance(objectContext, "currentEndsLast", course6.course).build()
        CSession.instance(objectContext, currentEndsLast.courseClass).startDate(new Date() - 10).endDate(new Date() - 9).build()
        CSession.instance(objectContext, currentEndsLast.courseClass).startDate(new Date() + 15).endDate(new Date() + 16).build()

        CCourseClass future = CCourseClass.instance(objectContext, "future", course7.course).build()
        CSession.instance(objectContext, future.courseClass).startDate(new Date() + 10).endDate(new Date() + 11).build()
        CSession.instance(objectContext, future.courseClass).startDate(new Date() + 20).endDate(new Date() + 21).build()

        CCourseClass futureStartsFirst = CCourseClass.instance(objectContext, "futureStartsFirst", course8.course).build()
        CSession.instance(objectContext, futureStartsFirst.courseClass).startDate(new Date() + 5).endDate(new Date() + 6).build()
        CSession.instance(objectContext, futureStartsFirst.courseClass).startDate(new Date() + 20).endDate(new Date() + 21).build()

        CCourseClass futureEndsLast = CCourseClass.instance(objectContext, "futureEndsLast", course9.course).build()
        CSession.instance(objectContext, futureEndsLast.courseClass).startDate(new Date() + 10).endDate(new Date() + 11).build()
        CSession.instance(objectContext, futureEndsLast.courseClass).startDate(new Date() + 30).endDate(new Date() + 31).build()
        

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
