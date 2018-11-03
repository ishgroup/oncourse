package ish.oncourse.portal.services.survey;

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
        int courseScores = 0;
        int venueScores = 0;
        int tutorScores = 0;
        int netPromoterScores = 0;

        for (Survey survey : surveys) {
            if (survey.getCourseScore() != null &&  survey.getCourseScore() > 0) {
                result.setCourseScore(result.getCourseScore() + survey.getCourseScore());
                courseScores++;
            }
            if (survey.getVenueScore() != null &&  survey.getVenueScore() > 0) {
                result.setVenueScore(result.getVenueScore() + survey.getVenueScore());
                venueScores++;
            }
            if (survey.getTutorScore() != null &&  survey.getTutorScore() > 0) {
                result.setTutorScore(result.getTutorScore() + survey.getTutorScore());
                tutorScores++;
            }
            if (survey.getNetPromoterScore() != null &&  survey.getNetPromoterScore() > 0) {
                result.setNetPromoterScore(result.getNetPromoterScore() + survey.getNetPromoterScore());
                netPromoterScores++;
            }
        }
        
        if (result.getVenueScore() > 0) {
            result.setVenueScore((int)(Math.round((double)result.getVenueScore() / venueScores)));
        }
        if (result.getCourseScore() > 0) {
            result.setCourseScore((int)(Math.round((double)result.getCourseScore() / courseScores)));
        }   
        if (result.getTutorScore() > 0) {
            result.setTutorScore((int)(Math.round((double)result.getTutorScore() / tutorScores)));
        }   
        if (result.getNetPromoterScore() > 0) {
            result.setNetPromoterScore((int)(Math.round((double)result.getNetPromoterScore() / netPromoterScores)));
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
