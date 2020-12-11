package ish.oncourse.services.textile.attrs;

import java.util.ArrayList;
import java.util.List;

public enum TutorsTextileAttributes {
    
    TAG_NAME("tagName:"),
    ID("id:"),
    COUNT("count:");

    private String value;

    private TutorsTextileAttributes(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static List<String> getAttrValues() {
        TutorsTextileAttributes[] values = values();
        List<String> attrValues = new ArrayList<>(values.length);

        for (TutorsTextileAttributes attr : values) {
            attrValues.add(attr.getValue());
        }

        return attrValues;
    }
}
