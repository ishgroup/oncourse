package ish.oncourse.solr.functions.course

import io.reactivex.schedulers.Schedulers
import ish.oncourse.solr.InitSolr
import ish.oncourse.solr.model.DataContext
import ish.oncourse.solr.model.SCourse
import ish.oncourse.solr.reindex.ReindexCoursesJob
import ish.oncourse.test.TestContext
import ish.oncourse.test.context.CCollege
import ish.oncourse.test.context.CCourse
import ish.oncourse.test.context.CCourseClass
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

        CCourse course = cCollege.cCourse("course").build()
        CCourseClass past = CCourseClass.instance(objectContext, "past", course.course).startDate(new Date() - 20).endDate(new Date() - 10).build()
        CCourseClass pastStartsFirst = CCourseClass.instance(objectContext, "pastStartsFirst", course.course).startDate(new Date() - 30).endDate(new Date() - 15).build()
        CCourseClass pastEndsLast = CCourseClass.instance(objectContext, "pastEndsLast", course.course).startDate(new Date() - 20).endDate(new Date() - 5).build()
        CCourseClass current = CCourseClass.instance(objectContext, "current", course.course).startDate(new Date() - 10).endDate(new Date() + 10).build()
        CCourseClass currentStartsFirst = CCourseClass.instance(objectContext, "currentStartsFirst", course.course).startDate(new Date() - 15).endDate(new Date() + 5).build()
        CCourseClass currentEndsLast = CCourseClass.instance(objectContext, "currentEndsLast", course.course).startDate(new Date() - 5).endDate(new Date() + 15).build()
        CCourseClass futureClass = CCourseClass.instance(objectContext, "futureClass", course.course).startDate(new Date() + 10).endDate(new Date() + 20).build()
        CCourseClass futureStartsFirst = CCourseClass.instance(objectContext, "futureStartsFirst", course.course).startDate(new Date() + 5).endDate(new Date() + 15).build()
        CCourseClass futureEndsLast = CCourseClass.instance(objectContext, "futureEndsLast", course.course).startDate(new Date() + 15).endDate(new Date() + 25).build()

        ReindexCoursesJob job = new ReindexCoursesJob(objectContext, solrClient)
        job.run()
        while (job.isActive()){
            Thread.sleep(100)
        }

        List<SCourse> actualSClasses = solrClient.query("courses", new SolrQuery("course")).getBeans(SCourse.class)
        assertEquals(6, actualSClasses.size())

        solrClient.close()
    }

    @After
    void after(){
        Schedulers.shutdown()
        testContext.close()
    }
}
