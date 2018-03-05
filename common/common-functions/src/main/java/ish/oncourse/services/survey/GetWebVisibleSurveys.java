package ish.oncourse.services.survey;

import ish.common.types.SurveyVisibility;
import ish.oncourse.model.Survey;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.query.QueryCacheStrategy;

import java.util.List;

public class GetWebVisibleSurveys {

    private List<Survey> surveys;
    protected Expression VISIBLE = Survey.TESTIMONIAL.isNotNull().andExp(Survey.VISIBILITY.eq(SurveyVisibility.TESTIMONIAL));

    private GetWebVisibleSurveys() {}

    public static GetWebVisibleSurveys valueOf(List<Survey> surveys) {
        GetWebVisibleSurveys obj = new GetWebVisibleSurveys();
        obj.surveys = surveys;
        return obj;
    }

    public List<Survey> get() {
        return VISIBLE.filterObjects(surveys);
    }
}
