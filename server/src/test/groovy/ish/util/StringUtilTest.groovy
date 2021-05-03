/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.util

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@CompileStatic
class StringUtilTest {

    @Test
    void testPadFieldStringInput() {

        Assertions.assertEquals("", StringUtil.padField(0))
        Assertions.assertEquals("     ", StringUtil.padField(5))

        Assertions.assertEquals("", StringUtil.padField(0, "Lorem"))
        Assertions.assertEquals("L", StringUtil.padField(1, "Lorem"))
        Assertions.assertEquals("Lorem", StringUtil.padField(5, "Lorem"))
        Assertions.assertEquals("Lorem     ", StringUtil.padField(10, "Lorem"))

        Assertions.assertEquals("", StringUtil.padFieldLeft(0, "Lorem"))
        Assertions.assertEquals("L", StringUtil.padFieldLeft(1, "Lorem"))
        Assertions.assertEquals("Lorem", StringUtil.padFieldLeft(5, "Lorem"))
        Assertions.assertEquals("Lorem     ", StringUtil.padFieldLeft(10, "Lorem"))

        Assertions.assertEquals("", StringUtil.padFieldRight(0, "Lorem"))
        Assertions.assertEquals("L", StringUtil.padFieldRight(1, "Lorem"))
        Assertions.assertEquals("Lorem", StringUtil.padFieldRight(5, "Lorem"))
        Assertions.assertEquals("     Lorem", StringUtil.padFieldRight(10, "Lorem"))

        Assertions.assertEquals("", StringUtil.padField(0, "Lorem", '+' as char))
        Assertions.assertEquals("L", StringUtil.padField(1, "Lorem", '+' as char))
        Assertions.assertEquals("Lorem", StringUtil.padField(5, "Lorem", '+' as char))
        Assertions.assertEquals("Lorem+++++", StringUtil.padField(10, "Lorem", '+' as char))

        Assertions.assertEquals("", StringUtil.padFieldLeft(0, "Lorem", '+' as char))
        Assertions.assertEquals("L", StringUtil.padFieldLeft(1, "Lorem", '+' as char))
        Assertions.assertEquals("Lorem", StringUtil.padFieldLeft(5, "Lorem", '+' as char))
        Assertions.assertEquals("Lorem+++++", StringUtil.padFieldLeft(10, "Lorem", '+' as char))

        Assertions.assertEquals("", StringUtil.padFieldRight(0, "Lorem", '+' as char))
        Assertions.assertEquals("L", StringUtil.padFieldRight(1, "Lorem", '+' as char))
        Assertions.assertEquals("Lorem", StringUtil.padFieldRight(5, "Lorem", '+' as char))
        Assertions.assertEquals("+++++Lorem", StringUtil.padFieldRight(10, "Lorem", '+' as char))

        Assertions.assertEquals("", StringUtil.padField(0, "Lorem", '+' as char, false))
        Assertions.assertEquals("L", StringUtil.padField(1, "Lorem", '+' as char, false))
        Assertions.assertEquals("Lorem", StringUtil.padField(5, "Lorem", '+' as char, false))
        Assertions.assertEquals("Lorem+++++", StringUtil.padField(10, "Lorem", '+' as char, false))

        Assertions.assertEquals("", StringUtil.padField(0, "Lorem", '+' as char, true))
        Assertions.assertEquals("L", StringUtil.padField(1, "Lorem", '+' as char, true))
        Assertions.assertEquals("Lorem", StringUtil.padField(5, "Lorem", '+' as char, true))
        Assertions.assertEquals("+++++Lorem", StringUtil.padField(10, "Lorem", '+' as char, true))
    }

