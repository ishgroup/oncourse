package ish.oncourse.solr.functions.course

import io.reactivex.schedulers.Schedulers
import ish.oncourse.solr.ASolrTest
import ish.oncourse.solr.InitSolr
import ish.oncourse.solr.model.SCourse
import ish.oncourse.solr.query.DayOption
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
 * Created by alex on 1/17/18.
 */
class SolrCourseClassQueryWithDayFilterTest extends ASolrTest {
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
    void testSortCoursesWithDayFilter() {
        SolrClient solrClient = new EmbeddedSolrServer(h.getCore())
        
        Date mon = createDate(Calendar.MONDAY)
        Date tue = createDate(Calendar.TUESDAY)
        Date wed = createDate(Calendar.WEDNESDAY)
        Date thu = createDate(Calendar.THURSDAY)
        Date fri = createDate(Calendar.FRIDAY)
        Date sat = createDate(Calendar.SATURDAY)
        Date sun = createDate(Calendar.SUNDAY)

        cCollege.newCourse("course1").newCourseClassWithSessions("workdayClass", mon, tue, wed, thu, fri).build()
        cCollege.newCourse("course2").newCourseClassWithSessions("weekendClass", sat, sun).build()
        cCollege.newCourse("course3").newCourseClassWithSessions("wholeWeekClass", mon, tue, wed, thu, fri, sat, sun).build()
        cCollege.newCourse("course4").newCourseClassWithSessions("saturday", sat).build()
        cCollege.newCourse("course5").newCourseClassWithSessions("mondayClass", mon).build()
        
        cCollege.newCourse("course6").newCourseClassWithTimezonedSessions("fridayDifferentTimeZoneClass", "Australia/Perth", sat).build() //saturday 1am after timeZone recalculate will be friday 11pm
        cCollege.newCourse("course7").newCourseClassWithTimezonedSessions("sundayDifferentTimeZoneClass", "Australia/Perth", mon).build() //monday 1am after timeZone recalculate it will be sunday 11pm

        ReindexCoursesJob job = new ReindexCoursesJob(objectContext, solrClient)
        job.run()
        while (job.isActive()) {
            Thread.sleep(100)
        }

        List<SCourse> actualSCourses = solrClient.query("courses",
                SolrQueryBuilder.valueOf(new SearchParams(s: "course*", day: DayOption.weekend), cCollege.college.id.toString(), null, null).build())
                .getBeans(SCourse.class)
        assertEquals(4, actualSCourses.size())
        assertNotNull(actualSCourses.find {c -> c.name == "course2" })
        assertNotNull(actualSCourses.find {c -> c.name == "course3" })
        assertNotNull(actualSCourses.find {c -> c.name == "course4" })
        assertNotNull(actualSCourses.find {c -> c.name == "course7" })

        actualSCourses = solrClient.query("courses",
                SolrQueryBuilder.valueOf(new SearchParams(s: "course*", day: DayOption.weekday), cCollege.college.id.toString(), null, null).build())
                .getBeans(SCourse.class)
        assertEquals(4, actualSCourses.size())
        assertNotNull(actualSCourses.find {c -> c.name == "course1" })
        assertNotNull(actualSCourses.find {c -> c.name == "course3" })
        assertNotNull(actualSCourses.find {c -> c.name == "course5" })
        assertNotNull(actualSCourses.find {c -> c.name == "course6" })

        actualSCourses = solrClient.query("courses",
                SolrQueryBuilder.valueOf(new SearchParams(s: "course*", day: DayOption.sat), cCollege.college.id.toString(), null, null).build())
                .getBeans(SCourse.class)
        assertEquals(3, actualSCourses.size())
        assertNotNull(actualSCourses.find {c -> c.name == "course2" })
        assertNotNull(actualSCourses.find {c -> c.name == "course3" })
        assertNotNull(actualSCourses.find {c -> c.name == "course4" })
        assertNull(actualSCourses.find {c -> c.name == "course6" })

        actualSCourses = solrClient.query("courses",
                SolrQueryBuilder.valueOf(new SearchParams(s: "course*", day: DayOption.mon), cCollege.college.id.toString(), null, null).build())
                .getBeans(SCourse.class)
        assertEquals(3, actualSCourses.size())
        assertNotNull(actualSCourses.find {c -> c.name == "course1" })
        assertNotNull(actualSCourses.find {c -> c.name == "course3" })
        assertNotNull(actualSCourses.find {c -> c.name == "course5" })
        assertNull(actualSCourses.find {c -> c.name == "course7" })
        
    }

    private static Date createDate(int dayOfWeek){
        Calendar date = Calendar.getInstance()
        date.add(Calendar.DAY_OF_MONTH, 7)
        date.set(Calendar.DAY_OF_WEEK, dayOfWeek)
        date.set(Calendar.HOUR_OF_DAY, 1)
        date.time
    }

    @After
    void after() {
        Schedulers.shutdown()

        // Can't drop DB cause 2 mariaDB threads is still working.
        // TODO: define mariaDB daemon threads and shut them down
        testContext.close(false)
    }
}