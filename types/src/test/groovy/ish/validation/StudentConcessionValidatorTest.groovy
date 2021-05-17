/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.validation

import groovy.transform.CompileStatic
import ish.oncourse.cayenne.ConcessionTypeInterface
import ish.oncourse.cayenne.StudentConcessionInterface
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

@CompileStatic
class StudentConcessionValidatorTest {

	@Test
    void testConcessionType() throws Exception {
		StudentConcessionInterface studentConcession = mock(StudentConcessionInterface.class)

        when(studentConcession.getConcessionType()).thenReturn(null)

        Assertions.assertEquals(1, StudentConcessionValidator.valueOf(studentConcession).validate().size())


        StudentConcessionInterface studentConcession1 = mock(StudentConcessionInterface.class)
        ConcessionTypeInterface concessionType1 = mock(ConcessionTypeInterface.class)

        when(studentConcession1.getConcessionType()).thenReturn(concessionType1)

        Assertions.assertEquals(0, StudentConcessionValidator.valueOf(studentConcession1).validate().size())
    }
	
	
	@Test
    void testConcessionNumber() throws Exception {
		StudentConcessionInterface studentConcession = mock(StudentConcessionInterface.class)
        ConcessionTypeInterface concessionType = mock(ConcessionTypeInterface.class)

        when(studentConcession.getConcessionType()).thenReturn(concessionType)
        when(concessionType.getHasConcessionNumber()).thenReturn(true)
        when(studentConcession.getConcessionNumber()).thenReturn("")

        Assertions.assertEquals(1, StudentConcessionValidator.valueOf(studentConcession).validate().size())


        StudentConcessionInterface studentConcession1 = mock(StudentConcessionInterface.class)
        ConcessionTypeInterface concessionType1 = mock(ConcessionTypeInterface.class)

        when(studentConcession1.getConcessionType()).thenReturn(concessionType1)
        when(concessionType1.getHasConcessionNumber()).thenReturn(true)
        when(studentConcession1.getConcessionNumber()).thenReturn("  ")

        Assertions.assertEquals(1, StudentConcessionValidator.valueOf(studentConcession1).validate().size())


        StudentConcessionInterface studentConcession2 = mock(StudentConcessionInterface.class)
        ConcessionTypeInterface concessionType2 = mock(ConcessionTypeInterface.class)

        when(studentConcession2.getConcessionType()).thenReturn(concessionType2)
        when(concessionType2.getHasConcessionNumber()).thenReturn(true)
        when(studentConcession2.getConcessionNumber()).thenReturn(null)

        Assertions.assertEquals(1, StudentConcessionValidator.valueOf(studentConcession2).validate().size())


        StudentConcessionInterface studentConcession3 = mock(StudentConcessionInterface.class)
        ConcessionTypeInterface concessionType3 = mock(ConcessionTypeInterface.class)

        when(studentConcession3.getConcessionType()).thenReturn(concessionType3)
        when(concessionType3.getHasConcessionNumber()).thenReturn(false)
        when(studentConcession3.getConcessionNumber()).thenReturn("")

        Assertions.assertEquals(0, StudentConcessionValidator.valueOf(studentConcession3).validate().size())


        StudentConcessionInterface studentConcession4 = mock(StudentConcessionInterface.class)
        ConcessionTypeInterface concessionType4 = mock(ConcessionTypeInterface.class)

        when(studentConcession4.getConcessionType()).thenReturn(concessionType4)
        when(concessionType4.getHasConcessionNumber()).thenReturn(true)
        when(studentConcession4.getConcessionNumber()).thenReturn("666")

        Assertions.assertEquals(0, StudentConcessionValidator.valueOf(studentConcession4).validate().size())
    }


	@Test
    void testExpiryDate() throws Exception {
		StudentConcessionInterface studentConcession = mock(StudentConcessionInterface.class)
        ConcessionTypeInterface concessionType = mock(ConcessionTypeInterface.class)

        when(studentConcession.getConcessionType()).thenReturn(concessionType)
        when(concessionType.getHasExpiryDate()).thenReturn(true)
        when(studentConcession.getExpiresOn()).thenReturn(null)

        Assertions.assertEquals(1, StudentConcessionValidator.valueOf(studentConcession).validate().size())


        StudentConcessionInterface studentConcession1 = mock(StudentConcessionInterface.class)
        ConcessionTypeInterface concessionType1 = mock(ConcessionTypeInterface.class)

        when(studentConcession1.getConcessionType()).thenReturn(concessionType1)
        when(concessionType1.getHasExpiryDate()).thenReturn(true)
        when(studentConcession1.getExpiresOn()).thenReturn(new Date())

        Assertions.assertEquals(0, StudentConcessionValidator.valueOf(studentConcession1).validate().size())
    }
}
