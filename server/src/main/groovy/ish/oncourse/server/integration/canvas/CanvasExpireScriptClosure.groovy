/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.integration.canvas

import groovy.transform.CompileStatic
import ish.oncourse.API
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.cayenne.Student
import ish.oncourse.server.scripting.ScriptClosure
import ish.oncourse.server.scripting.ScriptClosureTrait
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.commons.lang3.StringUtils

/**
 * Integration allows us to establish interaction between Canvas LMS and onCourse enrol system.
 *
 * This closure will iterate through all currently unsuspended users in Canvas and match their enrolments to enrolments in onCourse.
 * For each of those enrolments, look at the enrolment end date (by default this is the class end date) and
 * susped the Canvas user if all enrolments are past their end date.
 * ```
 * canvas_expire {
 *     ends new Date()
 * }
 * ```
 * Setting 'ends' is Date and if end dates of all user's enrollments are before 'ends' date => suspend the Canvas user
 */
@API
@CompileStatic
@ScriptClosure(key = "canvas_expire", integration = CanvasIntegration)
class CanvasExpireScriptClosure implements ScriptClosureTrait<CanvasIntegration>{
    Date ends

    def ends(Date ends){
        this.ends = ends
    }

    @Override
    Object execute(CanvasIntegration integration) {
        integration.initAuthHeader()
        ObjectContext context = integration.cayenneService.newContext
        Date expireDate = ends != null ? ends : new Date()

        def unsuspendedUsers = integration.getUnsuspendedUsers()
        unsuspendedUsers.each { user ->
            Map enrolmentsResp = integration.getEnrolments(user["id"]) as Map
            List enrolmentsCanvas = integration.responseToJson(enrolmentsResp) as List
            List<Long> enrolmentSisSectionIds = enrolmentsCanvas["sis_section_id"].findAll { StringUtils.isNumeric(it as CharSequence) }
            if (enrolmentSisSectionIds.isEmpty()) {
                return
            }

            if (StringUtils.isNumeric(user["sis_user_id"] as CharSequence)) {
                Long sisUserId = user["sis_user_id"] as Long
                def enrolmentsDbMatchedCanvas = ObjectSelect.query(Enrolment)
                        .where(Enrolment.STUDENT.dot(Student.CONTACT).dot(Contact.ID).eq(sisUserId)
                                .andExp(Enrolment.COURSE_CLASS.dot(CourseClass.ID).in(enrolmentSisSectionIds)))
                        .select(context)
                def isAllEnrolmentsExpired = !enrolmentsDbMatchedCanvas
                        .collect { it.courseClass.endDateTime != null && expireDate.after(it.courseClass.endDateTime) }
                        .contains(false)
                if (enrolmentsDbMatchedCanvas.size() > 0 && isAllEnrolmentsExpired) {
                    integration.suspendUser(user["id"])
                }
            }
        }

        return null
    }
}