    @Test
    void testPadFieldBooleanInput() {

        Assertions.assertEquals("", StringUtil.padField(0, Boolean.FALSE))
        Assertions.assertEquals("f", StringUtil.padField(1, Boolean.FALSE))
        Assertions.assertEquals("false", StringUtil.padField(5, Boolean.FALSE))
        Assertions.assertEquals("false     ", StringUtil.padField(10, Boolean.FALSE))

        Assertions.assertEquals("", StringUtil.padFieldLeft(0, Boolean.FALSE))
        Assertions.assertEquals("f", StringUtil.padFieldLeft(1, Boolean.FALSE))
        Assertions.assertEquals("false", StringUtil.padFieldLeft(5, Boolean.FALSE))
        Assertions.assertEquals("false     ", StringUtil.padFieldLeft(10, Boolean.FALSE))

        Assertions.assertEquals("", StringUtil.padFieldRight(0, Boolean.FALSE))
        Assertions.assertEquals("f", StringUtil.padFieldRight(1, Boolean.FALSE))
        Assertions.assertEquals("false", StringUtil.padFieldRight(5, Boolean.FALSE))
        Assertions.assertEquals("     false", StringUtil.padFieldRight(10, Boolean.FALSE))

        Assertions.assertEquals("", StringUtil.padField(0, Boolean.FALSE, '+' as char))
        Assertions.assertEquals("f", StringUtil.padField(1, Boolean.FALSE, '+' as char))
        Assertions.assertEquals("false", StringUtil.padField(5, Boolean.FALSE, '+' as char))
        Assertions.assertEquals("false+++++", StringUtil.padField(10, Boolean.FALSE, '+' as char))

        Assertions.assertEquals("", StringUtil.padFieldLeft(0, Boolean.FALSE, '+' as char))
        Assertions.assertEquals("f", StringUtil.padFieldLeft(1, Boolean.FALSE, '+' as char))
        Assertions.assertEquals("false", StringUtil.padFieldLeft(5, Boolean.FALSE, '+' as char))
        Assertions.assertEquals("false+++++", StringUtil.padFieldLeft(10, Boolean.FALSE, '+' as char))

        Assertions.assertEquals("", StringUtil.padFieldRight(0, Boolean.FALSE, '+' as char))
        Assertions.assertEquals("f", StringUtil.padFieldRight(1, Boolean.FALSE, '+' as char))
        Assertions.assertEquals("false", StringUtil.padFieldRight(5, Boolean.FALSE, '+' as char))
        Assertions.assertEquals("+++++false", StringUtil.padFieldRight(10, Boolean.FALSE, '+' as char))

    }

    @Test
    void testPadFieldIntegerInput() {

        Assertions.assertEquals("", StringUtil.padField(0, 12345))
        Assertions.assertEquals("1", StringUtil.padField(1, 12345))
        Assertions.assertEquals("12345", StringUtil.padField(5, 12345))
        Assertions.assertEquals("0000012345", StringUtil.padField(10, 12345))

        Assertions.assertEquals("", StringUtil.padFieldLeft(0, 12345))
        Assertions.assertEquals("1", StringUtil.padFieldLeft(1, 12345))
        Assertions.assertEquals("12345", StringUtil.padFieldLeft(5, 12345))
        Assertions.assertEquals("1234500000", StringUtil.padFieldLeft(10, 12345))

        Assertions.assertEquals("", StringUtil.padFieldRight(0, 12345))
        Assertions.assertEquals("1", StringUtil.padFieldRight(1, 12345))
        Assertions.assertEquals("12345", StringUtil.padFieldRight(5, 12345))
        Assertions.assertEquals("0000012345", StringUtil.padFieldRight(10, 12345))

        Assertions.assertEquals("", StringUtil.padField(0, 12345, '+' as char))
        Assertions.assertEquals("1", StringUtil.padField(1, 12345, '+' as char))
        Assertions.assertEquals("12345", StringUtil.padField(5, 12345, '+' as char))
        Assertions.assertEquals("+++++12345", StringUtil.padField(10, 12345, '+' as char))

        Assertions.assertEquals("", StringUtil.padFieldLeft(0, 12345, '+' as char))
        Assertions.assertEquals("1", StringUtil.padFieldLeft(1, 12345, '+' as char))
        Assertions.assertEquals("12345", StringUtil.padFieldLeft(5, 12345, '+' as char))
        Assertions.assertEquals("12345+++++", StringUtil.padFieldLeft(10, 12345, '+' as char))

        Assertions.assertEquals("", StringUtil.padFieldRight(0, 12345, '+' as char))
        Assertions.assertEquals("1", StringUtil.padFieldRight(1, 12345, '+' as char))
        Assertions.assertEquals("12345", StringUtil.padFieldRight(5, 12345, '+' as char))
        Assertions.assertEquals("+++++12345", StringUtil.padFieldRight(10, 12345, '+' as char))

        Assertions.assertEquals("", StringUtil.padField(0, 12345, '+' as char, false))
        Assertions.assertEquals("1", StringUtil.padField(1, 12345, '+' as char, false))
        Assertions.assertEquals("12345", StringUtil.padField(5, 12345, '+' as char, false))
        Assertions.assertEquals("12345+++++", StringUtil.padField(10, 12345, '+' as char, false))

        Assertions.assertEquals("", StringUtil.padField(0, 12345, '+' as char, true))
        Assertions.assertEquals("1", StringUtil.padField(1, 12345, '+' as char, true))
        Assertions.assertEquals("12345", StringUtil.padField(5, 12345, '+' as char, true))
        Assertions.assertEquals("+++++12345", StringUtil.padField(10, 12345, '+' as char, true))

    }

