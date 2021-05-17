/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */
package ish.util

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test


@CompileStatic
class UsiUtilTest {

	@Test
	void testValidationUSI() {
		Assertions.assertFalse(UsiUtil.validateKey("HGDFSGYHIG"))
		Assertions.assertFalse(UsiUtil.validateKey("5U9HD88TA3"))
		Assertions.assertFalse(UsiUtil.validateKey("2GF2ugxxv8"))
		Assertions.assertTrue(UsiUtil.validateKey("2GF2UGXXV8"))
		Assertions.assertTrue(UsiUtil.validateKey("ZTCP6RPU6M"))
		Assertions.assertTrue(UsiUtil.validateKey("KLP2ZP6NCW"))
		Assertions.assertTrue(UsiUtil.validateKey("G2RN4G89SD"))
		Assertions.assertTrue(UsiUtil.validateKey("PCVA64XU3D"))
		Assertions.assertTrue(UsiUtil.validateKey("CVPZXK7N9H"))
		Assertions.assertTrue(UsiUtil.validateKey("QBK9UZYQNJ"))
		Assertions.assertTrue(UsiUtil.validateKey("W2NBT2FW66"))
		Assertions.assertTrue(UsiUtil.validateKey("TSXEDK8HVP"))
	}
}
