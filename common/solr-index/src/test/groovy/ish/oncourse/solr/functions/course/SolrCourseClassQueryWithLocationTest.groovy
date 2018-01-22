package ish.oncourse.solr.functions.course

import io.reactivex.schedulers.Schedulers
import ish.oncourse.solr.ASolrTest
import ish.oncourse.solr.InitSolr
import ish.oncourse.solr.model.SCourse
import ish.oncourse.solr.query.SearchParams
import ish.oncourse.solr.query.SolrQueryBuilder
import ish.oncourse.solr.query.Suburb
import ish.oncourse.solr.reindex.ReindexCoursesJob
import ish.oncourse.test.TestContext
import ish.oncourse.test.context.CCollege
import ish.oncourse.test.context.DataContext
import org.apache.cayenne.ObjectContext
import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Created by alex on 1/11/18.
 */
class SolrCourseClassQueryWithLocationTest extends ASolrTest{
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
    void testSortCoursesWithLocation(){
        SolrClient solrClient = new EmbeddedSolrServer(h.getCore())

        cCollege.newCourse("course1").withClassWithSiteLocation("course1",15,15).build()
        cCollege.newCourse("course2").withClassWithSiteLocation("course2",20,15).build()
        cCollege.newCourse("course3").withClassWithSiteLocation("course3",15,20).build()
        cCollege.newCourse("course4").withClassWithSiteLocation("tooFarCourse",20,20).build()
        cCollege.newCourse("course5").withSelfPacedClass("withoutLocation").build()

        ReindexCoursesJob job = new ReindexCoursesJob(objectContext, solrClient)
        job.run()
        while (job.isActive()){
            Thread.sleep(100)
        }
        
        Suburb location = new Suburb()
        location.latitude = 15
        location.longitude = 15
        location.distance = 540

        List<SCourse> actualSCourses = solrClient.query("courses",
                SolrQueryBuilder.valueOf(new SearchParams(s: "course*", suburbs: Arrays.asList(location)), cCollege.college.id.toString(), null, null).build())
                .getBeans(SCourse.class)
        assertEquals(2, actualSCourses.size())
        assertTrue(actualSCourses.first().name == "course1" )
        assertTrue(actualSCourses.last().name == "course2" )
        
        location.distance = 560
        actualSCourses = solrClient.query("courses",
                SolrQueryBuilder.valueOf(new SearchParams(s: "course*", suburbs: Arrays.asList(location)), cCollege.college.id.toString(), null, null).build())
                .getBeans(SCourse.class)
        assertEquals(3, actualSCourses.size())
        assertTrue(actualSCourses.first().name == "course1" )
        assertTrue(actualSCourses.get(1).name == "course2" )
        assertTrue(actualSCourses.last().name == "course3" )

        location.distance = 770
        actualSCourses = solrClient.query("courses",
                SolrQueryBuilder.valueOf(new SearchParams(s: "course*", suburbs: Arrays.asList(location)), cCollege.college.id.toString(), null, null).build())
                .getBeans(SCourse.class)
        assertEquals(4, actualSCourses.size())
        assertTrue(actualSCourses.first().name == "course1" )
        assertTrue(actualSCourses.get(1).name == "course2" )
        assertTrue(actualSCourses.get(2).name == "course3" )
        assertTrue(actualSCourses.last().name == "course4" )
    }

    @After
    void after(){
        Schedulers.shutdown()

        // Can't drop DB cause 2 mariaDB threads is still working.
        // TODO: define mariaDB daemon threads and shut them down
        testContext.close(false)
    }
}
