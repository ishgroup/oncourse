package ish.oncourse.solr.functions.course

import ish.oncourse.model.College
import ish.oncourse.model.Course
import ish.oncourse.solr.model.SContact
import ish.oncourse.solr.model.SCourse
import ish.oncourse.solr.model.SCourseClass
import org.junit.Test

import static ish.oncourse.solr.functions.course.CourseTestFunctions.emptyCourseContext
import static org.junit.Assert.assertEquals
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

/**
 * User: akoiro
 * Date: 4/11/17
 */
class SCourseFunctionsTest {

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

        CourseContext context = emptyCourseContext()
        context.course = course

        SCourse solrCourse = SCourseFunctions.GetSCourse(context)
        assertEquals(String.valueOf(course.id), solrCourse.id)
        assertEquals(course.name, solrCourse.name)
        assertEquals(course.detail, solrCourse.detail)
        assertEquals(course.code, solrCourse.code)
        assertEquals(course.college.id, solrCourse.collegeId)
    }

    @Test
    void test_addContacts() {
        use(ContactFunctions) {
            List<SContact> contacts = [
                    ContactFunctionsTest.contact(),
                    ContactFunctionsTest.contact(),
                    ContactFunctionsTest.contact(),
                    ContactFunctionsTest.contact()
            ].collect { c -> c.getSContact() }
            SCourse sCourse = new SCourse()
            
            SCourseClass scc = new SCourseClass()
            
            scc.tutorId = contacts.tutorId.unique().findAll { it != null }
            scc.tutor = contacts.name.unique().findAll { it != null }
            
            use(SCourseFunctions) {
                sCourse = sCourse.addContacts(scc)
                assertEquals(contacts.tutorId, sCourse.tutorId)
                assertEquals(contacts.name, sCourse.tutor)
            }
        }
    }
}
