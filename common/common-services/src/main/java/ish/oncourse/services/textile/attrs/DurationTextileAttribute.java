package ish.oncourse.services.textile.attrs;

import java.util.ArrayList;
import java.util.List;

public enum DurationTextileAttribute {

    DISPLAY("display:"),
    DURATION("duration:");

    private String value;

    DurationTextileAttribute(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static List<String> getAttrValues() {
        DurationTextileAttribute[] values = values();
        List<String> attrValues = new ArrayList<>(values.length);

        for (DurationTextileAttribute attr : values) {
            attrValues.add(attr.getValue());
        }

        return attrValues;
    }
}
