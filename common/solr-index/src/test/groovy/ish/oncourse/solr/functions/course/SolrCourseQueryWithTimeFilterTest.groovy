package ish.oncourse.solr.functions.course

import ish.oncourse.solr.ASolrTest
import ish.oncourse.solr.model.SCourse
import ish.oncourse.solr.query.SearchParams
import ish.oncourse.solr.query.SolrQueryBuilder
import ish.oncourse.solr.reindex.ReindexCoursesJob
import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.junit.Test

/**
 * Our system works in next way:
 * All dateTime data is stored in DB in "Australia/Sydney" timezone
 * Than if class'es first session has a room with different timezone,
 * SessionFunctions.getDayTime converts session's start date using site's timezone and gets hours
 * from 6 to 17 is daytime. All other hours is evening time
 * For example: to set Australia/Perth 8AM to a session, we need to set '10AM'(Sydney time) and 'Australia/Perth' timezone to a session. And we will get 8AM Perth
 */
class SolrCourseQueryWithTimeFilterTest extends ASolrTest {
    private static String TIMEZONE_SYDNEY = "Australia/Sydney" // +10 UTC - default timezone for all solr tests
    private static String TIMEZONE_PERTH = "Australia/Perth" // +8 UTC

    @Test
    void testSortCoursesWithTimeFilter() {
        SolrClient solrClient = new EmbeddedSolrServer(h.getCore())

        //from 6 to 17 is daytime. All other hours is evening time
        cCollege.newCourse("course1").newCourseClassWithTimezonedSessions("morningCurrentTimezone", TIMEZONE_SYDNEY, createDate(7, 10)).build()
        cCollege.newCourse("course2").newCourseClassWithTimezonedSessions("morningDifferentTimezone",TIMEZONE_PERTH, createDate(18, 10)).build() //date is 'evening', but will become daytime, cause 6pm will convert to 4pm UTC+8

        cCollege.newCourse("course3").newCourseClassWithTimezonedSessions("eveningCurrentTimezone",TIMEZONE_SYDNEY, createDate(18, 1)).build()
        cCollege.newCourse("course4").newCourseClassWithTimezonedSessions("eveningDifferentTimezone",TIMEZONE_PERTH, createDate(7, 1)).build() //date is 'morning', but will become evening, cause 7am will convert to 5am UTC+8
        
        cCollege.newCourse("course5").newCourseClassWithSessions("bothTimesPast", createDate(7, -5), createDate(18, -4)).build()
        cCollege.newCourse("course6").newCourseClassWithSessions("bothTimesCurrent", createDate(7, -1), createDate(18, 1)).build()
        cCollege.newCourse("course7").newCourseClassWithSessions("bothTimesfuture", createDate(7, 5), createDate(18, 6)).build()
        
        cCollege.timeZone(TIMEZONE_SYDNEY).newCourse("course8").withSelfPacedClass("selfpacedCurrentTimezone").build()
        cCollege.timeZone(TIMEZONE_PERTH).newCourse("course9").withSelfPacedClass("selfpacedDifferentTimezone").build()

        ReindexCoursesJob job = new ReindexCoursesJob(objectContext, solrClient)
        job.run()
        while (job.isActive()) {
            Thread.sleep(100)
        }

        List<SCourse> actualSCourses = solrClient.query("courses",
                SolrQueryBuilder.valueOf(new SearchParams(s: "course*", time: "daytime"), cCollege.college.id.toString(), null, null).build())
                .getBeans(SCourse.class)
        assertEquals(4, actualSCourses.size())
        assertTrue(actualSCourses.first().name == "course1")
        assertTrue(actualSCourses.get(1).name == "course2")
        assertTrue(actualSCourses.get(2).name == "course6")
        assertTrue(actualSCourses.last().name == "course7")
        
        actualSCourses = solrClient.query("courses",
                SolrQueryBuilder.valueOf(new SearchParams(s: "course*", time: "evening"), cCollege.college.id.toString(), null, null).build())
                .getBeans(SCourse.class)
        assertEquals(4, actualSCourses.size())
        assertTrue(actualSCourses.first().name == "course4")
        assertTrue(actualSCourses.get(1).name == "course3")
        assertTrue(actualSCourses.get(2).name == "course6")
        assertTrue(actualSCourses.last().name == "course7")
    }
    
    private static Date createDate(int hoursOfDay, int daysfromNow){
        Calendar date = Calendar.getInstance()
        date.set(Calendar.HOUR_OF_DAY,hoursOfDay)
        date.set(Calendar.MINUTE,01)
        date.add(Calendar.DAY_OF_MONTH, daysfromNow)
        date.time
    }
}