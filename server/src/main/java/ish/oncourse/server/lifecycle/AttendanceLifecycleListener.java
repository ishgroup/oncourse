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

package ish.oncourse.server.lifecycle;

import ish.common.types.AttendanceType;
import ish.oncourse.server.cayenne.TutorAttendance;
import org.apache.cayenne.annotation.PreUpdate;

public class AttendanceLifecycleListener {

    @PreUpdate(value = TutorAttendance.class)
    public void preUpdate(TutorAttendance tutorAttendance) {
        if (AttendanceType.DID_NOT_ATTEND_WITHOUT_REASON.equals(tutorAttendance.getAttendanceType())
                && tutorAttendance.getActualPayableDurationMinutes() != 0) {
            tutorAttendance.setActualPayableDurationMinutes(0);
        }
    }
}
