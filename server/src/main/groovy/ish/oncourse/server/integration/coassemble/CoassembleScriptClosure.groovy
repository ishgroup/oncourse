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

package ish.oncourse.server.integration.coassemble

import groovy.transform.CompileStatic
import ish.oncourse.API
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.scripting.ScriptClosureTrait
import ish.oncourse.server.scripting.ScriptClosure
/**
 * Integration allows us to establish interaction between Coassemble (previously called E-coach) and onCourse enrol system.
 *
 * Add enrolments to Enrolments automatically as they are enrolled in onCourse.
 * ```
 * ecoach {
 *     enrolment record
 * }
 * ```
 *
 * Use the name option if you have more than one coassemble integration and you want to push to only one.
 * ```
 * ecoach {
 *     name "name of integration"
 *     enrolment record
 * }
 * ```
 */
@API
@CompileStatic
@ScriptClosure(key = "coassemble", integration = CoassembleIntegration)
class CoassembleScriptClosure implements ScriptClosureTrait<CoassembleIntegration> {

    Enrolment enrolment

    def enrolment(Enrolment enrolment) {
        this.enrolment = enrolment
    }

    /**
     * Execute the closure with the configuration from the passed integration
     *
     * @param integration
     */
    @Override
    void execute(CoassembleIntegration integration) {
        if (enrolment.student.contact.email) {
            // make or get students
            def member = integration.postMember(enrolment)

            //get course id for group add
            List<Map<String, String>> courses = (integration.getCourses() as List<Map<String, String>>)

            def course = courses.find { Map<String, String> c -> c.title == enrolment.courseClass.course.name }

            // get groups (classes) in course
            List<Map<String, String>> courseClasses = (integration.getGroups(course["id"]) as  List<Map<String, String>>)

            def courseClass = courseClasses.find { cc -> cc.name == enrolment.courseClass.uniqueCode }

            if (!courseClass) {
                //create groupClass if one does not exist
                courseClass = integration.postGroup(enrolment, course["id"])
            }

            ////add student to group
            integration.enrolMemberToGroup(member["id"], courseClass["id"])
        }
    }
}
