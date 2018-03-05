package ish.oncourse.services.survey;

import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Survey;
import org.apache.cayenne.ObjectContext;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class GetSurveysByCourseTest {

    @Test
    private void test() {
        ObjectContext context = mock(ObjectContext.class);
        Course course = mock(Course.class);

        assertEquals(Survey.COLLEGE.eq(course.getCollege())
                .andExp(Survey.ENROLMENT
                        .dot(Enrolment.COURSE_CLASS)
                        .dot(CourseClass.COURSE)
                        .eq(course)), GetSurveysByCourse.valueOf(context, course).getQualifier());
    }
}
