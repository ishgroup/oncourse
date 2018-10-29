package ish.oncourse.portal.util;

import ish.oncourse.model.Survey;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.services.ValueEncoderFactory;

public class SurveyValueEncoderFactory implements ValueEncoderFactory<Survey> {
    @Override
    public ValueEncoder<Survey> create(Class<Survey> type) {
        return SurveyEncoder.valueOf();
    }
}
