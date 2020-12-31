/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.util

import ish.IshTestCase
import org.junit.Test
import static org.junit.Assert.*

class StringUtilTest  {

	@Test
    void testPadFieldStringInput() {

		assertEquals("", StringUtil.padField(0))
        assertEquals("     ", StringUtil.padField(5))

        assertEquals("", StringUtil.padField(0, "Lorem"))
        assertEquals("L", StringUtil.padField(1, "Lorem"))
        assertEquals("Lorem", StringUtil.padField(5, "Lorem"))
        assertEquals("Lorem     ", StringUtil.padField(10, "Lorem"))

        assertEquals("", StringUtil.padFieldLeft(0, "Lorem"))
        assertEquals("L", StringUtil.padFieldLeft(1, "Lorem"))
        assertEquals("Lorem", StringUtil.padFieldLeft(5, "Lorem"))
        assertEquals("Lorem     ", StringUtil.padFieldLeft(10, "Lorem"))

        assertEquals("", StringUtil.padFieldRight(0, "Lorem"))
        assertEquals("L", StringUtil.padFieldRight(1, "Lorem"))
        assertEquals("Lorem", StringUtil.padFieldRight(5, "Lorem"))
        assertEquals("     Lorem", StringUtil.padFieldRight(10, "Lorem"))

        assertEquals("", StringUtil.padField(0, "Lorem", '+' as char))
        assertEquals("L", StringUtil.padField(1, "Lorem", '+' as char))
        assertEquals("Lorem", StringUtil.padField(5, "Lorem", '+' as char))
        assertEquals("Lorem+++++", StringUtil.padField(10, "Lorem", '+' as char))

        assertEquals("", StringUtil.padFieldLeft(0, "Lorem", '+' as char))
        assertEquals("L", StringUtil.padFieldLeft(1, "Lorem", '+' as char))
        assertEquals("Lorem", StringUtil.padFieldLeft(5, "Lorem", '+' as char))
        assertEquals("Lorem+++++", StringUtil.padFieldLeft(10, "Lorem", '+' as char))

        assertEquals("", StringUtil.padFieldRight(0, "Lorem", '+' as char))
        assertEquals("L", StringUtil.padFieldRight(1, "Lorem", '+' as char))
        assertEquals("Lorem", StringUtil.padFieldRight(5, "Lorem", '+' as char))
        assertEquals("+++++Lorem", StringUtil.padFieldRight(10, "Lorem", '+' as char))

        assertEquals("", StringUtil.padField(0, "Lorem", '+' as char, false))
        assertEquals("L", StringUtil.padField(1, "Lorem", '+' as char, false))
        assertEquals("Lorem", StringUtil.padField(5, "Lorem", '+' as char, false))
        assertEquals("Lorem+++++", StringUtil.padField(10, "Lorem", '+' as char, false))

        assertEquals("", StringUtil.padField(0, "Lorem", '+' as char, true))
        assertEquals("L", StringUtil.padField(1, "Lorem", '+' as char, true))
        assertEquals("Lorem", StringUtil.padField(5, "Lorem", '+' as char, true))
        assertEquals("+++++Lorem", StringUtil.padField(10, "Lorem", '+' as char, true))
    }

	@Test
    void testPadFieldBooleanInput() {

		assertEquals("", StringUtil.padField(0, Boolean.FALSE))
        assertEquals("f", StringUtil.padField(1, Boolean.FALSE))
        assertEquals("false", StringUtil.padField(5, Boolean.FALSE))
        assertEquals("false     ", StringUtil.padField(10, Boolean.FALSE))

        assertEquals("", StringUtil.padFieldLeft(0, Boolean.FALSE))
        assertEquals("f", StringUtil.padFieldLeft(1, Boolean.FALSE))
        assertEquals("false", StringUtil.padFieldLeft(5, Boolean.FALSE))
        assertEquals("false     ", StringUtil.padFieldLeft(10, Boolean.FALSE))

        assertEquals("", StringUtil.padFieldRight(0, Boolean.FALSE))
        assertEquals("f", StringUtil.padFieldRight(1, Boolean.FALSE))
        assertEquals("false", StringUtil.padFieldRight(5, Boolean.FALSE))
        assertEquals("     false", StringUtil.padFieldRight(10, Boolean.FALSE))

        assertEquals("", StringUtil.padField(0, Boolean.FALSE, '+' as char))
        assertEquals("f", StringUtil.padField(1, Boolean.FALSE, '+' as char))
        assertEquals("false", StringUtil.padField(5, Boolean.FALSE, '+' as char))
        assertEquals("false+++++", StringUtil.padField(10, Boolean.FALSE, '+' as char))

        assertEquals("", StringUtil.padFieldLeft(0, Boolean.FALSE, '+' as char))
        assertEquals("f", StringUtil.padFieldLeft(1, Boolean.FALSE, '+' as char))
        assertEquals("false", StringUtil.padFieldLeft(5, Boolean.FALSE, '+' as char))
        assertEquals("false+++++", StringUtil.padFieldLeft(10, Boolean.FALSE, '+' as char))

        assertEquals("", StringUtil.padFieldRight(0, Boolean.FALSE, '+' as char))
        assertEquals("f", StringUtil.padFieldRight(1, Boolean.FALSE, '+' as char))
        assertEquals("false", StringUtil.padFieldRight(5, Boolean.FALSE, '+' as char))
        assertEquals("+++++false", StringUtil.padFieldRight(10, Boolean.FALSE, '+' as char))

    }

