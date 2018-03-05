package ish.oncourse.portal.util;

import ish.oncourse.model.Survey;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.services.Request;

public class RequestToSurvey {

    private Survey dest;
    private Request src;

    private RequestToSurvey() {}

    public static RequestToSurvey valueOf(Survey survey, Request request) {
        RequestToSurvey obj = new RequestToSurvey();
        obj.dest = survey;
        obj.src = request;
        return obj;
    }

    public void parse() {
        String value = StringUtils.trimToNull(src.getParameter(Survey.TUTOR_SCORE.getName()));
        if (StringUtils.isNumeric(value)) {
            dest.setTutorScore(Integer.valueOf(value));
        }

        value = StringUtils.trimToNull(src.getParameter(Survey.VENUE_SCORE.getName()));
        if (StringUtils.isNumeric(value)) {
            dest.setVenueScore(Integer.valueOf(value));
        }

        value = StringUtils.trimToNull(src.getParameter(Survey.COURSE_SCORE.getName()));
        if (StringUtils.isNumeric(value)) {
            dest.setCourseScore(Integer.valueOf(value));
        }

        value = StringUtils.trimToNull(src.getParameter(Survey.NET_PROMOTER_SCORE.getName()));
        if (StringUtils.isNumeric(value)) {
            dest.setNetPromoterScore(Integer.valueOf(value));
        }

        value = StringUtils.trimToNull(src.getParameter(Survey.COMMENT.getName()));
        if (value != null) {
            dest.setComment(value);
        }
    }
}
