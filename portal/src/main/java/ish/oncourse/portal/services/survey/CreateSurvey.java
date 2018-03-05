package ish.oncourse.portal.services.survey;

import ish.oncourse.model.*;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.query.ObjectSelect;

public class CreateSurvey {

    private ObjectContext context;
    private CourseClass courseClass;
    private Student student;

    private CreateSurvey() {
    }

    public static CreateSurvey valueOf(ObjectContext context, CourseClass courseClass, Student student) {
        CreateSurvey obj = new CreateSurvey();
        obj.context = context;
        obj.courseClass = courseClass;
        obj.student = student;
        return obj;
    }

    public Survey create() {
        Survey survey = context.newObject(Survey.class);
        survey.setCollege(context.localObject(student.getContact().getCollege()));
        survey.setEnrolment(getEnrolment());
        return survey;
    }

    protected Enrolment getEnrolment() {
        return ObjectSelect.query(Enrolment.class)
                .where(getEnrolmentQualifier())
                .selectFirst(context);
    }

    protected Expression getEnrolmentQualifier() {
        return Enrolment.STUDENT.eq(student)
                .andExp(Enrolment.COURSE_CLASS.eq(courseClass))
                .andExp(Enrolment.STATUS.in(Enrolment.VALID_ENROLMENTS));
    }
}
