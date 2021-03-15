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

package ish.oncourse.server.integration.canvas

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import ish.oncourse.API
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.scripting.ScriptClosureTrait
import ish.oncourse.server.scripting.ScriptClosure
/**
 * Integration allows us to establish interaction between Canvas LMS and onCourse enrol system.
 *
 * 1. Add enrolments to Enrolments automatically as they are enrolled in onCourse.
 * ```
 * Canvas {
 *     name "Canvas integration 1"
 *     enrolment e
 *     course_code "My-Canvas-Course"
 *     section_code "key." + e.courseClass.uniqueCode
 * 	   create_section true
 * 	   create_student true
 * }
 * ```
 * Setting 'create_section' to true will create a new Canvas sections from the equivalent onCourse Class if one does not already exist inside your Canvas instance.
 * Setting 'create_student' to true will create a new Canvas User profile for the enrolled student if one does not exist in your Canvas instance.
 */
@API
@CompileStatic
@ScriptClosure(key = "canvas", integration = CanvasIntegration)
class CanvasScriptClosure implements ScriptClosureTrait<CanvasIntegration> {

    Enrolment enrolment
    String course_code
    String section_code
    boolean create_section
    boolean create_student

    def enrolment(Enrolment enrolment) {
        this.enrolment = enrolment
    }

    def course_code(String course_code) {
        this.course_code = course_code
    }

    def section_code(String section_code) {
        this.section_code = section_code
    }

    def create_section(boolean create_section) {
        this.create_section = create_section
    }

    def create_student(boolean create_student) {
        this.create_student = create_student
    }


    @Override
    Object execute(CanvasIntegration integration) {
        integration.initAuthHeader()

        if (enrolment.student.contact.email) {
            Map userResp = integration.getUserByEmail(enrolment.student.contact.email) as Map
            List userJson = integration.responseToJson(userResp) as List

            List course = integration.getCourse(course_code) as List

            if (course.size() == 0) {
                throw new IllegalArgumentException("There are no courses with specified code ${course_code}")
            }

            def student
            if (userJson.size() == 0) {

                if (!create_student) {
                    throw new IllegalArgumentException("Illegal state, no student with email ${enrolment.student.contact.email} with student creation disabled.")
                }

                student = integration.createNewUser(enrolment.student.contact.fullName, enrolment.student.contact.email)
            } else {

                // will return a list of one students, take the first item in the list
                student = userJson.first()
            }

            def section = integration.getSection(section_code, course["id"])

            if (!section) {
                if (!create_section) {
                    throw new IllegalArgumentException("Illegal state, no section for courseClass ${enrolment.courseClass.uniqueCode} with section creation disabled.")
                }

                section = integration.createSection(section_code, course["id"])
            }

            integration.enrolUser(student["id"], section["id"])

        }
        return null
    }
}
