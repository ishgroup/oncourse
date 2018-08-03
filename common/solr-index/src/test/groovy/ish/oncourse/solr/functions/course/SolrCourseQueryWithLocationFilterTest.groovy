package ish.oncourse.solr.functions.course

import ish.oncourse.solr.ASolrTest
import ish.oncourse.solr.model.SCourse
import ish.oncourse.solr.query.SearchParams
import ish.oncourse.solr.query.SolrQueryBuilder
import ish.oncourse.solr.query.Suburb
import ish.oncourse.solr.reindex.ReindexCourses
import org.junit.Test

/**
 * Created by alex on 1/11/18.
 */
class SolrCourseQueryWithLocationFilterTest extends ASolrTest{

    @Test
    void testSortCoursesWithLocationFilter(){
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

        ReindexCourses job = new ReindexCourses(objectContext, solrClient)
        job.run()

        Suburb location = new Suburb()
        location.latitude = 15
        location.longitude = 15
        location.distance = 540

        List<SCourse> actualSCourses = solrClient.query("courses",
                SolrQueryBuilder.valueOf(new SearchParams(s: "course*", suburbs: Arrays.asList(location)), cCollege.college.id.toString(), null, null).build())
                .getBeans(SCourse.class)
        assertEquals(4, actualSCourses.size())
        assertEquals('course2', actualSCourses[0].name)
        assertEquals('course3', actualSCourses[1].name)
        assertEquals('course5', actualSCourses[2].name)
        assertEquals('course6', actualSCourses[3].name)
        
        location.distance = 560
        actualSCourses = solrClient.query("courses",
                SolrQueryBuilder.valueOf(new SearchParams(s: "course*", suburbs: Arrays.asList(location)), cCollege.college.id.toString(), null, null).build())
                .getBeans(SCourse.class)
        assertEquals(6, actualSCourses.size())

        assertEquals("course2", actualSCourses[0].name)
        assertEquals("course3", actualSCourses[1].name)
        assertEquals("course5", actualSCourses[2].name)
        assertEquals("course6", actualSCourses[3].name)
        assertEquals("course8", actualSCourses[4].name)
        assertEquals("course9", actualSCourses[5].name)
    }
}
