package ish.oncourse.server.duplicate

import ish.oncourse.server.cayenne.Course
import org.apache.cayenne.ObjectContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito

import static ish.oncourse.server.duplicate.DuplicateCourseService.DuplicateCourseCode
import static org.junit.Assert.assertEquals
import static org.mockito.Matchers.any
import static org.mockito.Mockito.when

class DuplicateCourseCodeTest {

    private ObjectContext context
    private Course oldCourse

    @BeforeEach
    void setUp() throws Exception {
        context = Mockito.mock(ObjectContext.class)
        oldCourse = Mockito.mock(Course.class)
        when(oldCourse.getContext()).thenReturn(context)
    }

    /**
     * Test situation when: oldCourseCode = "Code" . In database there are courses with such codes : Code1, Code2, Code3.
     * We need to add course with "Code4" code.
     * @throws Exception
     */
    @Test
    void testCourseCodeDuplication1() throws Exception {
        String oldCourseCode = "Code"
        List courses = generateCourse(oldCourseCode, 3)
        when(context.select(any())).thenReturn(courses)

        when(oldCourse.getCode()).thenReturn(oldCourseCode)
        DuplicateCourseCode duplicateCourseCode = DuplicateCourseCode.valueOf(oldCourse)
        String newCode = duplicateCourseCode.duplicateCode()

        assertEquals("Code4", newCode)
    }

    /**
     * Test situation when: oldCourseCode = "Code" . In database there are not present courses with code starts with "Code"
     * We need to add course with "Code1" code.
     * @throws Exception
     */
    @Test
    void testCourseCodeDuplication2() throws Exception {
        String oldCourseCode = "Code"
        when(context.select(any())).thenReturn(new ArrayList<>())

        when(oldCourse.getCode()).thenReturn(oldCourseCode)
        DuplicateCourseCode duplicateCourseCode = DuplicateCourseCode.valueOf(oldCourse)
        String newCode = duplicateCourseCode.duplicateCode()

        assertEquals("Code1", newCode)
    }


    /**
     * Test situation when: oldCourseCode = "Code123" .In database there are courses with such code : Code1, Code2, Code3.
     * We need to add course with "Code124" code.
     * @throws Exception
     */
    @Test
    void testCourseCodeDuplication3() throws Exception {
        String oldCourseCode = "Code123"
        List courses = generateCourse("Code", 3)
        when(context.select(any())).thenReturn(courses)
        when(oldCourse.getCode()).thenReturn(oldCourseCode)

        DuplicateCourseCode duplicateCourseCode = DuplicateCourseCode.valueOf(oldCourse)
        String newCode = duplicateCourseCode.duplicateCode()

        assertEquals("Code124", newCode)
    }

    /**
     * Test situation when: oldCourseCode = "Code123" . In database there are courses with such code : Code1, Code2, Code129.
     * We need to add course with "Code130" code.
     * @throws Exception
     */
    @Test
    void testCourseCodeDuplication4() throws Exception {
        String oldCourseCode = "Code123"
        List courses = generateCourse("Code", 2)
        Course courseWithMaxEndNumber = Mockito.mock(Course.class)
        when(courseWithMaxEndNumber.getCode()).thenReturn("Code129")
        courses.add(courseWithMaxEndNumber)
        when(context.select(any())).thenReturn(courses)
        when(oldCourse.getCode()).thenReturn(oldCourseCode)

        DuplicateCourseCode duplicateCourseCode = DuplicateCourseCode.valueOf(oldCourse)
        String newCode = duplicateCourseCode.duplicateCode()

        assertEquals("Code130", newCode)
    }

    @Test
    void testLongWithoutDigitsCourseCode() throws Exception {

        when(oldCourse.getCode()).thenReturn("AaaaaBbbbbCccccEeeeeDeeeeFffffG")
        DuplicateCourseCode duplicateCourseCode = DuplicateCourseCode.valueOf(oldCourse)
        String newCode = duplicateCourseCode.duplicateCode()
        assertEquals(newCode, "AaaaaBbbbbCccccEeeeeDeeeeFffffG1")

        when(oldCourse.getCode()).thenReturn("AaaaaBbbbbCccccEeeeeDeeeeFffffGg")
        duplicateCourseCode = DuplicateCourseCode.valueOf(oldCourse)
        newCode = duplicateCourseCode.duplicateCode()
        assertEquals(newCode, "AaaaaBbbbbCccccEeeeeDeeeeFffffG1")
    }

    @Test
    void testLongCourseCode() throws Exception {
        when(oldCourse.getCode()).thenReturn("AaaaaBbbbbCccccEeeeeDeeeeFffffG9")
        DuplicateCourseCode duplicateCourseCode = DuplicateCourseCode.valueOf(oldCourse)
        String newCode = duplicateCourseCode.duplicateCode()

        assertEquals("AaaaaBbbbbCccccEeeeeDeeeeFffff10", newCode)
    }

    private List<Course> generateCourse(String code, int toIndex) {
        List<Course> courseList = new ArrayList<>()
        for (int i = 1 ; i <= toIndex ; i++) {
            Course course = Mockito.mock(Course.class)
            when(course.getCode()).thenReturn(code + i)
            courseList.add(course)
        }
        return  courseList
    }
}