/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.persistence;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CommonPreferenceControllerTest {

	@Test
	public void testWrappingQuotationMarksPattern() {
		assertTrue(CommonPreferenceController.WRAPPING_QUOTATION_MARKS.matcher("\"testWrapping\"").matches());
		assertTrue(CommonPreferenceController.WRAPPING_QUOTATION_MARKS.matcher("\"test\"Wrapping\"").matches());
		assertTrue(CommonPreferenceController.WRAPPING_QUOTATION_MARKS.matcher("\"test\"\"Wrapping\"").matches());
		assertTrue(CommonPreferenceController.WRAPPING_QUOTATION_MARKS.matcher("\"\"").matches());

		assertFalse(CommonPreferenceController.WRAPPING_QUOTATION_MARKS.matcher("testWrapping\"").matches());
		assertFalse(CommonPreferenceController.WRAPPING_QUOTATION_MARKS.matcher("\"testWrapping").matches());
		assertFalse(CommonPreferenceController.WRAPPING_QUOTATION_MARKS.matcher("quote\"testWrapping\"").matches());
		assertFalse(CommonPreferenceController.WRAPPING_QUOTATION_MARKS.matcher("\"testWrapping\"quote").matches());
		assertFalse(CommonPreferenceController.WRAPPING_QUOTATION_MARKS.matcher("q\"testWrapping\"q").matches());
		assertFalse(CommonPreferenceController.WRAPPING_QUOTATION_MARKS.matcher("\"\"test").matches());
	}
}
