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
 * Our system works in next way:
 * All dateTime data is stored in DB in "Australia/Sydney" timezone
 * Than if class'es first session has a room with different timezone,
 * SessionFunctions.getDayTime converts session's start date using site's timezone and gets hours
 * from 6 to 17 is daytime. All other hours is evening time
 * For example: to set Australia/Perth 8AM to a session, we need to set '10AM'(Sydney time) and 'Australia/Perth' timezone to a session. And we will get 8AM Perth
 */
class SolrCourseClassQueryWithDayTime extends ASolrTest {
    private static String TIMEZONE_SYDNEY = "Australia/Sydney" // +10 UTC - default timezone for all solr tests
    private static String TIMEZONE_PERTH = "Australia/Perth" // +8 UTC
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
    void testSortCoursesWithDayTime() {
        SolrClient solrClient = new EmbeddedSolrServer(h.getCore())

        //from 6 to 17 is daytime. All other hours is evening time
        Date morning = createDate(7)
        Date evening = createDate(18)
        
        cCollege.newCourse("course1").newCourseClassWithTimezonedSessions("morningCurrentTimezone", TIMEZONE_SYDNEY, morning).build()
        cCollege.newCourse("course4").newCourseClassWithTimezonedSessions("morningDifferentTimezone",TIMEZONE_PERTH, evening).build() //date is 'evening', but will become daytime, cause 6pm will convert to 4pm UTC+8

        cCollege.newCourse("course2").newCourseClassWithTimezonedSessions("eveningCurrentTimezone",TIMEZONE_SYDNEY, evening).build()
        cCollege.newCourse("course3").newCourseClassWithTimezonedSessions("eveningDifferentTimezone",TIMEZONE_PERTH, morning).build() //date is 'morning', but will become evening, cause 7am will convert to 5am UTC+8
        
        
        cCollege.timeZone(TIMEZONE_SYDNEY).newCourse("course5").withSelfPacedClass("selfpacedCurrentTimezone").build()
        cCollege.timeZone(TIMEZONE_PERTH).newCourse("course6").withSelfPacedClass("selfpacedDifferentTimezone").build()

        ReindexCoursesJob job = new ReindexCoursesJob(objectContext, solrClient)
        job.run()
        while (job.isActive()) {
            Thread.sleep(100)
        }

        List<SCourse> actualSCourses = solrClient.query("courses",
                SolrQueryBuilder.valueOf(new SearchParams(s: "course*", time: "daytime"), cCollege.college.id.toString(), null, null).build())
                .getBeans(SCourse.class)
        assertEquals(2, actualSCourses.size())
        assertNotNull(actualSCourses.find {c -> c.name == "course1" })
        assertNotNull(actualSCourses.find {c -> c.name == "course4" })
        
        actualSCourses = solrClient.query("courses",
                SolrQueryBuilder.valueOf(new SearchParams(s: "course*", time: "evening"), cCollege.college.id.toString(), null, null).build())
                .getBeans(SCourse.class)
        assertEquals(2, actualSCourses.size())
        assertNotNull(actualSCourses.find {c -> c.name == "course2" })
        assertNotNull(actualSCourses.find {c -> c.name == "course3" })
    }
    
    private static Date createDate(int hoursOfDay){
        Calendar date = Calendar.getInstance()
        date.set(Calendar.HOUR_OF_DAY,hoursOfDay)
        date.set(Calendar.MINUTE,01)
        date.add(Calendar.DAY_OF_MONTH, 1)
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