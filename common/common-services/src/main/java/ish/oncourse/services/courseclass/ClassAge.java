package ish.oncourse.services.courseclass;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class ClassAge {
    private static final Logger logger = LogManager.getLogger();

    private int days = 0;
    private ClassAgeType type = ClassAgeType.beforeClassEnds;

    public int getDays() {
        return days;
    }

    public ClassAgeType getType() {
        return type;
    }

    public static ClassAge valueOf(int days, ClassAgeType type) {
        ClassAge classAge = new ClassAge();
        classAge.days = days;
        classAge.type = type;
        return classAge;
    }

    public static ClassAge valueOf(String stringDays, String stringType) {
        ClassAge classAge = new ClassAge();

        if (stringDays == null || stringType == null) {
            return classAge;
        }

        try {
            classAge.days = Integer.valueOf(stringDays);
        } catch (NumberFormatException e) {
            logger.error(e);
        }

        try {
            classAge.type = ClassAgeType.valueOf(stringType);
        } catch (IllegalArgumentException e) {
            logger.error(e);
        }
        return classAge;
    }
}
