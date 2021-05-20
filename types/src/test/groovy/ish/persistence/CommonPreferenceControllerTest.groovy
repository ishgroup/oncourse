/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */
package ish.persistence

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@CompileStatic
class CommonPreferenceControllerTest {

	@Test
    void testWrappingQuotationMarksPattern() {
		Assertions.assertTrue(CommonPreferenceController.WRAPPING_QUOTATION_MARKS.matcher("\"testWrapping\"").matches())
        Assertions.assertTrue(CommonPreferenceController.WRAPPING_QUOTATION_MARKS.matcher("\"test\"Wrapping\"").matches())
        Assertions.assertTrue(CommonPreferenceController.WRAPPING_QUOTATION_MARKS.matcher("\"test\"\"Wrapping\"").matches())
        Assertions.assertTrue(CommonPreferenceController.WRAPPING_QUOTATION_MARKS.matcher("\"\"").matches())

        Assertions.assertFalse(CommonPreferenceController.WRAPPING_QUOTATION_MARKS.matcher("testWrapping\"").matches())
        Assertions.assertFalse(CommonPreferenceController.WRAPPING_QUOTATION_MARKS.matcher("\"testWrapping").matches())
        Assertions.assertFalse(CommonPreferenceController.WRAPPING_QUOTATION_MARKS.matcher("quote\"testWrapping\"").matches())
        Assertions.assertFalse(CommonPreferenceController.WRAPPING_QUOTATION_MARKS.matcher("\"testWrapping\"quote").matches())
        Assertions.assertFalse(CommonPreferenceController.WRAPPING_QUOTATION_MARKS.matcher("q\"testWrapping\"q").matches())
        Assertions.assertFalse(CommonPreferenceController.WRAPPING_QUOTATION_MARKS.matcher("\"\"test").matches())
    }
}
