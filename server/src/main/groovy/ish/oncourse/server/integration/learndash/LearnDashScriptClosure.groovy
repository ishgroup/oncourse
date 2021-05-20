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

package ish.oncourse.server.integration.learndash

import ish.oncourse.API
import ish.oncourse.server.cayenne.Student
import ish.oncourse.server.scripting.ScriptClosure
import ish.oncourse.server.scripting.ScriptClosureTrait
/**
 *
 *
 * Export successful enrolment record to LearnDash system. This action will search for students matching the onCourse
 * email address against the LearnDash username, and creates them if not found.
 * That student will be enrolled in the Learndash course with slug matching the course code in onCourse
 *
 * ```
 *
 * learndash {
 *     action "enrol"
 *     course enrolment.courseClass.course.code
 *     student enrolment.student
 * }
 * ```
 */
@API
@ScriptClosure(key = "learndash", integration = LearnDashIntegration)
class LearnDashScriptClosure implements ScriptClosureTrait<LearnDashIntegration> {

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
    Object execute(LearnDashIntegration integration) {
        String courseId = integration.getCourseId(course)
        if (!courseId) {
            throw new IllegalStateException("Course ${course} is missed from ${integration.baseUrl}")
        }
        if (!student.contact.email) {
            throw new IllegalStateException("Student (contact id: ${student.contact.id}) has no email address")
        }
        String studentId = integration.getUserId(student.contact.email)
        if (!studentId) {
            studentId = integration.createUser(student.contact.email, student.contact.firstName, student.contact.lastName)
        }
        integration.enrol(courseId, studentId)
        return null
    }
}
