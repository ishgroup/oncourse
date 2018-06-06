package ish.oncourse.solr.functions.course

import ish.oncourse.solr.ASolrTest
import ish.oncourse.solr.model.SCourse
import ish.oncourse.solr.query.DayOption
import ish.oncourse.solr.query.SearchParams
import ish.oncourse.solr.query.SolrQueryBuilder
import ish.oncourse.solr.reindex.ReindexCourses
import org.junit.Test

/**
 * Created by alex on 1/17/18.
 */
class SolrCourseQueryWithDayFilterTest extends ASolrTest {

    @Test
    void testSortCoursesWithDayFilter() {
        String collegeId = cCollege.college.id
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
        cCollege.newCourse("course4").newCourseClassWithSessions("saturdayClass", sat).build()
        cCollege.newCourse("course5").newCourseClassWithSessions("mondayClass", mon).build()
        
        cCollege.newCourse("course6").newCourseClassWithTimezonedSessions("fridayDifferentTimeZoneClass", "Australia/Perth", sat).build() //saturday 1am after timeZone recalculate will be friday 11pm
        cCollege.newCourse("course7").newCourseClassWithTimezonedSessions("sundayDifferentTimeZoneClass", "Australia/Perth", mon).build() //monday 1am after timeZone recalculate it will be sunday 11pm

        cCollege.newCourse("course8").newCourseClassWithSessions("pastTuesdayClass", createDate(Calendar.TUESDAY, -14), createDate(Calendar.TUESDAY, -7)).build()
        cCollege.newCourse("course9").newCourseClassWithSessions("currentTuesdayClass", createDate(Calendar.TUESDAY, -7), createDate(Calendar.TUESDAY, 7)).build()
        cCollege.newCourse("course10").newCourseClassWithSessions("futureTuesdayClass", createDate(Calendar.TUESDAY, 7), createDate(Calendar.TUESDAY, 14)).build()
        cCollege.newCourse("course11").newCourseClassWithSessions("currentNotOnlyTuesdayClass", createDate(Calendar.TUESDAY, -7), createDate(Calendar.TUESDAY, 7), thu).build()

        ReindexCourses job = new ReindexCourses(objectContext, solrClient)
        job.run()

        List<SCourse> actualSCourses = solrClient.query("courses",
                SolrQueryBuilder.valueOf(new SearchParams(s: "course*", day: DayOption.weekend), collegeId, null, null).build())
                .getBeans(SCourse.class)
        assertEquals(4, actualSCourses.size())
        assertTrue(actualSCourses.first().name == "course2")
        assertTrue(actualSCourses.get(1).name == "course7")
        assertTrue(actualSCourses.get(2).name == "course4")
        assertTrue(actualSCourses.last().name == "course3")

        actualSCourses = solrClient.query("courses",
                SolrQueryBuilder.valueOf(new SearchParams(s: "course*", day: DayOption.weekday), collegeId, null, null).build())
                .getBeans(SCourse.class)
        assertEquals(7, actualSCourses.size())
        assertTrue(actualSCourses.first().name == "course1")
        assertTrue(actualSCourses.get(1).name == "course11")
        assertTrue(actualSCourses.get(2).name == "course3")
        assertTrue(actualSCourses.get(3).name == "course9")
        assertTrue(actualSCourses.get(4).name == "course5")
        assertTrue(actualSCourses.get(5).name == "course10")
        assertTrue(actualSCourses.last().name == "course6")

        actualSCourses = solrClient.query("courses",
                SolrQueryBuilder.valueOf(new SearchParams(s: "course*", day: DayOption.sat), collegeId, null, null).build())
                .getBeans(SCourse.class)
        assertEquals(3, actualSCourses.size())
        assertTrue(actualSCourses.first().name == "course4")
        assertTrue(actualSCourses.get(1).name == "course2")
        assertTrue(actualSCourses.last().name == "course3")

        actualSCourses = solrClient.query("courses",
                SolrQueryBuilder.valueOf(new SearchParams(s: "course*", day: DayOption.mon), collegeId, null, null).build())
                .getBeans(SCourse.class)
        assertEquals(3, actualSCourses.size())
        assertTrue(actualSCourses.first().name == "course5")
        assertTrue(actualSCourses.get(1).name == "course1")
        assertTrue(actualSCourses.last().name == "course3")

        actualSCourses = solrClient.query("courses",
                SolrQueryBuilder.valueOf(new SearchParams(s: "course*", day: DayOption.tues), collegeId, null, null).build())
                .getBeans(SCourse.class)
        assertEquals(5, actualSCourses.size())
        assertTrue(actualSCourses.first().name == "course9")
        assertTrue(actualSCourses.get(1).name == "course10")
        assertTrue(actualSCourses.get(2).name == "course11")
        assertTrue(actualSCourses.get(3).name == "course1")
        assertTrue(actualSCourses.last().name == "course3")
    }

    private static Date createDate(int dayOfWeek, int daysFromNow = 7){
        Calendar date = Calendar.getInstance()
        date.add(Calendar.DAY_OF_MONTH, daysFromNow)
        date.set(Calendar.DAY_OF_WEEK, dayOfWeek)
        date.set(Calendar.HOUR_OF_DAY, 1)
        date.time
    }
}