package ish.oncourse.solr.functions.course

import io.reactivex.schedulers.Schedulers
import ish.oncourse.model.Tutor
import ish.oncourse.solr.ASolrTest
import ish.oncourse.solr.InitSolr
import ish.oncourse.solr.model.SCourse
import ish.oncourse.solr.query.SearchParams
import ish.oncourse.solr.query.SolrQueryBuilder
import ish.oncourse.solr.reindex.ReindexCoursesJob
import ish.oncourse.test.TestContext
import ish.oncourse.test.context.CCollege
import ish.oncourse.test.context.CTutor
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
class SolrCourseClassQueryWithTutorFilterTest extends ASolrTest {
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
    void testSortCoursesWithBeforeAfterFilter() {
        SolrClient solrClient = new EmbeddedSolrServer(h.getCore())

        String collegeId = cCollege.college.id.toString()
        List<SCourse> actualSCourses
        
        Tutor targetTutor = CTutor.instance(objectContext, cCollege.college, "targetTutor").build().tutor
        Tutor otherTutor = CTutor.instance(objectContext, cCollege.college, "otherTutor").build().tutor

        cCollege.newCourse("course1").newCourseClassWithSessionsAndTutor("pastTargetTutor", targetTutor, -5, -4).build()
        cCollege.newCourse("course2").newCourseClassWithSessionsAndTutor("pastOtherTutor", otherTutor, -5, -4).build()
        cCollege.newCourse("course3").newCourseClass("pastBothTutorsTargetFirst").withSessionAndTutor(-5, targetTutor).withSessionAndTutor(-4, otherTutor).build()
        cCollege.newCourse("course4").newCourseClass("pastBothTutorsOtherFirst").withSessionAndTutor(-5, otherTutor).withSessionAndTutor(-4, targetTutor).build()

        cCollege.newCourse("course5").newCourseClassWithSessionsAndTutor("currentTargetSite", targetTutor, -1, 1).build()
        cCollege.newCourse("course6").newCourseClassWithSessionsAndTutor("currentOtherTutor", otherTutor, -1, 1).build()
        cCollege.newCourse("course7").newCourseClass("pastBothTutorsTargetFirst").withSessionAndTutor(-1, targetTutor).withSessionAndTutor(1, otherTutor).build()
        cCollege.newCourse("course8").newCourseClass("pastBothTutorsOtherFirst").withSessionAndTutor(-1, otherTutor).withSessionAndTutor(1, targetTutor).build()

        cCollege.newCourse("course9").newCourseClassWithSessionsAndTutor("futureTargetSite", targetTutor, 5, 6).build()
        cCollege.newCourse("course10").newCourseClassWithSessionsAndTutor("futureOtherTutor", otherTutor, 5, 6).build()
        cCollege.newCourse("course11").newCourseClass("pastBothTutorsTargetFirst").withSessionAndTutor(5, targetTutor).withSessionAndTutor(6, otherTutor).build()
        cCollege.newCourse("course12").newCourseClass("pastBothTutorsOtherFirst").withSessionAndTutor(5, otherTutor).withSessionAndTutor(6, targetTutor).build()

        cCollege.newCourse("course13").withSelfPacedClassAndTutor("withTargetTutor", targetTutor).build()
        cCollege.newCourse("course14").withSelfPacedClass("withoutTutor").build()

        ReindexCoursesJob job = new ReindexCoursesJob(objectContext, solrClient)
        job.run()
        while (job.isActive()) {
            Thread.sleep(100)
        }

        actualSCourses = solrClient.query("courses",
                SolrQueryBuilder.valueOf(new SearchParams(s: "course*", tutorId: targetTutor.contact.id), collegeId, null, null).build())
                .getBeans(SCourse.class)
        assertEquals(7, actualSCourses.size())
        assertTrue(actualSCourses.first().name == "course5")
        assertTrue(actualSCourses.get(1).name == "course13")
        assertTrue(actualSCourses.get(2).name == "course9")
        assertTrue(actualSCourses.subList(3, 5).name.contains("course7"))
        assertTrue(actualSCourses.subList(3, 5).name.contains("course8"))
        assertTrue(actualSCourses.subList(5, 7).name.contains("course11"))
        assertTrue(actualSCourses.subList(5, 7).name.contains("course12"))

        actualSCourses = solrClient.query("courses",
                SolrQueryBuilder.valueOf(new SearchParams(s: "course*", tutorId: otherTutor.id), collegeId, null, null).build())
                .getBeans(SCourse.class)
        assertEquals(6, actualSCourses.size())
        assertTrue(actualSCourses.first().name == "course6")
        assertTrue(actualSCourses.get(1).name == "course10")
        assertTrue(actualSCourses.subList(2, 4).name.contains("course7"))
        assertTrue(actualSCourses.subList(2, 4).name.contains("course8"))
        assertTrue(actualSCourses.subList(4, 6).name.contains("course11"))
        assertTrue(actualSCourses.subList(4, 6).name.contains("course12"))
    }

    @After
    void after() {
        Schedulers.shutdown()

        // Can't drop DB cause 2 mariaDB threads is still working.
        // TODO: define mariaDB daemon threads and shut them down
        testContext.close(false)
    }
}
