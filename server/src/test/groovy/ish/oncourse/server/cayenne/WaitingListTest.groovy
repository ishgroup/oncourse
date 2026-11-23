/*
 * Copyright ish group pty ltd 2026.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.cayenne

import groovy.transform.CompileStatic
import ish.DatabaseSetup
import ish.TestWithDatabase
import ish.oncourse.server.lifecycle.SampleEntityBuilder
import org.apache.commons.lang3.exception.ExceptionUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable

@CompileStatic
@DatabaseSetup
class WaitingListTest extends TestWithDatabase {

    /**
     * A waiting list can arrive together with the student it belongs to, e.g. replicated from willow
     * in one transaction group. The duplicate check must not query with the unsaved student.
     */
    @Test
    void testCreateWithNewStudentAndCourse() {
        SampleEntityBuilder builder = SampleEntityBuilder.newBuilder(cayenneContext)
        Course course = builder.createCourse()
        Student student = builder.createStudent(builder.createContact())
        WaitingList waitingList = createWaitingList(course, student)

        cayenneContext.commitChanges()

        Assertions.assertFalse(waitingList.isNewRecord())
    }

    @Test
    void testDuplicateIsRejected() {
        SampleEntityBuilder builder = SampleEntityBuilder.newBuilder(cayenneContext)
        Course course = builder.createCourse()
        Student student = builder.createStudent(builder.createContact())
        createWaitingList(course, student)
        cayenneContext.commitChanges()

        createWaitingList(course, student)

        Throwable error = Assertions.assertThrows(Exception.class, { cayenneContext.commitChanges() } as Executable)
        Assertions.assertTrue(ExceptionUtils.getRootCause(error).getMessage().contains("already exists"),
                "Expected the duplicate waiting list error, got: " + ExceptionUtils.getRootCause(error))
    }

    private WaitingList createWaitingList(Course course, Student student) {
        WaitingList waitingList = cayenneContext.newObject(WaitingList.class)
        waitingList.setCourse(course)
        waitingList.setStudent(student)
        waitingList.setStudentCount(1)
        return waitingList
    }
}
