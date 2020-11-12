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

package ish.oncourse.server.payroll.filters;

import ish.common.types.AttendanceType;
import ish.oncourse.server.cayenne.ClassCost;

import java.util.Date;

import static ish.common.types.ClassCostRepetitionType.PER_SESSION;
import static ish.common.types.ClassCostRepetitionType.PER_TIMETABLED_HOUR;

public class ClassCostConfirmed {

    private ClassCost classCost;
    private Date until;

    private ClassCostConfirmed() {

    }

    public static ClassCostConfirmed valueOf(ClassCost classCost, Date until) {
        var classCostConfirmed = new ClassCostConfirmed();
        classCostConfirmed.classCost = classCost;
        classCostConfirmed.until = until;
        return classCostConfirmed;
    }

    private boolean getIsConfirmed() {
        return (classCost.getRepetitionType() == PER_SESSION || classCost.getRepetitionType() == PER_TIMETABLED_HOUR) &&
                        classCost.getTutorRole() != null &&
                        classCost.getTutorRole().getSessionsTutors().stream()
                                .noneMatch(tutorAttendance ->
                                        AttendanceType.UNMARKED.equals(tutorAttendance.getAttendanceType()) &&
                                                tutorAttendance.getSession().getStartDatetime().before(until));
    }

    public boolean isTrue() {
        return getIsConfirmed();
    }

    public boolean isFalse() {
        return !getIsConfirmed();
    }



}
