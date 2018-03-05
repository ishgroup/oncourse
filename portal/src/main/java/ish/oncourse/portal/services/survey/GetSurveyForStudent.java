package ish.oncourse.portal.services.survey;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Student;
import ish.oncourse.model.Survey;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.query.ObjectSelect;

public class GetSurveyForStudent {

    private Student student;
    private CourseClass courseClass;

    private GetSurveyForStudent() {}

    public static GetSurveyForStudent valueOf(Student student, CourseClass courseClass) {
        GetSurveyForStudent obj = new GetSurveyForStudent();
        obj.student = student;
        obj.courseClass = courseClass;
        return obj;
    }

    public Survey get() {
        return ObjectSelect.query(Survey.class).where(getQualifier()).selectFirst(student.getObjectContext());
    }

    protected Expression getQualifier() {
        return Survey.ENROLMENT.dot(Enrolment.STUDENT).eq(student)
                .andExp(Survey.ENROLMENT.dot(Enrolment.COURSE_CLASS).eq(courseClass));
    }
}
