/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.util;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UsiUtilTest {

	@Test
	public void testValidationUSI() {
		assertFalse(UsiUtil.validateKey("HGDFSGYHIG"));
		assertFalse(UsiUtil.validateKey("5U9HD88TA3"));
		assertFalse(UsiUtil.validateKey("2GF2ugxxv8"));
		assertTrue(UsiUtil.validateKey("2GF2UGXXV8"));
		assertTrue(UsiUtil.validateKey("ZTCP6RPU6M"));
		assertTrue(UsiUtil.validateKey("KLP2ZP6NCW"));
		assertTrue(UsiUtil.validateKey("G2RN4G89SD"));
		assertTrue(UsiUtil.validateKey("PCVA64XU3D"));
		assertTrue(UsiUtil.validateKey("CVPZXK7N9H"));
		assertTrue(UsiUtil.validateKey("QBK9UZYQNJ"));
		assertTrue(UsiUtil.validateKey("W2NBT2FW66"));
		assertTrue(UsiUtil.validateKey("TSXEDK8HVP"));
	}
}