    @Test
    void testPadFieldIntInput() {

        Assertions.assertEquals("", StringUtil.padField(0, 12345))
        Assertions.assertEquals("1", StringUtil.padField(1, 12345))
        Assertions.assertEquals("12345", StringUtil.padField(5, 12345))
        Assertions.assertEquals("0000012345", StringUtil.padField(10, 12345))

        Assertions.assertEquals("", StringUtil.padFieldLeft(0, 12345))
        Assertions.assertEquals("1", StringUtil.padFieldLeft(1, 12345))
        Assertions.assertEquals("12345", StringUtil.padFieldLeft(5, 12345))
        Assertions.assertEquals("1234500000", StringUtil.padFieldLeft(10, 12345))

        Assertions.assertEquals("", StringUtil.padFieldRight(0, 12345))
        Assertions.assertEquals("1", StringUtil.padFieldRight(1, 12345))
        Assertions.assertEquals("12345", StringUtil.padFieldRight(5, 12345))
        Assertions.assertEquals("0000012345", StringUtil.padFieldRight(10, 12345))

        Assertions.assertEquals("", StringUtil.padField(0, 12345, '+' as char))
        Assertions.assertEquals("1", StringUtil.padField(1, 12345, '+' as char))
        Assertions.assertEquals("12345", StringUtil.padField(5, 12345, '+' as char))
        Assertions.assertEquals("+++++12345", StringUtil.padField(10, 12345, '+' as char))

        Assertions.assertEquals("", StringUtil.padFieldLeft(0, 12345, '+' as char))
        Assertions.assertEquals("1", StringUtil.padFieldLeft(1, 12345, '+' as char))
        Assertions.assertEquals("12345", StringUtil.padFieldLeft(5, 12345, '+' as char))
        Assertions.assertEquals("12345+++++", StringUtil.padFieldLeft(10, 12345, '+' as char))

        Assertions.assertEquals("", StringUtil.padFieldRight(0, 12345, '+' as char))
        Assertions.assertEquals("1", StringUtil.padFieldRight(1, 12345, '+' as char))
        Assertions.assertEquals("12345", StringUtil.padFieldRight(5, 12345, '+' as char))
        Assertions.assertEquals("+++++12345", StringUtil.padFieldRight(10, 12345, '+' as char))

        Assertions.assertEquals("", StringUtil.padField(0, 12345, '+' as char, false))
        Assertions.assertEquals("1", StringUtil.padField(1, 12345, '+' as char, false))
        Assertions.assertEquals("12345", StringUtil.padField(5, 12345, '+' as char, false))
        Assertions.assertEquals("12345+++++", StringUtil.padField(10, 12345, '+' as char, false))

        Assertions.assertEquals("", StringUtil.padField(0, 12345, '+' as char, true))
        Assertions.assertEquals("1", StringUtil.padField(1, 12345, '+' as char, true))
        Assertions.assertEquals("12345", StringUtil.padField(5, 12345, '+' as char, true))
        Assertions.assertEquals("+++++12345", StringUtil.padField(10, 12345, '+' as char, true))
    }

