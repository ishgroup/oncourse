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
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Created by alex on 1/24/18.
 */
class SolrCourseClassQueryWithBeforeAfterFilterTest extends ASolrTest {
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
        
        Calendar checkDate = Calendar.instance
        checkDate.set(Calendar.HOUR_OF_DAY, 0)
        checkDate.set(Calendar.MINUTE, 0)
        checkDate.set(Calendar.SECOND, 0)

        cCollege.newCourse("course1").newCourseClassWithSessions("past", -5, -4).build()
        cCollege.newCourse("course2").newCourseClassWithSessions("pastStartsFirst", -6, -4).build()
        cCollege.newCourse("course3").newCourseClassWithSessions("pastEndsLast", -5, -3).build()

        cCollege.newCourse("course4").newCourseClassWithSessions("current", -1, 1).build()
        cCollege.newCourse("course5").newCourseClassWithSessions("currentStartsFirst", -2, 1).build()
        cCollege.newCourse("course6").newCourseClassWithSessions("currentEndsLast", -1, 2).build()

        cCollege.newCourse("course7").newCourseClassWithSessions("future", 5, 6).build()
        cCollege.newCourse("course8").newCourseClassWithSessions("futureStartsFirst", 4, 6).build()
        cCollege.newCourse("course9").newCourseClassWithSessions("futureEndsLast", 5, 7).build()

        cCollege.newCourse("course10").withSelfPacedClass("distantLearning").build()

        ReindexCoursesJob job = new ReindexCoursesJob(objectContext, solrClient)
        job.run()
        while (job.isActive()) {
            Thread.sleep(100)
        }

        //if we use 'before' filter - past classes doesn't selected by solr query because they have startDate = now + 100 years
        actualSCourses = solrClient.query("courses",
                SolrQueryBuilder.valueOf(new SearchParams(s: "course*", before: checkDate.time + 10, after: new Date() - 10), collegeId, null, null).build())
                .getBeans(SCourse.class)
        assertEquals(7, actualSCourses.size())
        assertEquals("course5", actualSCourses.first().name)
        assertNotNull(actualSCourses.find {c -> c.name == "course4" })
        assertNotNull(actualSCourses.find {c -> c.name == "course6" })
        assertEquals("course10", actualSCourses.get(3).name)
        assertEquals("course8", actualSCourses.get(4).name)
        assertNotNull(actualSCourses.find {c -> c.name == "course7" })
        assertNotNull(actualSCourses.find {c -> c.name == "course9" })

        //'before' date filter EXCLUDE classes, which started on filtered date. 
        actualSCourses = solrClient.query("courses",
                SolrQueryBuilder.valueOf(new SearchParams(s: "course*", before: checkDate.time + 5), collegeId, null, null).build())
                .getBeans(SCourse.class)
        assertEquals(5, actualSCourses.size())
        assertEquals("course5", actualSCourses.first().name)
        assertNotNull(actualSCourses.find {c -> c.name == "course4" })
        assertNotNull(actualSCourses.find {c -> c.name == "course6" })
        assertEquals("course10", actualSCourses.get(3).name)
        assertEquals("course8", actualSCourses.last().name)

        //'after' filter with value = 2+ days won't select selfpaced (distant learning) classes, cause that classes are getting 'classStart' date = tomorrow
        actualSCourses = solrClient.query("courses",
                SolrQueryBuilder.valueOf(new SearchParams(s: "course*", after: checkDate.time + 2), collegeId, null, null).build())
                .getBeans(SCourse.class)
        assertEquals(6, actualSCourses.size())
        assertEquals("course8", actualSCourses.first().name)
        assertNotNull(actualSCourses.find {c -> c.name == "course7" })
        assertNotNull(actualSCourses.find {c -> c.name == "course9" })
        assertNotNull(actualSCourses.find {c -> c.name == "course1" })
        assertNotNull(actualSCourses.find {c -> c.name == "course2" })
        assertNotNull(actualSCourses.find {c -> c.name == "course3" })
        
        //when we try to select classes 'before' past date, we can get only current classes (if they have started before past date). No selfpased, no past classes.
        actualSCourses = solrClient.query("courses",
                SolrQueryBuilder.valueOf(new SearchParams(s: "course*", before: checkDate.time - 3), collegeId, null, null).build())
                .getBeans(SCourse.class)
        assertTrue(actualSCourses.isEmpty())
    }

    @After
    void after() {
        Schedulers.shutdown()

        // Can't drop DB cause 2 mariaDB threads is still working.
        // TODO: define mariaDB daemon threads and shut them down
        testContext.close(false)
    }
}