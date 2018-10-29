package ish.oncourse.portal.util;

import ish.oncourse.model.Field;
import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.ValueEncoder;

public class FieldEncoder implements ValueEncoder<Field> {

    public ObjectContext context;

    private FieldEncoder() {}

    public static FieldEncoder valueOf(ObjectContext context) {
        FieldEncoder obj = new FieldEncoder();
        obj.context = context;
        return obj;
    }

    @Override
    public String toClient(Field value) {
        return value.toString();
    }

    @Override
    public Field toValue(String clientValue) {
        return context.newObject(Field.class);
    }
}
