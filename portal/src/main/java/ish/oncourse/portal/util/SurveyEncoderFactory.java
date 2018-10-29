package ish.oncourse.portal.util;

import ish.oncourse.model.Survey;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.services.ValueEncoderFactory;

import javax.inject.Inject;

public class SurveyEncoderFactory implements ValueEncoderFactory<Survey> {

    @Inject
    public ICayenneService cayenneService;

    @Override
    public ValueEncoder<Survey> create(Class<Survey> type) { return SurveyEncoder.valueOf(null); }
}
