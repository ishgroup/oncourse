package ish.oncourse.solr.functions.course

import ish.oncourse.model.College
import ish.oncourse.model.Course
import ish.oncourse.solr.model.SCourse
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

/**
 * Test for ConvertCourse2SolrCourse function
 */
class Course2SolrCourseTest {

    @Test
    void test_filling_SolrCourse() {
        College college = mock(College)
        when(college.getId()).thenReturn(1L)

        Course course = mock(Course)
        when(course.id).thenReturn(11L)
        when(course.getCollege()).thenReturn(college)
        when(course.name).thenReturn('Course 1')
        when(course.detail).thenReturn('Course 1 details')
        when(course.code).thenReturn('COURSE1')


        SCourse solrCourse = CourseFunctions.getSolrCourse(course)
        assertEquals(String.valueOf(course.id), solrCourse.id)
        assertEquals(course.name, solrCourse.name)
        assertEquals(course.detail, solrCourse.detail)
        assertEquals(course.code, solrCourse.code)
        assertEquals(course.college.id, solrCourse.collegeId)
    }
}
