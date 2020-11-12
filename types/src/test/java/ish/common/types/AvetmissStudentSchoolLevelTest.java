package ish.common.types;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AvetmissStudentSchoolLevelTest {

	@Test
	public void testIsValid() {
		assertTrue(AvetmissStudentSchoolLevel.isValid(null, 9));
		assertTrue(AvetmissStudentSchoolLevel.isValid(null, 10));
		assertTrue(AvetmissStudentSchoolLevel.isValid(null, 11));
		assertTrue(AvetmissStudentSchoolLevel.isValid(null, 12));

		assertTrue(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.DEFAULT_POPUP_OPTION, 9));
		assertTrue(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.DEFAULT_POPUP_OPTION, 10));
		assertTrue(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.DEFAULT_POPUP_OPTION, 11));
		assertTrue(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.DEFAULT_POPUP_OPTION, 12));

		assertTrue(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.DID_NOT_GO_TO_SCHOOL, 9));
		assertTrue(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.DID_NOT_GO_TO_SCHOOL, 10));
		assertTrue(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.DID_NOT_GO_TO_SCHOOL, 11));
		assertTrue(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.DID_NOT_GO_TO_SCHOOL, 12));

		assertTrue(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.COMPLETED_YEAR_8_OR_BELOW, 9));
		assertTrue(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.COMPLETED_YEAR_8_OR_BELOW, 10));
		assertTrue(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.COMPLETED_YEAR_8_OR_BELOW, 11));
		assertTrue(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.COMPLETED_YEAR_8_OR_BELOW, 12));
		assertTrue(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.COMPLETED_YEAR_8_OR_BELOW, 13));

		assertFalse(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.COMPLETED_YEAR_9, 9));
		assertTrue(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.COMPLETED_YEAR_9, 10));
		assertTrue(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.COMPLETED_YEAR_9, 11));
		assertTrue(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.COMPLETED_YEAR_9, 12));
		assertTrue(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.COMPLETED_YEAR_9, 13));

		assertFalse(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.COMPLETED_YEAR_10, 9));
		assertFalse(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.COMPLETED_YEAR_10, 10));
		assertTrue(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.COMPLETED_YEAR_10, 11));
		assertTrue(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.COMPLETED_YEAR_10, 12));
		assertTrue(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.COMPLETED_YEAR_10, 13));

		assertFalse(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.COMPLETED_YEAR_11, 9));
		assertFalse(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.COMPLETED_YEAR_11, 10));
		assertFalse(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.COMPLETED_YEAR_11, 11));
		assertTrue(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.COMPLETED_YEAR_11, 12));
		assertTrue(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.COMPLETED_YEAR_11, 13));

		assertFalse(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.COMPLETED_YEAR_12, 9));
		assertFalse(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.COMPLETED_YEAR_12, 10));
		assertFalse(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.COMPLETED_YEAR_12, 11));
		assertFalse(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.COMPLETED_YEAR_12, 12));
		assertTrue(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.COMPLETED_YEAR_12, 13));

	}
}
