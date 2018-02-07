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
class SolrCourseQueryWithLocationFilterTest extends ASolrTest{
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
    void testSortCoursesWithLocationFilter(){
        SolrClient solrClient = new EmbeddedSolrServer(h.getCore())

        cCollege.newCourse("course1").withClassWithSiteLocation("past",15,15, -5, -4).build()
        cCollege.newCourse("course2").withClassWithSiteLocation("current",15,15, -1, 1).build()
        cCollege.newCourse("course3").withClassWithSiteLocation("future",15,15, 5, 6).build()

        cCollege.newCourse("course4").withClassWithSiteLocation("past",20,15, -5, -4).build()
        cCollege.newCourse("course5").withClassWithSiteLocation("current",20,15, -1, 1).build()
        cCollege.newCourse("course6").withClassWithSiteLocation("future",20,15, 5, 6).build()

        cCollege.newCourse("course7").withClassWithSiteLocation("past",15,20, -5, -4).build()
        cCollege.newCourse("course8").withClassWithSiteLocation("current",15,20, -1, 1).build()
        cCollege.newCourse("course9").withClassWithSiteLocation("future",15,20, 5, 6).build()

        cCollege.newCourse("course10").withClassWithSiteLocation("past",20,20, -5, -4).build()
        cCollege.newCourse("course11").withClassWithSiteLocation("current",20,20, -1, 1).build()
        cCollege.newCourse("course12").withClassWithSiteLocation("future",20,20, 5, 6).build()
        
        cCollege.newCourse("course13").withSelfPacedClass("withoutLocation").build()

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
        assertEquals(4, actualSCourses.size())
        //sort order is: current first, than future. No past and selfpaced classes. location order isn't in use
        assertTrue(actualSCourses.subList(0, 2).name.contains("course2"))
        assertTrue(actualSCourses.subList(0, 2).name.contains("course5"))
        assertTrue(actualSCourses.subList(2, 4).name.contains("course3"))
        assertTrue(actualSCourses.subList(2, 4).name.contains("course6"))
        
        location.distance = 560
        actualSCourses = solrClient.query("courses",
                SolrQueryBuilder.valueOf(new SearchParams(s: "course*", suburbs: Arrays.asList(location)), cCollege.college.id.toString(), null, null).build())
                .getBeans(SCourse.class)
        assertEquals(6, actualSCourses.size())
        assertTrue(actualSCourses.subList(0, 3).name.contains("course2"))
        assertTrue(actualSCourses.subList(0, 3).name.contains("course5"))
        assertTrue(actualSCourses.subList(0, 3).name.contains("course8"))
        assertTrue(actualSCourses.subList(3, 6).name.contains("course3"))
        assertTrue(actualSCourses.subList(3, 6).name.contains("course6"))
        assertTrue(actualSCourses.subList(3, 6).name.contains("course9"))
    }

    @After
    void after(){
        Schedulers.shutdown()

        // Can't drop DB cause 2 mariaDB threads is still working.
        // TODO: define mariaDB daemon threads and shut them down
        testContext.close(false)
    }
}
