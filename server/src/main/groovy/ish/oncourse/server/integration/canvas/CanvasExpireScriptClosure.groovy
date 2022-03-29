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
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.query.ObjectSelect
import org.apache.commons.lang3.StringUtils

import java.text.Format
import java.text.SimpleDateFormat

/**
 * This automation component will iterate through all currently unsuspended users in Canvas and match their
 * enrolments to enrolments in onCourse.Suspend the Canvas user if all their enrolments have completed.
 *
 * ```
 * canvas_expire { }
 * ```
 *
 * By default the enrolment will be deemed to have ended when the class is complete. If you want to specify a
 * different end date you can do that with a simple expression like this:
 * ```
 * canvas_expire {
 *     ends "enrolment.customFieldEndDate"
 * }
 * ```
 *
 * Or with an expression like this:
 * ```
 * canvas_expire {
 *     ends "enrolment.courseClass.endDateTime + 10"
 * }
 * ```
 */
@API
@CompileStatic
@ScriptClosure(key = "canvas_expire", integration = CanvasIntegration)
class CanvasExpireScriptClosure implements ScriptClosureTrait<CanvasIntegration>{

    private static final Format format = new SimpleDateFormat("yyyy-MM-dd")

    String ends

    def ends(String ends){
        this.ends = ends
    }

    @Override
    Object execute(CanvasIntegration integration) {
        integration.initAuthHeader()

        ends = ends != null ? ends : "enrolment.courseClass.endDateTime"
        String pathToEndsDate
        if (ends.startsWith("enrolment.")) {
            pathToEndsDate = ends.replaceFirst("enrolment\\.", "")
        } else {
            throw new IllegalArgumentException("Wrong 'ends' variable: '${ends}'.")
        }

        ObjectContext context = integration.cayenneService.newContext
        def expressionNotExpireOrNullDate = createExpression(integration, pathToEndsDate, context)
        def unsuspendedUsers = integration.getUnsuspendedUsers()
        unsuspendedUsers.each { user ->
            List enrolmentsCanvas = integration.getAllUserEnrolments(user["id"])
            List<Long> enrolmentSisSectionIds = enrolmentsCanvas["sis_section_id"].findAll { StringUtils.isNumeric(it as CharSequence) }
            if (enrolmentSisSectionIds.isEmpty()) {
                return
            }

            if (StringUtils.isNumeric(user["sis_user_id"] as CharSequence)) {
                Long sisUserId = user["sis_user_id"] as Long
                def enrolmentsDbMatchedCanvasCount = ObjectSelect.query(Enrolment)
                        .where(Enrolment.STUDENT.dot(Student.CONTACT).dot(Contact.ID).eq(sisUserId)
                                .andExp(Enrolment.COURSE_CLASS.dot(CourseClass.ID).in(enrolmentSisSectionIds)))
                        .selectCount(context)
                def activeEnrolmentsOrNullEndDateCount = ObjectSelect.query(Enrolment)
                        .where(Enrolment.STUDENT.dot(Student.CONTACT).dot(Contact.ID).eq(sisUserId)
                                .andExp(Enrolment.COURSE_CLASS.dot(CourseClass.ID).in(enrolmentSisSectionIds))
                                .andExp(expressionNotExpireOrNullDate)
                        )
                        .selectCount(context)
                if (enrolmentsDbMatchedCanvasCount > 0 && activeEnrolmentsOrNullEndDateCount == 0) {
                    integration.suspendUser(user["id"])
                }
            }
        }

        return null
    }

    private static Expression createExpression(CanvasIntegration integration, String pathToEndsDate, ObjectContext context) {
        Date expireDate = new Date()
        String aqlQuery = pathToEndsDate + " > " + format.format(expireDate) + " or " + pathToEndsDate + " is null"
        def result = integration.antlrAqlService.compile(aqlQuery, Enrolment.class, context)
        if (result.errors) {
            throw new IllegalArgumentException("Can not compile aql query: '${aqlQuery}'. ${result.errors.message}")
        }
        return result.cayenneExpression.get()
    }
}