	@Test
    void testPadFieldIntegerInput() {

		assertEquals("", StringUtil.padField(0, 12345))
        assertEquals("1", StringUtil.padField(1, 12345))
        assertEquals("12345", StringUtil.padField(5, 12345))
        assertEquals("0000012345", StringUtil.padField(10, 12345))

        assertEquals("", StringUtil.padFieldLeft(0, 12345))
        assertEquals("1", StringUtil.padFieldLeft(1, 12345))
        assertEquals("12345", StringUtil.padFieldLeft(5, 12345))
        assertEquals("1234500000", StringUtil.padFieldLeft(10, 12345))

        assertEquals("", StringUtil.padFieldRight(0, 12345))
        assertEquals("1", StringUtil.padFieldRight(1, 12345))
        assertEquals("12345", StringUtil.padFieldRight(5, 12345))
        assertEquals("0000012345", StringUtil.padFieldRight(10, 12345))

        assertEquals("", StringUtil.padField(0, 12345, '+' as char))
        assertEquals("1", StringUtil.padField(1, 12345, '+' as char))
        assertEquals("12345", StringUtil.padField(5, 12345, '+' as char))
        assertEquals("+++++12345", StringUtil.padField(10, 12345, '+' as char))

        assertEquals("", StringUtil.padFieldLeft(0, 12345, '+' as char))
        assertEquals("1", StringUtil.padFieldLeft(1, 12345, '+' as char))
        assertEquals("12345", StringUtil.padFieldLeft(5, 12345, '+' as char))
        assertEquals("12345+++++", StringUtil.padFieldLeft(10, 12345, '+' as char))

        assertEquals("", StringUtil.padFieldRight(0, 12345, '+' as char))
        assertEquals("1", StringUtil.padFieldRight(1, 12345, '+' as char))
        assertEquals("12345", StringUtil.padFieldRight(5, 12345, '+' as char))
        assertEquals("+++++12345", StringUtil.padFieldRight(10, 12345, '+' as char))

        assertEquals("", StringUtil.padField(0, 12345, '+' as char, false))
        assertEquals("1", StringUtil.padField(1, 12345, '+' as char, false))
        assertEquals("12345", StringUtil.padField(5, 12345, '+' as char, false))
        assertEquals("12345+++++", StringUtil.padField(10, 12345, '+' as char, false))

        assertEquals("", StringUtil.padField(0, 12345, '+' as char, true))
        assertEquals("1", StringUtil.padField(1, 12345, '+' as char, true))
        assertEquals("12345", StringUtil.padField(5, 12345, '+' as char, true))
        assertEquals("+++++12345", StringUtil.padField(10, 12345, '+' as char, true))

    }

	@Test
    void testPadFieldIntInput() {

		assertEquals("", StringUtil.padField(0, 12345))
        assertEquals("1", StringUtil.padField(1, 12345))
        assertEquals("12345", StringUtil.padField(5, 12345))
        assertEquals("0000012345", StringUtil.padField(10, 12345))

        assertEquals("", StringUtil.padFieldLeft(0, 12345))
        assertEquals("1", StringUtil.padFieldLeft(1, 12345))
        assertEquals("12345", StringUtil.padFieldLeft(5, 12345))
        assertEquals("1234500000", StringUtil.padFieldLeft(10, 12345))

        assertEquals("", StringUtil.padFieldRight(0, 12345))
        assertEquals("1", StringUtil.padFieldRight(1, 12345))
        assertEquals("12345", StringUtil.padFieldRight(5, 12345))
        assertEquals("0000012345", StringUtil.padFieldRight(10, 12345))

        assertEquals("", StringUtil.padField(0, 12345, '+' as char))
        assertEquals("1", StringUtil.padField(1, 12345, '+' as char))
        assertEquals("12345", StringUtil.padField(5, 12345, '+' as char))
        assertEquals("+++++12345", StringUtil.padField(10, 12345, '+' as char))

        assertEquals("", StringUtil.padFieldLeft(0, 12345, '+' as char))
        assertEquals("1", StringUtil.padFieldLeft(1, 12345, '+' as char))
        assertEquals("12345", StringUtil.padFieldLeft(5, 12345, '+' as char))
        assertEquals("12345+++++", StringUtil.padFieldLeft(10, 12345, '+' as char))

        assertEquals("", StringUtil.padFieldRight(0, 12345, '+' as char))
        assertEquals("1", StringUtil.padFieldRight(1, 12345, '+' as char))
        assertEquals("12345", StringUtil.padFieldRight(5, 12345, '+' as char))
        assertEquals("+++++12345", StringUtil.padFieldRight(10, 12345, '+' as char))

        assertEquals("", StringUtil.padField(0, 12345, '+' as char, false))
        assertEquals("1", StringUtil.padField(1, 12345, '+' as char, false))
        assertEquals("12345", StringUtil.padField(5, 12345, '+' as char, false))
        assertEquals("12345+++++", StringUtil.padField(10, 12345, '+' as char, false))

        assertEquals("", StringUtil.padField(0, 12345, '+' as char, true))
        assertEquals("1", StringUtil.padField(1, 12345, '+' as char, true))
        assertEquals("12345", StringUtil.padField(5, 12345, '+' as char, true))
        assertEquals("+++++12345", StringUtil.padField(10, 12345, '+' as char, true))
    }

