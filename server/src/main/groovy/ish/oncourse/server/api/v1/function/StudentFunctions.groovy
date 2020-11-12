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

package ish.oncourse.server.api.v1.function

import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.cayenne.Student

class StudentFunctions {

    static void mergeStudentToManyRelationshipsToA(Student a, Student b, List<String> mergeableProperties) {
        for (String property : mergeableProperties) {
            if (b.getValueForKey(property) != null && b.getValueForKey(property) instanceof List) {
                EntityFunctions.moveToManyEntityRelationshipToA(b, a, property)
            }
        }
    }

    static void mergeEnrolments(Student a, Student b) {
        for (Enrolment aEnr : a.enrolments) {
            for (Enrolment bEnr : b.enrolments) {
                if (aEnr.courseClass.id == bEnr.courseClass.id) {
                    AttendanceFunctions.mergeAttendances(aEnr, bEnr)
                }
            }
        }
    }
}
