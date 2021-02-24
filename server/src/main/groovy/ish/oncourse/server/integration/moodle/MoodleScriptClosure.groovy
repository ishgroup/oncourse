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

package ish.oncourse.server.integration.moodle

import groovy.transform.CompileStatic
import ish.oncourse.API
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.scripting.ScriptClosureTrait
import ish.oncourse.server.scripting.ScriptClosure
import ish.util.SecurityUtil

/**
 * Export successful enrolment record to moodle system. This action will search for students matching the onCourse
 * email address against the Moodle username, and creates them if not found.
 *
 * ```
 * moodle {
 *      name 'Moodle integration'
 *      enrolment record
 * }
 * ```
 *
 * The action finds courses in Moodle by 'shortname' field against the course name in onCourse. You can use
 * the 'courseReference' option to select a different field in Moodle and map it against any value from onCourse.
 *
 * ```
 * moodle {
 *      name "Moodle integration"
 *      enrolment record
 *      courseReference 'courseId': record.courseClass.course.code
 * }
 * ```
 *
 */

@API
@CompileStatic
@ScriptClosure(key = "moodle", integration = MoodleIntegration)
class MoodleScriptClosure implements ScriptClosureTrait<MoodleIntegration> {

    Enrolment enrolment
    Map<String,String> courseReference

    def enrolment(Enrolment enrolment) {
        this.enrolment = enrolment
    }

    def courseReference(Map<String,String> courseReference) {
        this.courseReference = courseReference
    }

    /**
    * Execute the closure with the configuration from the passed integration
    *
    * @param integration
    */
    @Override
    Object execute(MoodleIntegration integration) {
        if (enrolment.student.contact.email) {
            def userId = integration.getUserIdByEmail(enrolment.student.contact.email)
            if (!userId) {
                //Moodle username field requirement: Only lowercase letters allowed
                def userName = enrolment.student.contact.email?.toLowerCase()

                // create new Moodle user with name equal to student email and random 8 character password
                userId = (integration.createUser(userName,
                        // Moodle expects passwords to contain different types of chars
                        SecurityUtil.generateRandomPassword(8) + '1Aa!',
                        enrolment.student.contact) as Map<String, String>)['id']
            }

            def course = integration.getCourseBy(enrolment.courseClass.course.name, courseReference)

            if (course) {
                integration.enrolUsers(userId, course["id"])
            }
        }
        return null
    }
}
