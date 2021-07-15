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

import ish.oncourse.server.cayenne.Course;
import ish.oncourse.server.cayenne.Module;

import java.math.BigDecimal;

public class CalculateCourseReportableHours {

    private Course course;

    private CalculateCourseReportableHours() {}

    public static CalculateCourseReportableHours valueOf(Course course) {
        CalculateCourseReportableHours obj = new CalculateCourseReportableHours();
        obj.course = course;
        return obj;
    }

    public BigDecimal calculate() {
        BigDecimal sum = BigDecimal.ZERO;
        if (course != null && course.getModules() != null) {
            for (Module m : course.getModules()) {
                if (m.getNominalHours() != null) {
                    sum = sum.add(m.getNominalHours());
                }
            }
        }
        return sum;
    }
}
