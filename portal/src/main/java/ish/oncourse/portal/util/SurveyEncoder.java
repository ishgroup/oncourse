package ish.oncourse.portal.util;

import ish.oncourse.model.Survey;
import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.ValueEncoder;

public class SurveyEncoder implements ValueEncoder<Survey> {

    private ObjectContext context;

    private SurveyEncoder() {}

    public static SurveyEncoder valueOf(ObjectContext context) {
        SurveyEncoder obj = new SurveyEncoder();
        obj.context = context;
        return obj;
    }

    @Override
    public String toClient(Survey value) {
        return value.getId().toString();
    }

    @Override
    public Survey toValue(String clientValue) {
        return new Survey();
    }
}