    @Test
    void testPadFieldDoubleInput() {

        Assertions.assertEquals("", StringUtil.padField(0, Double.valueOf("12.34")))
        Assertions.assertEquals("1", StringUtil.padField(1, Double.valueOf("12.34")))
        Assertions.assertEquals("12.34", StringUtil.padField(5, Double.valueOf("12.34")))
        Assertions.assertEquals("0000012.34", StringUtil.padField(10, Double.valueOf("12.34")))

        Assertions.assertEquals("", StringUtil.padFieldLeft(0, Double.valueOf("12.34")))
        Assertions.assertEquals("1", StringUtil.padFieldLeft(1, Double.valueOf("12.34")))
        Assertions.assertEquals("12.34", StringUtil.padFieldLeft(5, Double.valueOf("12.34")))
        Assertions.assertEquals("12.3400000", StringUtil.padFieldLeft(10, Double.valueOf("12.34")))

        Assertions.assertEquals("", StringUtil.padFieldRight(0, Double.valueOf("12.34")))
        Assertions.assertEquals("1", StringUtil.padFieldRight(1, Double.valueOf("12.34")))
        Assertions.assertEquals("12.34", StringUtil.padFieldRight(5, Double.valueOf("12.34")))
        Assertions.assertEquals("0000012.34", StringUtil.padFieldRight(10, Double.valueOf("12.34")))

        Assertions.assertEquals("", StringUtil.padField(0, Double.valueOf("12.34"), '+' as char))
        Assertions.assertEquals("1", StringUtil.padField(1, Double.valueOf("12.34"), '+' as char))
        Assertions.assertEquals("12.34", StringUtil.padField(5, Double.valueOf("12.34"), '+' as char))
        Assertions.assertEquals("+++++12.34", StringUtil.padField(10, Double.valueOf("12.34"), '+' as char))

        Assertions.assertEquals("", StringUtil.padFieldLeft(0, Double.valueOf("12.34"), '+' as char))
        Assertions.assertEquals("1", StringUtil.padFieldLeft(1, Double.valueOf("12.34"), '+' as char))
        Assertions.assertEquals("12.34", StringUtil.padFieldLeft(5, Double.valueOf("12.34"), '+' as char))
        Assertions.assertEquals("12.34+++++", StringUtil.padFieldLeft(10, Double.valueOf("12.34"), '+' as char))

        Assertions.assertEquals("", StringUtil.padFieldRight(0, Double.valueOf("12.34"), '+' as char))
        Assertions.assertEquals("1", StringUtil.padFieldRight(1, Double.valueOf("12.34"), '+' as char))
        Assertions.assertEquals("12.34", StringUtil.padFieldRight(5, Double.valueOf("12.34"), '+' as char))
        Assertions.assertEquals("+++++12.34", StringUtil.padFieldRight(10, Double.valueOf("12.34"), '+' as char))

        Assertions.assertEquals("", StringUtil.padField(0, Double.valueOf("12.34"), '+' as char, false))
        Assertions.assertEquals("1", StringUtil.padField(1, Double.valueOf("12.34"), '+' as char, false))
        Assertions.assertEquals("12.34", StringUtil.padField(5, Double.valueOf("12.34"), '+' as char, false))
        Assertions.assertEquals("12.34+++++", StringUtil.padField(10, Double.valueOf("12.34"), '+' as char, false))

        Assertions.assertEquals("", StringUtil.padField(0, Double.valueOf("12.34"), '+' as char, true))
        Assertions.assertEquals("1", StringUtil.padField(1, Double.valueOf("12.34"), '+' as char, true))
        Assertions.assertEquals("12.34", StringUtil.padField(5, Double.valueOf("12.34"), '+' as char, true))
        Assertions.assertEquals("+++++12.34", StringUtil.padField(10, Double.valueOf("12.34"), '+' as char, true))

    }
}
