/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.common

import ish.oncourse.cayenne.AssessmentClassModuleInterface
import ish.oncourse.cayenne.CourseClassInterface
import ish.oncourse.cayenne.ModuleInterface
import ish.oncourse.cayenne.OutcomeInterface
import ish.oncourse.cayenne.SessionModuleInterface

abstract class AttendanceProcessor {
    abstract List<Date> getAssessmentDueDates(CourseClassInterface courseClass, OutcomeInterface outcome)

    abstract List<SessionModuleInterface> getModulesOf(CourseClassInterface courseClass,
                                                       ModuleInterface controlModule,
                                                       OutcomeInterface outcome)

    protected static List<Date> getDefaultDatesOf(List<AssessmentClassModuleInterface> assessmentClassModules){
        return assessmentClassModules*.assessmentClass*.dueDate
    }

    protected static List<AssessmentClassModuleInterface> getAssessmentClassModulesOf(CourseClassInterface courseClass, OutcomeInterface outcome){
        return (courseClass.assessmentClasses*.assessmentClassModules
                .flatten() as List<AssessmentClassModuleInterface>)
                .findAll { acm -> acm.module == outcome.module }
    }

    protected static List<SessionModuleInterface> getSessionModulesOf(CourseClassInterface courseClass, ModuleInterface controlModule){
        return (courseClass.sessions*.sessionModules
                .flatten() as List<SessionModuleInterface>)
                .findAll { sm -> sm.module == controlModule }
    }
}