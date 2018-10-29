package ish.oncourse.portal.util;

import ish.oncourse.model.Survey;
import org.apache.tapestry5.ValueEncoder;

public class SurveyEncoder implements ValueEncoder<Survey> {
    @Override
    public String toClient(Survey value) {
        return value.getId().toString();
    }

    @Override
    public Survey toValue(String clientValue) {
        return new Survey();
    }

    public static SurveyEncoder valueOf() {
        SurveyEncoder result = new SurveyEncoder();
        return result;
    }
}
