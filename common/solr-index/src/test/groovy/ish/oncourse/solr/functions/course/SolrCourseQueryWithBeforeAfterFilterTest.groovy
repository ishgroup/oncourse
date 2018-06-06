package ish.oncourse.solr.functions.course

import ish.oncourse.solr.ASolrTest
import ish.oncourse.solr.model.SCourse
import ish.oncourse.solr.query.SearchParams
import ish.oncourse.solr.query.SolrQueryBuilder
import ish.oncourse.solr.reindex.ReindexCourses
import org.junit.Test

/**
 * Created by alex on 1/24/18.
 */
class SolrCourseQueryWithBeforeAfterFilterTest extends ASolrTest {

    @Test
    void testSortCoursesWithBeforeAfterFilter() {
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

        ReindexCourses job = new ReindexCourses(objectContext, solrClient)
        job.run()

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
}