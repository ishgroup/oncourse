package ish.oncourse.solr.functions.course

import io.reactivex.schedulers.Schedulers
import ish.oncourse.model.Site
import ish.oncourse.solr.ASolrTest
import ish.oncourse.solr.InitSolr
import ish.oncourse.solr.model.SCourse
import ish.oncourse.solr.query.SearchParams
import ish.oncourse.solr.query.SolrQueryBuilder
import ish.oncourse.solr.reindex.ReindexCoursesJob
import ish.oncourse.test.TestContext
import ish.oncourse.test.context.CCollege
import ish.oncourse.test.context.CSite
import ish.oncourse.test.context.DataContext
import org.apache.cayenne.ObjectContext
import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Created by alex on 1/26/18.
 */
class SolrCourseClassQueryWithSiteFilterTest extends ASolrTest {
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
    void testSortCoursesWithSiteFilter() {
        SolrClient solrClient = new EmbeddedSolrServer(h.getCore())

        String collegeId = cCollege.college.id.toString()
        List<SCourse> actualSCourses
        
        
        Site targetSite = CSite.instance(objectContext, cCollege.college).build().site
        Site otherSite = CSite.instance(objectContext, cCollege.college).build().site
        Site virtualSite = CSite.instance(objectContext, cCollege.college).isVirtual(true).build().site

        cCollege.newCourse("course1").newCourseClassWithSessionsAndSite("pastTargetSite", targetSite,  -5, -4).build()
        cCollege.newCourse("course2").newCourseClassWithSessionsAndSite("pastOtherSite", otherSite, -5, -4).build()
        cCollege.newCourse("course3").newCourseClass("pastBothSitesTargetFirst").withSessionAndSite(-5, targetSite).withSessionAndSite(-4, otherSite).build()
        cCollege.newCourse("course4").newCourseClass("pastBothSitesOtherFirst").withSessionAndSite(-5, otherSite).withSessionAndSite(-4, targetSite).build()
        
        cCollege.newCourse("course5").newCourseClassWithSessionsAndSite("currentTargetSite", targetSite, -1, 1).build()
        cCollege.newCourse("course6").newCourseClassWithSessionsAndSite("currentOtherSite", otherSite, -1, 1).build()
        cCollege.newCourse("course7").newCourseClass("currentBothSitesTargetFirst").withSessionAndSite(-1, targetSite).withSessionAndSite(1, otherSite).build()
        cCollege.newCourse("course8").newCourseClass("currentBothSitesOtherFirst").withSessionAndSite(-1, otherSite).withSessionAndSite(1, targetSite).build()
        
        cCollege.newCourse("course9").newCourseClassWithSessionsAndSite("futureTargetSite", targetSite, 5, 6).build()
        cCollege.newCourse("course10").newCourseClassWithSessionsAndSite("futureOtherSite", otherSite, 5, 6).build()
        cCollege.newCourse("course11").newCourseClass("futureBothSitesTargetFirst").withSessionAndSite(5, targetSite).withSessionAndSite(6, otherSite).build()
        cCollege.newCourse("course12").newCourseClass("futureBothSitesOtherFirst").withSessionAndSite(5, otherSite).withSessionAndSite(6, targetSite).build()

        cCollege.newCourse("course13").withSelfPacedClassWithSite("withVirtualSite", virtualSite).build()
        cCollege.newCourse("course14").withSelfPacedClass("withoutSiteSite").build()

        ReindexCoursesJob job = new ReindexCoursesJob(objectContext, solrClient)
        job.run()
        while (job.isActive()) {
            Thread.sleep(100)
        }
        
        actualSCourses = solrClient.query("courses",
                SolrQueryBuilder.valueOf(new SearchParams(s: "course*", siteId: targetSite.id), collegeId, null, null).build())
                .getBeans(SCourse.class)
        assertEquals(6, actualSCourses.size())
        assertTrue(actualSCourses.first().name == "course5")
        assertTrue(actualSCourses.get(1).name == "course9")
        assertTrue(actualSCourses.subList(2, 4).name.contains("course7"))
        assertTrue(actualSCourses.subList(2, 4).name.contains("course8"))
        assertTrue(actualSCourses.subList(4, 6).name.contains("course11"))
        assertTrue(actualSCourses.subList(4, 6).name.contains("course12"))

        actualSCourses = solrClient.query("courses",
                SolrQueryBuilder.valueOf(new SearchParams(s: "course*", siteId: otherSite.id), collegeId, null, null).build())
                .getBeans(SCourse.class)
        assertEquals(6, actualSCourses.size())
        assertTrue(actualSCourses.first().name == "course6")
        assertTrue(actualSCourses.get(1).name == "course10")
        assertTrue(actualSCourses.subList(2, 4).name.contains("course7"))
        assertTrue(actualSCourses.subList(2, 4).name.contains("course8"))
        assertTrue(actualSCourses.subList(4, 6).name.contains("course11"))
        assertTrue(actualSCourses.subList(4, 6).name.contains("course12"))
        
        actualSCourses = solrClient.query("courses",
                SolrQueryBuilder.valueOf(new SearchParams(s: "course*", siteId: virtualSite.id), collegeId, null, null).build())
                .getBeans(SCourse.class)
        assertEquals(1, actualSCourses.size())
        assertTrue(actualSCourses.first().name == "course13")
    }

    @After
    void after() {
        Schedulers.shutdown()

        // Can't drop DB cause 2 mariaDB threads is still working.
        // TODO: define mariaDB daemon threads and shut them down
        testContext.close(false)
    }
}
