package ish.oncourse.solr.functions.course

import ish.oncourse.solr.ASolrTest
import ish.oncourse.solr.model.SCourse
import ish.oncourse.solr.query.SearchParams
import ish.oncourse.solr.query.SolrQueryBuilder
import ish.oncourse.solr.reindex.ReindexCourses
import org.apache.solr.client.solrj.SolrServerException
import org.junit.Test

/**
 * Created by alex on 12/27/17.
 */
class SolrCourseQueryWithSessionTest extends ASolrTest{

    @Test
    void testSortClassesWithSessionsAndDistantLearning() throws IOException, SolrServerException {
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

        List<SCourse> actualSCourses = solrClient.query("courses",
                SolrQueryBuilder.valueOf(new SearchParams(s: "course*"), cCollege.college.id.toString(), null, null).build())
                .getBeans(SCourse.class)
        assertEquals(10, actualSCourses.size())

        List<SCourse> currentCourses = actualSCourses.subList(0, 3)
        List<SCourse> futureCourses = actualSCourses.subList(4, 7)
        List<SCourse> pastCourses = actualSCourses.subList(7, 10)
        
        //first group of courses will be with current classes. First one will be the class, which starts first. 
        assertEquals("course5", currentCourses.first().name)
        assertNotNull(currentCourses.find {c -> c.name == "course4" })
        assertNotNull(currentCourses.find {c -> c.name == "course6" })

        //distant learning classes are going between current and future classes
        assertEquals("course10", actualSCourses.get(3).name)
        
        //second group of courses will be with future classes. First one will be the class, which starts first. 
        assertEquals("course8", futureCourses.first().name)
        assertNotNull(futureCourses.find {c -> c.name == "course7" })
        assertNotNull(futureCourses.find {c -> c.name == "course9" })

        //last group of courses will be with past classes. sort order isn't set for past classes.
        assertNotNull(pastCourses.find {c -> c.name == "course1" })
        assertNotNull(pastCourses.find {c -> c.name == "course2" })
        assertNotNull(pastCourses.find {c -> c.name == "course3" })
        
        solrClient.close()
    }
}
