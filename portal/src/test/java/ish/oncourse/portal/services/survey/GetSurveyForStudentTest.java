package ish.oncourse.portal.services.survey;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Student;
import ish.oncourse.model.Survey;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class GetSurveyForStudentTest {

    @Test
    public void testQualifier() {
        Student student = mock(Student.class);
        CourseClass courseClass = mock(CourseClass.class);

        assertEquals(Survey.ENROLMENT.dot(Enrolment.STUDENT).eq(student)
                        .andExp(Survey.ENROLMENT.dot(Enrolment.COURSE_CLASS).eq(courseClass)),
                GetSurveyForStudent.valueOf(student, courseClass).getQualifier());
    }
}
