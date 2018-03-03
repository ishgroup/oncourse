package ish.oncourse.services.survey;

import ish.common.types.SurveyVisibility;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Survey;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.query.ObjectSelect;

import java.util.List;

public class GetAverageSurvey {

    private ObjectContext nonReplicating;
    private CourseClass courseClass;

    private GetAverageSurvey() {
    }

    public static GetAverageSurvey valueOf(ObjectContext nonReplicating, CourseClass courseClass) {
        GetAverageSurvey obj = new GetAverageSurvey();
        obj.nonReplicating = nonReplicating;
        obj.courseClass = courseClass;
        return obj;
    }

    public Survey get() {
        List<Survey> surveys = ObjectSelect.query(Survey.class)
                .where(getQualifier())
                .select(nonReplicating);

        return calculateAverage(initTransportObject(), surveys);
    }

    protected Expression getQualifier() {
        return Survey.ENROLMENT.dot(Enrolment.COURSE_CLASS).eq(courseClass);
    }

    protected Survey calculateAverage(Survey result, List<Survey> surveys) {
        for (Survey survey : surveys) {
            result.setCourseScore(result.getCourseScore() + survey.getCourseScore());
            result.setVenueScore(result.getVenueScore() + survey.getVenueScore());
            result.setTutorScore(result.getTutorScore() + survey.getTutorScore());
            result.setNetPromoterScore(result.getNetPromoterScore() + survey.getNetPromoterScore());
        }

        int size = surveys.size();
        if (size != 0) {
            result.setCourseScore(((int) Math.floor(result.getCourseScore() / size)));
            result.setTutorScore(((int) Math.floor(result.getTutorScore() / size)));
            result.setVenueScore(((int) Math.floor(result.getVenueScore() / size)));
            result.setNetPromoterScore(((int) Math.floor(result.getNetPromoterScore() / size)));
        }
        return result;
    }

    protected Survey initTransportObject() {
        Survey result = new Survey();
        result.setCourseScore(0);
        result.setVenueScore(0);
        result.setTutorScore(0);
        result.setNetPromoterScore(0);
        result.setVisibility(SurveyVisibility.TESTIMONIAL);
        return result;
    }
}
