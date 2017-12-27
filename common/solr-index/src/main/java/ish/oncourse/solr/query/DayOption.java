package ish.oncourse.solr.query;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public enum DayOption {

    weekday("weekday"),
    weekend("weekend"),
    mon("Monday"),
    tues("Tuesday"),
    wed("Wednesday"),
    thurs("Thursday"),
    fri("Friday"),
    sat("Saturday"),
    sun("Sunday");

    private String fullName;

    DayOption(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return this.fullName;
    }
}