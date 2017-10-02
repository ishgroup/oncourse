package ish.oncourse.solr.model

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Assert
import org.junit.Test

/**
 * Created by akoira on 28/2/17.
 */
class SolrCourseTest {

    @Test
    void test() {

        Date current = new Date()
        ObjectMapper mapper = new ObjectMapper()
        SolrCourse course = new SolrCourse().with {
            collegeId = 1
            id = 1
            name = 'Course'
            detail = 'Course detail'
            code = 'C1'
            startDate = current
            it
        }

        String json = mapper.writeValueAsString(course)

        SolrCourse rCourse = mapper.readValue(json, SolrCourse)
        Assert.assertEquals(course, rCourse)
    }
}
