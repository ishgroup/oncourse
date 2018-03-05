package ish.oncourse.portal.util;

import ish.oncourse.model.Survey;
import org.apache.tapestry5.json.JSONObject;

public class SurveyToJSON {

    private Survey survey;
    private boolean isTutor;

    private static final String AVERAGE_SURVEY = "readOnly";

    private SurveyToJSON() {}

    public static SurveyToJSON valueOf(Survey survey, boolean isTutor) {
        SurveyToJSON obj = new SurveyToJSON();
        obj.survey = survey;
        obj.isTutor = isTutor;
        return obj;
    }

    public JSONObject get(){
        JSONObject result = new JSONObject();
        result.put(Survey.COURSE_SCORE.getName(), survey.getCourseScore());
        result.put(Survey.TUTOR_SCORE.getName(), survey.getTutorScore());
        result.put(Survey.VENUE_SCORE.getName(), survey.getVenueScore());
        result.put(Survey.COMMENT.getName(), survey.getComment());
        result.put(Survey.VISIBILITY.getName(), survey.getVisibility().getDatabaseValue());
        result.put(Survey.NET_PROMOTER_SCORE.getName(), survey.getNetPromoterScore());

        if (isTutor) {
            result.put(AVERAGE_SURVEY, isTutor);
        }
        return result;
    }
}
