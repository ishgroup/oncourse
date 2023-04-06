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

package ish.oncourse.server.cayenne

trait TutorAttendanceTrait {

    abstract Session getSession()

    abstract CourseClassTutor getCourseClassTutor()

    boolean hasPayslips() {
        return !getPayslips().empty
    }
    
    List<Payslip> getPayslips() {
        List<PayLine> payLines = getCourseClassTutor().classCosts*.paylines.flatten().grep() as List<PayLine>
        return payLines.findAll {it.session && it.session.equalsIgnoreContext(session)}*.payslip.unique() 
    }
    
}
