package ish.oncourse.services.courseclass;

import ish.oncourse.model.College;
import ish.oncourse.model.CourseClass;
import ish.oncourse.services.preference.GetPreference;
import ish.oncourse.services.preference.Preferences;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Date;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class CheckClassAge {

    private ClassAge classAge;
    private CourseClass courseClass;

    public boolean check() {
        Date startOfDay = new Date();
        switch (classAge.getType()) {
            case afterClassStarts:
                return courseClass.getStartDate() == null || DateUtils.addDays(courseClass.getStartDate(), classAge.getDays())
                        .after(startOfDay);
            case beforeClassStarts:
                return courseClass.getStartDate() == null || DateUtils.addDays(courseClass.getStartDate(), -classAge.getDays())
                        .after(startOfDay);
            case afterClassEnds:
                return courseClass.getEndDate() == null || DateUtils.addDays(courseClass.getEndDate(), classAge.getDays())
                        .after(startOfDay);
            case beforeClassEnds:
                return courseClass.getEndDate() == null || DateUtils.addDays(courseClass.getEndDate(), -classAge.getDays())
                        .after(startOfDay);
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

    public CheckClassAge college(College college) {
        String  age = new GetPreference(college, Preferences.STOP_WEB_ENROLMENTS_AGE, college.getObjectContext()).getValue();
        String type = new GetPreference(college, Preferences.STOP_WEB_ENROLMENTS_AGE_TYPE, college.getObjectContext()).getValue();
        return classAge(ClassAge.valueOf(age, type));
    }
}
