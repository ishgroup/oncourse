package ish.oncourse.cms.components;

import ish.oncourse.selectutils.ListSelectModel;
import ish.oncourse.services.courseclass.ClassAge;
import ish.oncourse.services.courseclass.ClassAgeType;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class ClassAgeField {


    @Property
    @Parameter(required = true)
    private String label;

    @Parameter(required = true)
    private ClassAge value;

    @Property
    @Parameter(required = true)
    private SelectModel types;

    private int days;

    private ClassAgeType type;



    @SetupRender
    public void setupRender() {
        days = value.getDays();
        type = value.getType();
    }


    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public ClassAgeType getType() {
        return type;
    }

    public void setType(ClassAgeType type) {
        this.type = type;
    }
}
