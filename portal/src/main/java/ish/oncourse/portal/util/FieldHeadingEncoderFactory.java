package ish.oncourse.portal.util;

import com.google.inject.Inject;
import ish.oncourse.model.FieldHeading;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.services.ValueEncoderFactory;

public class FieldHeadingEncoderFactory implements ValueEncoderFactory<FieldHeading> {

    @Inject
    private ICayenneService cayenneService;

    @Override
    public ValueEncoder<FieldHeading> create(Class<FieldHeading> type) {
        return FieldHeadingEncoder.valueOf(null);
    }
}
