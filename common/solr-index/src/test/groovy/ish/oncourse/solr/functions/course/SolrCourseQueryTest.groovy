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
class SolrCourseQueryTest extends ASolrTest {

    @Test
    void testGetCourseClassesWithoutSessions() throws IOException, SolrServerException {
        cCollege.newCourse("course1").withCancelledClass("cancelled").build()
        cCollege.newCourse("course2").withDistantClass("distantLearning").build()
        cCollege.newCourse("course3").withInactiveClass("inactive").build()
        cCollege.newCourse("course4").withEnrolDisabledClass("enrolDisabled").build()
        cCollege.newCourse("course5").withWebInvisibleClass("webInvisible").build()
        cCollege.newCourse("course6").isWebVisible(false).withClass("courseWebInvisible").build()
        
        ReindexCourses job = new ReindexCourses(objectContext, solrClient)
        job.run()

        List<SCourse> actualSClasses = solrClient.query("courses",
                SolrQueryBuilder.valueOf(new SearchParams(s: "course"), cCollege.college.id.toString(), null, null).build())
                .getBeans(SCourse.class)
        assertEquals(5, actualSClasses.size())
        assertNull(actualSClasses.find {sCourse -> sCourse.name == "course6"})
        solrClient.close()
    }
}
