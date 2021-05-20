/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.validation

import groovy.transform.CompileStatic
import ish.oncourse.cayenne.StudentInterface
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito

import static org.mockito.Mockito.when

@CompileStatic
class StudentValidatorTest {

    @Test
    void testCorrectStudentYearSchoolCompleted1() throws Exception {
        StudentInterface student = Mockito.mock(StudentInterface.class)
        when(student.getYearSchoolCompleted()).thenReturn(2006)

        Map<String, StudentErrorCode> errorCodeMap = validateStudent(student)

        Assertions.assertEquals(0, errorCodeMap.size())
    }

    @Test
    void testIncorrectStudentYearSchoolCompleted2() throws Exception {
        StudentInterface student = Mockito.mock(StudentInterface.class)
        when(student.getYearSchoolCompleted()).thenReturn(Calendar.getInstance().get(Calendar.YEAR) + 1)

        Map<String, StudentErrorCode> errorCodeMap = validateStudent(student)

        Assertions.assertEquals(1, errorCodeMap.size())
    }

    @Test
    void testIncorrectStudentYearSchoolCompleted3() throws Exception {
        StudentInterface student = Mockito.mock(StudentInterface.class)
        when(student.getYearSchoolCompleted()).thenReturn(Calendar.getInstance().get(Calendar.YEAR) -101)

        Map<String, StudentErrorCode> errorCodeMap = validateStudent(student)

        Assertions.assertEquals(1, errorCodeMap.size())
    }

    private Map<String, StudentErrorCode> validateStudent(StudentInterface student) {

        StudentValidator studentValidator = StudentValidator.valueOf(student)
        Map<String, StudentErrorCode> errorCodeMap = studentValidator.validate()
        return errorCodeMap
    }
}
