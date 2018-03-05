package ish.oncourse.portal.services.survey;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Student;
import org.apache.cayenne.ObjectContext;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class CreateSurveyTest {

    @Test
    public void testQualifier() {

        ObjectContext context = mock(ObjectContext.class);
        CourseClass courseClass = mock(CourseClass.class);
        Student student = mock(Student.class);

        CreateSurvey creator = CreateSurvey.valueOf(context, courseClass, student);

        assertEquals(Enrolment.STUDENT.eq(student)
                .andExp(Enrolment.COURSE_CLASS.eq(courseClass))
                .andExp(Enrolment.STATUS.in(Enrolment.VALID_ENROLMENTS)),
                creator.getEnrolmentQualifier());
    }
}
