/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.persistence;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CommonPreferenceControllerTest {

	@Test
	public void testWrappingQuotationMarksPattern() {
		Assertions.assertTrue(CommonPreferenceController.WRAPPING_QUOTATION_MARKS.matcher("\"testWrapping\"").matches());
		Assertions.assertTrue(CommonPreferenceController.WRAPPING_QUOTATION_MARKS.matcher("\"test\"Wrapping\"").matches());
		Assertions.assertTrue(CommonPreferenceController.WRAPPING_QUOTATION_MARKS.matcher("\"test\"\"Wrapping\"").matches());
		Assertions.assertTrue(CommonPreferenceController.WRAPPING_QUOTATION_MARKS.matcher("\"\"").matches());

		Assertions.assertFalse(CommonPreferenceController.WRAPPING_QUOTATION_MARKS.matcher("testWrapping\"").matches());
		Assertions.assertFalse(CommonPreferenceController.WRAPPING_QUOTATION_MARKS.matcher("\"testWrapping").matches());
		Assertions.assertFalse(CommonPreferenceController.WRAPPING_QUOTATION_MARKS.matcher("quote\"testWrapping\"").matches());
		Assertions.assertFalse(CommonPreferenceController.WRAPPING_QUOTATION_MARKS.matcher("\"testWrapping\"quote").matches());
		Assertions.assertFalse(CommonPreferenceController.WRAPPING_QUOTATION_MARKS.matcher("q\"testWrapping\"q").matches());
		Assertions.assertFalse(CommonPreferenceController.WRAPPING_QUOTATION_MARKS.matcher("\"\"test").matches());
	}
}
