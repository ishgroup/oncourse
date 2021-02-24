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

package ish.oncourse.server.integration.talentlms

import ish.oncourse.API
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.Student
import ish.oncourse.server.scripting.ScriptClosure
import ish.oncourse.server.scripting.ScriptClosureTrait
/**
 *
 * Export successful enrolment record to Talent LMS system. This action will search for students matching the onCourse
 * email address against the Talent LMS email, and creates them if not found.
 * That student will be enrolled in the Learndash course with code matching the course code in onCourse
 *
 * ```
 *
 * talentLMS {
 *     action "enrol"
 *     course enrolment.courseClass.course.code
 *     student enrolment.student
 * }
 * ```
 */
@API
@ScriptClosure(key = "talentLMS", integration = TalentLMSIntegration)
class TalentLMSScriptClosure implements ScriptClosureTrait<TalentLMSIntegration> {
    String action
    String course
    Student student

    void action(String action) {
        this.action = action
    }

    void course(String course) {
        this.course = course
    }

    void student(Student student) {
        this.student = student
    }

    @Override
    Object execute(TalentLMSIntegration integration) {
        String courseId = integration.getCourseId(course)
        if (!courseId) {
            throw new IllegalStateException("Course ${course} is missing from ${integration.baseUrl}")
        }
        String email = student.contact.email
        if (!email) {
            throw new IllegalStateException("Student (contact id: $student.contact.id) has no email address")
        }
        Contact contact = student.contact
        String studentId = integration.getUserId(contact.email)
        if (!studentId) {
            studentId = integration.createUser(contact.email, contact.firstName, contact.lastName)
        }
        integration.enrol(courseId, studentId)
        return null
    }
}
