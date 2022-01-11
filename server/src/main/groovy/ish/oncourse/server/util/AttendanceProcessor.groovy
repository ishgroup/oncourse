/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.util


import ish.oncourse.server.cayenne.AssessmentClassModule
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.Outcome
import ish.oncourse.server.cayenne.SessionModule

abstract class AttendanceProcessor {
    abstract List<Date> getAssessmentDueDates(CourseClass courseClass, Outcome outcome)

    abstract List<SessionModule> getModulesOf(CourseClass courseClass,
                                                       Outcome outcome)

    protected static List<Date> getDefaultDatesOf(List<AssessmentClassModule> assessmentClassModules){
        return assessmentClassModules*.assessmentClass*.dueDate
    }

    protected static List<AssessmentClassModule> getAssessmentClassModulesOf(CourseClass courseClass, Outcome outcome){
        return (courseClass.assessmentClasses*.assessmentClassModules
                .flatten() as List<AssessmentClassModule>)
                .findAll { acm -> acm.module == outcome.module }
    }

    protected static List<SessionModule> getSessionModulesOf(CourseClass courseClass, Outcome outcome){
        return (courseClass.sessions*.sessionModules
                .flatten() as List<SessionModule>)
                .findAll { sm -> sm.module == outcome.module }
    }
}