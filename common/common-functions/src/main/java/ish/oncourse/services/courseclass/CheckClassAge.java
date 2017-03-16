package ish.oncourse.services.courseclass;

import ish.oncourse.model.CourseClass;
import ish.oncourse.utils.TimestampUtilities;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Date;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class CheckClassAge {

    private ClassAge classAge;
    private CourseClass courseClass;

    public boolean check() {
        switch (classAge.getType()) {
            case afterClassStarts:
                return courseClass.getStartDate() == null || DateUtils.addDays(courseClass.getStartDate(), classAge.getDays())
                        .after(TimestampUtilities.normalisedDate(new Date()));
            case beforeClassStarts:
                return courseClass.getStartDate() == null || DateUtils.addDays(courseClass.getStartDate(), -classAge.getDays())
                        .after(TimestampUtilities.normalisedDate(new Date()));
            case afterClassEnds:
                return courseClass.getEndDate() == null || DateUtils.addDays(courseClass.getEndDate(), classAge.getDays())
                        .after(TimestampUtilities.normalisedDate(new Date()));
            case beforeClassEnds:
                return courseClass.getEndDate() == null || DateUtils.addDays(courseClass.getEndDate(), -classAge.getDays())
                        .after(TimestampUtilities.normalisedDate(new Date()));
            default:
                throw new IllegalArgumentException();
        }
    }

    public CheckClassAge courseClass(CourseClass courseClass)
    {
        CheckClassAge result = clone();
        result.courseClass = courseClass;
        return result;
    }

    protected CheckClassAge clone() {
        CheckClassAge result = new CheckClassAge();
        result.classAge = this.classAge;
        result.courseClass = this.courseClass;
        return result;
    }

    public CheckClassAge classAge(ClassAge classAge)
    {
        CheckClassAge result = clone();
        result.classAge = classAge;
        return result;
    }

}
