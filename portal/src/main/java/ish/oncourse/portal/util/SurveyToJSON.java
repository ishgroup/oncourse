package ish.oncourse.portal.util;

import ish.oncourse.model.Survey;
import org.apache.tapestry5.json.JSONObject;

public class SurveyToJSON {

    private Survey survey;
    private boolean isTutor;

    private static final String JSONPROPERTY_readOnly = "readOnly";

    private SurveyToJSON() {}

    public static SurveyToJSON valueOf(Survey survey, boolean isTutor) {
        SurveyToJSON obj = new SurveyToJSON();
        obj.survey = survey;
        obj.isTutor = isTutor;
        return obj;
    }

    public JSONObject get(){
        JSONObject result = new JSONObject();
        result.put(Survey.COURSE_SCORE_PROPERTY, survey.getCourseScore());
        result.put(Survey.TUTOR_SCORE_PROPERTY, survey.getTutorScore());
        result.put(Survey.VENUE_SCORE_PROPERTY, survey.getVenueScore());
        result.put(Survey.COMMENT_PROPERTY, survey.getComment());
        result.put(Survey.VISIBILITY_PROPERTY, survey.getVisibility().getDatabaseValue());
        result.put(Survey.NET_PROMOTER_SCORE_PROPERTY, survey.getNetPromoterScore());

        if (isTutor) {
            result.put(JSONPROPERTY_readOnly, isTutor);
        }
        return result;
    }
}
