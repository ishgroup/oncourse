/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;


public class UsiUtilTest {

	@Test
	public void testValidationUSI() {
		Assertions.assertFalse(UsiUtil.validateKey("HGDFSGYHIG"));
		Assertions.assertFalse(UsiUtil.validateKey("5U9HD88TA3"));
		Assertions.assertFalse(UsiUtil.validateKey("2GF2ugxxv8"));
		Assertions.assertTrue(UsiUtil.validateKey("2GF2UGXXV8"));
		Assertions.assertTrue(UsiUtil.validateKey("ZTCP6RPU6M"));
		Assertions.assertTrue(UsiUtil.validateKey("KLP2ZP6NCW"));
		Assertions.assertTrue(UsiUtil.validateKey("G2RN4G89SD"));
		Assertions.assertTrue(UsiUtil.validateKey("PCVA64XU3D"));
		Assertions.assertTrue(UsiUtil.validateKey("CVPZXK7N9H"));
		Assertions.assertTrue(UsiUtil.validateKey("QBK9UZYQNJ"));
		Assertions.assertTrue(UsiUtil.validateKey("W2NBT2FW66"));
		Assertions.assertTrue(UsiUtil.validateKey("TSXEDK8HVP"));
	}
}
