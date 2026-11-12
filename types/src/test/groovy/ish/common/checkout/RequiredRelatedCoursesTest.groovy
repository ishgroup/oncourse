/*
 * Copyright ish group pty ltd 2026.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.common.checkout

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

import java.util.function.Predicate

import static ish.common.types.EntityRelationCartAction.*

@CompileStatic
class RequiredRelatedCoursesTest {

    private static final Predicate<String> ENROLLED_IN_CERT_III = { String c -> c == 'CERT_III' } as Predicate<String>

    @Test
    void testIsAddedToCart() {
        Assertions.assertTrue(RequiredRelatedCourses.isAddedToCart(ADD_NO_REMOVAL))
        Assertions.assertTrue(RequiredRelatedCourses.isAddedToCart(ADD_ALLOW_REMOVAL))
        Assertions.assertFalse(RequiredRelatedCourses.isAddedToCart(SUGGESTION))
        Assertions.assertFalse(RequiredRelatedCourses.isAddedToCart(NO_ACTION))
        Assertions.assertFalse(RequiredRelatedCourses.isAddedToCart(null))
    }

    @Test
    void testRequiredCourseInCheckout() {
        // Diploma + Cert III in the same checkout: allowed regardless of history
        Assertions.assertEquals([], RequiredRelatedCourses.getMissingCourses(['CERT_III': true], { String c -> false } as Predicate<String>, ['CERT_III']))
        Assertions.assertEquals([], RequiredRelatedCourses.getMissingCourses(['CERT_III': false], { String c -> false } as Predicate<String>, ['CERT_III']))
    }

    @Test
    void testRequiredCourseInHistory() {
        // consider history: previous enrolment is enough
        Assertions.assertEquals([], RequiredRelatedCourses.getMissingCourses(['CERT_III': true], ENROLLED_IN_CERT_III, []))
        // history is not considered: course must be in the checkout even if student was enrolled before
        Assertions.assertEquals(['CERT_III'], RequiredRelatedCourses.getMissingCourses(['CERT_III': false], ENROLLED_IN_CERT_III, []))
    }

    @Test
    void testRequiredCourseMissing() {
        Assertions.assertEquals(['CERT_IV'],
                RequiredRelatedCourses.getMissingCourses(['CERT_III': true, 'CERT_IV': true], ENROLLED_IN_CERT_III, null))
        Assertions.assertEquals([], RequiredRelatedCourses.getMissingCourses([:], ENROLLED_IN_CERT_III, null))
        Assertions.assertEquals([], RequiredRelatedCourses.getMissingCourses(null, ENROLLED_IN_CERT_III, null))
    }

    @Test
    void testMergeConsiderHistory() {
        Map<String, Boolean> required = [:]
        RequiredRelatedCourses.mergeConsiderHistory(required, 'CERT_III', true)
        RequiredRelatedCourses.mergeConsiderHistory(required, 'CERT_IV', null)
        RequiredRelatedCourses.mergeConsiderHistory(required, null, true)
        Assertions.assertEquals(['CERT_III': true, 'CERT_IV': false], required)

        RequiredRelatedCourses.mergeConsiderHistory(required, 'CERT_III', false)
        Assertions.assertEquals(['CERT_III': false, 'CERT_IV': false], required)
    }

    @Test
    void testGetMissingCourseNames() {
        Assertions.assertEquals(['Certificate IV'],
                RequiredRelatedCourses.getMissingCourseNames(['CERT_III': true, 'CERT_IV': true], ENROLLED_IN_CERT_III, [],
                        { String c -> c == 'CERT_IV' ? 'Certificate IV' : c } as java.util.function.Function<String, String>))
    }

    @Test
    void testErrorMessage() {
        Assertions.assertEquals('John Smith is unable to enrol in Diploma without an enrolment in Cert III, Cert IV. Please add it to the checkout.',
                RequiredRelatedCourses.getErrorMessage('John Smith', 'Diploma', ['Cert III', 'Cert IV']))
    }
}
