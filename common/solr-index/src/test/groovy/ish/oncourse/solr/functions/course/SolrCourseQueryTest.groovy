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
class SolrCourseQueryTest extends ASolrTest {
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
    void testGetCourseClassesWithoutSessions() throws IOException, SolrServerException {
        SolrClient solrClient = new EmbeddedSolrServer(h.getCore())

        cCollege.newCourse("course1").withCancelledClass("cancelled").build()
        cCollege.newCourse("course2").withDistantClass("distantLearning").build()
        cCollege.newCourse("course3").withInactiveClass("inactive").build()
        cCollege.newCourse("course4").withEnrolDisabledClass("enrolDisabled").build()
        cCollege.newCourse("course5").withWebInvisibleClass("webInvisible").build()
        cCollege.newCourse("course6").isWebVisible(false).withClass("courseWebInvisible").build()
        
        ReindexCoursesJob job = new ReindexCoursesJob(objectContext, solrClient)
        job.run()
        while (job.isActive()){
            Thread.sleep(100)
        }

        List<SCourse> actualSClasses = solrClient.query("courses",
                SolrQueryBuilder.valueOf(new SearchParams(s: "course"), cCollege.college.id.toString(), null, null).build())
                .getBeans(SCourse.class)
        assertEquals(5, actualSClasses.size())
        assertNull(actualSClasses.find {sCourse -> sCourse.name == "course6"})
        solrClient.close()
    }

    @After
    void after(){
        Schedulers.shutdown()
        // Can't drop DB cause 2 mariaDB threads is still working.
        // TODO: define mariaDB daemon threads and shut them down
        testContext.close(false)
    }
}
