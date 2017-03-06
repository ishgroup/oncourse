package ish.oncourse.solr.functions

import ish.oncourse.model.College
import ish.oncourse.model.Course
import ish.oncourse.solr.model.SolrCourse
import org.junit.Assert
import org.junit.Test

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

/**
 * Test for ConvertCourse2SolrCourse function
 */
class ConvertCourse2SolrCourseTest {

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


        ConvertCourse2SolrCourse course2SolrCourse = new ConvertCourse2SolrCourse()
        SolrCourse solrCourse = course2SolrCourse.convert(course)
        Assert.assertEquals(String.valueOf(course.id), solrCourse.id)
        Assert.assertEquals(course.name, solrCourse.name)
        Assert.assertEquals(course.detail, solrCourse.detail)
        Assert.assertEquals(course.code, solrCourse.code)
        Assert.assertEquals(course.college.id, solrCourse.collegeId)
    }
}
