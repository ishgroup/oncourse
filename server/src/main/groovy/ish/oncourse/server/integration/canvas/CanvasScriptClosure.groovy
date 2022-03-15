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

import groovy.transform.CompileStatic
import ish.oncourse.API
import ish.oncourse.server.api.v1.function.CustomFieldFunctions
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.scripting.ScriptClosureTrait
import ish.oncourse.server.scripting.ScriptClosure
import org.apache.commons.lang.RandomStringUtils

import java.security.SecureRandom

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
 * 	   authentication_provider_id 1
 * 	   create_password "myCustomField"
 * 	   course_blueprint "ABC"
 * 	   add_tutors true
 * }
 * ```
 * Setting 'create_section' to true will create a new Canvas sections from the equivalent onCourse Class if one does not already exist inside your Canvas instance.
 * Setting 'create_student' to true will create a new Canvas User profile for the enrolled student if one does not exist in your Canvas instance.
 * Setting 'create_password' will generate random password for student to canvas and save to customField of Contact with key like create_password parameter
 * Setting 'course_blueprint' use if the course does not already exist in Canvas with the code provided, will create a new course from the blueprint specified.
 * Setting 'add_tutors' to true will enrol tutors from enrolment's class into the Canvas course as teachers
 * Setting 'student_attributes' map adds any valid parameters for Canvas user. Default parameters will be overwritten
 */
@API
@CompileStatic
@ScriptClosure(key = "canvas", integration = CanvasIntegration)
class CanvasScriptClosure implements ScriptClosureTrait<CanvasIntegration> {
    private static final char[] POSSIBLE_PASSWORD_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~`!@#\$%^&*()-_=+[{]}\\|;:\'\",<.>/?".toCharArray()
    private static final int PASSWORD_LENGTH = 8

    Enrolment enrolment
    String course_code
    String section_code
    boolean create_section
    boolean create_student
    int authentication_provider_id
    String create_password
    String course_blueprint
    boolean add_tutors
    Map student_attributes

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

    def authentication_provider_id(int authentication_provider_id){
        this.authentication_provider_id = authentication_provider_id
    }

    def create_password(String create_password){
        this.create_password = create_password
    }

    def course_blueprint(String course_blueprint){
        this.course_blueprint = course_blueprint
    }

    def add_tutors(boolean add_tutors){
        this.add_tutors = add_tutors
    }

    def student_attributes(Map student_attributes){
        this.student_attributes = student_attributes
    }

    @Override
    Object execute(CanvasIntegration integration) {
        integration.initAuthHeader()

        if (enrolment.student.contact.email) {
            Map userResp = integration.getUserByEmail(enrolment.student.contact.email) as Map
            List userJson = integration.responseToJson(userResp) as List
            userJson = userJson.findAll {enrolment.student.contact.email.equals(it["login_id"])}
            String password = null
            if(create_password)
                password = RandomStringUtils.random( PASSWORD_LENGTH, 0, POSSIBLE_PASSWORD_CHARS.length-1, false, false, POSSIBLE_PASSWORD_CHARS, new SecureRandom())

            List course = integration.getCourse(course_code) as List

            if (course.size() == 0) {
                if (course_blueprint != null) {
                    course = integration.createNewCourseFromBlueprint(course_blueprint, course_code, enrolment.courseClass.course.name, enrolment.courseClass.course.id.toString()) as List
                }
                else {
                    throw new IllegalArgumentException("Illegal state: There are no courses with specified code ${course_code} and blueprint course is not presented in script.")
                }
            }
            if (course.size() > 1) {
                throw new IllegalArgumentException("Illegal state: There are find more that one course for specified course code: ${course_code}. " +
                        "Please, specify more unique course code.")
            }

            def student
            if (userJson.size() == 0) {

                if (!create_student) {
                    throw new IllegalArgumentException("Illegal state, no student with email ${enrolment.student.contact.email} with student creation disabled.")
                }

                student = integration.createNewUser(enrolment.student.contact.fullName,
                        enrolment.student.contact.email,
                        enrolment.student.contact.id.toString(),
                        authentication_provider_id,
                        password,
                        student_attributes
                )

                if(create_password){
                    CustomFieldFunctions.addCustomFieldWithoutCommit(create_password, password, enrolment.student.contact, enrolment.context)
                    enrolment.context.commitChanges()
                }

            } else {

                // will return a list of one students, take the first item in the list
                student = userJson.first()
            }

            def section = integration.getSection(section_code, course["id"][0])

            if (!section) {
                if (!create_section) {
                    throw new IllegalArgumentException("Illegal state, no section for courseClass ${enrolment.courseClass.uniqueCode} with section creation disabled.")
                }

                section = integration.createSection(section_code, course["id"][0], enrolment.courseClass.id)
            }

            integration.enrolUser(student["id"], section["id"])
            integration.checkIfUserSuspendedAndUnsuspend(student["id"])

            if (add_tutors) {
                integration.enrolTeachers(enrolment.courseClass.tutorRoles.tutor.contact, course["id"][0])
            }

        }
        return null
    }
}
