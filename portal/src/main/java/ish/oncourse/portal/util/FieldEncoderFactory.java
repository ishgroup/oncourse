package ish.oncourse.portal.util;

import ish.oncourse.model.Field;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.services.ValueEncoderFactory;

import javax.inject.Inject;

public class FieldEncoderFactory implements ValueEncoderFactory<Field> {

    @Inject
    public ICayenneService cayenneService;

    @Override
    public ValueEncoder<Field> create(Class<Field> type) {
        return FieldEncoder.valueOf(null);
    }
}
