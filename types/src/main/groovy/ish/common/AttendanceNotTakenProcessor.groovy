/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.common


import ish.oncourse.cayenne.CourseClassInterface
import ish.oncourse.cayenne.ModuleInterface
import ish.oncourse.cayenne.OutcomeInterface
import ish.oncourse.cayenne.SessionModuleInterface

class AttendanceNotTakenProcessor extends AttendanceProcessor{
    @Override
    List<Date> getAssessmentDueDates(CourseClassInterface courseClass, OutcomeInterface outcome) {
        def assessmentClassModules = getAssessmentClassModulesOf(courseClass, outcome)
        return getDefaultDatesOf(assessmentClassModules)
    }

    @Override
    List<SessionModuleInterface> getModulesOf(CourseClassInterface courseClass, ModuleInterface controlModule) {
        return getSessionModulesOf(courseClass, controlModule)
    }
}
