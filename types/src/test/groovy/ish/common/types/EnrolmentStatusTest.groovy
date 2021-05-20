/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.common.types

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@CompileStatic
class EnrolmentStatusTest {

	@Test
    void testNumberToEnum() {
		// test integer
		EnrolmentStatus ps = TypesUtil.getEnumForDatabaseValue(3, EnrolmentStatus.class)
        Assertions.assertNotNull(ps, "")

        // test bigint
		ps = TypesUtil.getEnumForDatabaseValue(BigInteger.valueOf(3L), EnrolmentStatus.class)
        Assertions.assertNotNull(ps, "")

        // test long
		ps = TypesUtil.getEnumForDatabaseValue(3L, EnrolmentStatus.class)
        Assertions.assertNotNull(ps, "")

        // test string
		ps = TypesUtil.getEnumForDatabaseValue("3", EnrolmentStatus.class)
        Assertions.assertNotNull(ps, "")
    }
}
