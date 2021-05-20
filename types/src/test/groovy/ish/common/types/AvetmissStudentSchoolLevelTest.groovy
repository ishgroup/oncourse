package ish.common.types

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test


@CompileStatic
class AvetmissStudentSchoolLevelTest {

	@Test
    void testIsValid() {
		Assertions.assertTrue(AvetmissStudentSchoolLevel.isValid(null, 9))
        Assertions.assertTrue(AvetmissStudentSchoolLevel.isValid(null, 10))
        Assertions.assertTrue(AvetmissStudentSchoolLevel.isValid(null, 11))
        Assertions.assertTrue(AvetmissStudentSchoolLevel.isValid(null, 12))

        Assertions.assertTrue(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.DEFAULT_POPUP_OPTION, 9))
        Assertions.assertTrue(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.DEFAULT_POPUP_OPTION, 10))
        Assertions.assertTrue(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.DEFAULT_POPUP_OPTION, 11))
        Assertions.assertTrue(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.DEFAULT_POPUP_OPTION, 12))

        Assertions.assertTrue(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.DID_NOT_GO_TO_SCHOOL, 9))
        Assertions.assertTrue(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.DID_NOT_GO_TO_SCHOOL, 10))
        Assertions.assertTrue(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.DID_NOT_GO_TO_SCHOOL, 11))
        Assertions.assertTrue(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.DID_NOT_GO_TO_SCHOOL, 12))

        Assertions.assertTrue(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.COMPLETED_YEAR_8_OR_BELOW, 9))
        Assertions.assertTrue(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.COMPLETED_YEAR_8_OR_BELOW, 10))
        Assertions.assertTrue(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.COMPLETED_YEAR_8_OR_BELOW, 11))
        Assertions.assertTrue(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.COMPLETED_YEAR_8_OR_BELOW, 12))
        Assertions.assertTrue(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.COMPLETED_YEAR_8_OR_BELOW, 13))

        Assertions.assertFalse(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.COMPLETED_YEAR_9, 9))
        Assertions.assertTrue(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.COMPLETED_YEAR_9, 10))
        Assertions.assertTrue(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.COMPLETED_YEAR_9, 11))
        Assertions.assertTrue(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.COMPLETED_YEAR_9, 12))
        Assertions.assertTrue(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.COMPLETED_YEAR_9, 13))

        Assertions.assertFalse(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.COMPLETED_YEAR_10, 9))
        Assertions.assertFalse(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.COMPLETED_YEAR_10, 10))
        Assertions.assertTrue(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.COMPLETED_YEAR_10, 11))
        Assertions.assertTrue(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.COMPLETED_YEAR_10, 12))
        Assertions.assertTrue(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.COMPLETED_YEAR_10, 13))

        Assertions.assertFalse(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.COMPLETED_YEAR_11, 9))
        Assertions.assertFalse(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.COMPLETED_YEAR_11, 10))
        Assertions.assertFalse(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.COMPLETED_YEAR_11, 11))
        Assertions.assertTrue(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.COMPLETED_YEAR_11, 12))
        Assertions.assertTrue(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.COMPLETED_YEAR_11, 13))

        Assertions.assertFalse(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.COMPLETED_YEAR_12, 9))
        Assertions.assertFalse(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.COMPLETED_YEAR_12, 10))
        Assertions.assertFalse(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.COMPLETED_YEAR_12, 11))
        Assertions.assertFalse(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.COMPLETED_YEAR_12, 12))
        Assertions.assertTrue(AvetmissStudentSchoolLevel.isValid(AvetmissStudentSchoolLevel.COMPLETED_YEAR_12, 13))
    }
}
