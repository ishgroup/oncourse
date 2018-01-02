package ish.oncourse.solr

import com.carrotsearch.randomizedtesting.annotations.ThreadLeakScope
import io.reactivex.schedulers.Schedulers
import ish.oncourse.solr.model.SCourse
import ish.oncourse.solr.query.SolrQueryBuilder
import ish.oncourse.solr.reindex.ReindexCoursesJob
import ish.oncourse.test.TestContext
import ish.oncourse.test.context.CCollege
import ish.oncourse.test.context.CCourse
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
 * Created by alex on 11/22/17.
 */
@ThreadLeakScope(ThreadLeakScope.Scope.NONE)
class ReindexCourseTest extends SolrTestCaseJ4 {
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
        cCollege = dataContext.newCollege("College-Australia/Sydney", "Australia/Sydney")
    }

    @Test
    void testReindexCourse() throws IOException, SolrServerException {
        SolrClient solrClient = new EmbeddedSolrServer(h.getCore())

        CCourse course1 = cCollege.newCourse("course1")
        course1.detail("course1 Details").build()
        CCourse course2 = cCollege.newCourse("course2")
        course2.detail("course2 Details").build()

        SCourse expectedSCourse = new SCourse()
        expectedSCourse.setId(course1.course.id.toString())
        expectedSCourse.setCollegeId(course1.course.college.id)
        expectedSCourse.setName(course1.course.name)
        expectedSCourse.setDetail(course1.course.detail)
        expectedSCourse.setCode(course1.course.code)

        ReindexCoursesJob job = new ReindexCoursesJob(objectContext, solrClient)
        job.run()
        while (job.isActive()) {
            Thread.sleep(100)
        }

        List<SCourse> actualSClasses = solrClient.query("courses",
                new SolrQuery()
                        .setRequestHandler(SolrQueryBuilder.QUERY_TYPE)
                        .setParam(SolrQueryBuilder.PARAMETER_fl, "*"))
                .getBeans(SCourse.class)
        assertEquals(2, actualSClasses.size())
        assertNotNull(actualSClasses.find { c -> c.id == expectedSCourse.id })
        assertNotNull(actualSClasses.find { c -> c.collegeId == expectedSCourse.collegeId })
        assertNotNull(actualSClasses.find { c -> c.name == expectedSCourse.name })
        assertNotNull(actualSClasses.find { c -> c.detail == expectedSCourse.detail })
        assertNotNull(actualSClasses.find { c -> c.code == expectedSCourse.code })

        solrClient.close()
    }

    @After
    void after() {
        Schedulers.shutdown()
        testContext.close()
    }
}