	@Test
    void testPadFieldDoubleInput() {

		assertEquals("", StringUtil.padField(0, Double.valueOf("12.34")))
        assertEquals("1", StringUtil.padField(1, Double.valueOf("12.34")))
        assertEquals("12.34", StringUtil.padField(5, Double.valueOf("12.34")))
        assertEquals("0000012.34", StringUtil.padField(10, Double.valueOf("12.34")))

        assertEquals("", StringUtil.padFieldLeft(0, Double.valueOf("12.34")))
        assertEquals("1", StringUtil.padFieldLeft(1, Double.valueOf("12.34")))
        assertEquals("12.34", StringUtil.padFieldLeft(5, Double.valueOf("12.34")))
        assertEquals("12.3400000", StringUtil.padFieldLeft(10, Double.valueOf("12.34")))

        assertEquals("", StringUtil.padFieldRight(0, Double.valueOf("12.34")))
        assertEquals("1", StringUtil.padFieldRight(1, Double.valueOf("12.34")))
        assertEquals("12.34", StringUtil.padFieldRight(5, Double.valueOf("12.34")))
        assertEquals("0000012.34", StringUtil.padFieldRight(10, Double.valueOf("12.34")))

        assertEquals("", StringUtil.padField(0, Double.valueOf("12.34"), '+' as char))
        assertEquals("1", StringUtil.padField(1, Double.valueOf("12.34"), '+' as char))
        assertEquals("12.34", StringUtil.padField(5, Double.valueOf("12.34"), '+' as char))
        assertEquals("+++++12.34", StringUtil.padField(10, Double.valueOf("12.34"), '+' as char))

        assertEquals("", StringUtil.padFieldLeft(0, Double.valueOf("12.34"), '+' as char))
        assertEquals("1", StringUtil.padFieldLeft(1, Double.valueOf("12.34"), '+' as char))
        assertEquals("12.34", StringUtil.padFieldLeft(5, Double.valueOf("12.34"), '+' as char))
        assertEquals("12.34+++++", StringUtil.padFieldLeft(10, Double.valueOf("12.34"), '+' as char))

        assertEquals("", StringUtil.padFieldRight(0, Double.valueOf("12.34"), '+' as char))
        assertEquals("1", StringUtil.padFieldRight(1, Double.valueOf("12.34"), '+' as char))
        assertEquals("12.34", StringUtil.padFieldRight(5, Double.valueOf("12.34"), '+' as char))
        assertEquals("+++++12.34", StringUtil.padFieldRight(10, Double.valueOf("12.34"), '+' as char))

        assertEquals("", StringUtil.padField(0, Double.valueOf("12.34"), '+' as char, false))
        assertEquals("1", StringUtil.padField(1, Double.valueOf("12.34"), '+' as char, false))
        assertEquals("12.34", StringUtil.padField(5, Double.valueOf("12.34"), '+' as char, false))
        assertEquals("12.34+++++", StringUtil.padField(10, Double.valueOf("12.34"), '+' as char, false))

        assertEquals("", StringUtil.padField(0, Double.valueOf("12.34"), '+' as char, true))
        assertEquals("1", StringUtil.padField(1, Double.valueOf("12.34"), '+' as char, true))
        assertEquals("12.34", StringUtil.padField(5, Double.valueOf("12.34"), '+' as char, true))
        assertEquals("+++++12.34", StringUtil.padField(10, Double.valueOf("12.34"), '+' as char, true))

    }
}
