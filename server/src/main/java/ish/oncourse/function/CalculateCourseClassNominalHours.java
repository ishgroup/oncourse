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

import ish.messaging.ICourseClass;

import java.math.BigDecimal;

public class CalculateCourseClassNominalHours {

    private ICourseClass courseClass;

    private CalculateCourseClassNominalHours() {}

    public static CalculateCourseClassNominalHours valueOf(ICourseClass courseClass) {
        CalculateCourseClassNominalHours obj = new CalculateCourseClassNominalHours();
        obj.courseClass = courseClass;
        return obj;
    }

    public BigDecimal calculate() {
        BigDecimal result = CalculateCourseReportableHours.valueOf(courseClass.getCourse()).calculate();

        if (result.compareTo(BigDecimal.ZERO) == 0) {
            result = CalculateClassroomHours.valueOf(courseClass).calculate();
        }
        return result;
    }
}
