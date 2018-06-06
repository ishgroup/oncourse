package ish.oncourse.solr

import ish.oncourse.solr.model.SCourse
import ish.oncourse.solr.query.SearchParams
import ish.oncourse.solr.query.SolrQueryBuilder
import ish.oncourse.solr.reindex.ReindexCourses
import ish.oncourse.test.context.CCourse
import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.SolrServerException
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.junit.Test

/**
 * Created by alex on 11/22/17.
 */
class ReindexCourseTest extends ASolrTest {

    @Test
    void testReindexCourse() throws IOException, SolrServerException {
        SolrClient solrClient = new EmbeddedSolrServer(h.getCore())

        CCourse course1 = cCollege.newCourse("course1").build()
        CCourse course2 = cCollege.newCourse("course2").build()

        SCourse expectedSCourse = new SCourse()
        expectedSCourse.setId(course1.course.id.toString())
        expectedSCourse.setCollegeId(course1.course.college.id)
        expectedSCourse.setName(course1.course.name)
        expectedSCourse.setDetail(course1.course.detail)
        expectedSCourse.setCode(course1.course.code)

        ReindexCourses job = new ReindexCourses(objectContext, solrClient)
        job.run()

        List<SCourse> actualSClasses = solrClient.query("courses",
                SolrQueryBuilder.valueOf(new SearchParams(s: "course"), cCollege.college.id.toString(), null, null).build())
                .getBeans(SCourse.class)
        assertEquals(2, actualSClasses.size())
        assertNotNull(actualSClasses.find { c -> c.id == expectedSCourse.id })
        assertNotNull(actualSClasses.find { c -> c.name == expectedSCourse.name })

        solrClient.close()
    }
}
