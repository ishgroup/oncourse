package ish.oncourse.portal.util;

import ish.oncourse.model.FieldHeading;
import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.ValueEncoder;

public class FieldHeadingEncoder implements ValueEncoder<FieldHeading> {

    private ObjectContext context;

    private FieldHeadingEncoder() {}

    public static FieldHeadingEncoder valueOf(ObjectContext context) {
        FieldHeadingEncoder obj = new FieldHeadingEncoder();
        obj.context = context;
        return obj;
    }

    @Override
    public String toClient(FieldHeading value) {
        return value.toString();
    }

    @Override
    public FieldHeading toValue(String clientValue) {
        return context.newObject(FieldHeading.class);
    }
}
