package ish.oncourse.services.textile.attrs;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public enum LocationTextileAttribute {
    DISPLAY("display:"),
    SUBURB("suburb:"),
    POSTCODE("postcode:"),
    DISTANCE("distance:"),
    SITE("site:");

    private String value;

    LocationTextileAttribute(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static List<String> getAttrValues() {
        LocationTextileAttribute[] values = values();
        List<String> attrValues = new ArrayList<>(values.length);

        for (LocationTextileAttribute attr : values) {
            attrValues.add(attr.getValue());
        }

        return attrValues;
    }
}
