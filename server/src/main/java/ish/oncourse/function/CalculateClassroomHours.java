/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.function;

import ish.oncourse.server.cayenne.CourseClass;
import ish.oncourse.server.cayenne.Session;
import ish.util.DurationFormatter;

import java.math.BigDecimal;

public class CalculateClassroomHours {

    private CourseClass courseClass;

    private CalculateClassroomHours() {}

    public static CalculateClassroomHours valueOf(CourseClass courseClass) {
        CalculateClassroomHours obj = new CalculateClassroomHours();
        obj.courseClass = courseClass;
        return obj;
    }

    public BigDecimal calculate() {
        BigDecimal sum = BigDecimal.ZERO;
        if (courseClass.getSessions() != null && courseClass.getSessions().size() > 0) {
            for (Session s : courseClass.getSessions()) {
                sum = sum.add(s.getDurationInHours());
            }
        }
        return sum;
    }
}
