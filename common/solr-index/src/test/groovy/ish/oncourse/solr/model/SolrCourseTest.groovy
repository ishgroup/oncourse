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
        SCourse course = new SCourse().with {
            collegeId = 1
            id = 1
            name = 'Course'
            detail = 'Course detail'
            code = 'C1'
            startDate = current
            it
        }

        String json = mapper.writeValueAsString(course)

        SCourse rCourse = mapper.readValue(json, SCourse)
        Assert.assertEquals(course, rCourse)
    }
}
