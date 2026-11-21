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
import ish.common.types.EnrolmentStatus
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

import java.util.function.BooleanSupplier
import java.util.function.Function
import java.util.function.Predicate

import static ish.common.types.EntityRelationCartAction.*

@CompileStatic
class RequiredRelatedCoursesTest {

    private static final Predicate<String> ENROLLED_IN_CERT_III = { String c -> c == 'CERT_III' } as Predicate<String>
    private static final BooleanSupplier ENROLLED = { true } as BooleanSupplier
    private static final BooleanSupplier NOT_ENROLLED = { false } as BooleanSupplier

    @Test
    void testPreviousEnrolmentStatuses() {
        Assertions.assertEquals([EnrolmentStatus.SUCCESS], RequiredRelatedCourses.PREVIOUS_ENROLMENT_STATUSES)
    }

    @Test
    void testIsSatisfiedByHistory() {
        Assertions.assertTrue(RequiredRelatedCourses.isSatisfiedByHistory(true, ENROLLED))
        Assertions.assertFalse(RequiredRelatedCourses.isSatisfiedByHistory(true, NOT_ENROLLED))
        Assertions.assertFalse(RequiredRelatedCourses.isSatisfiedByHistory(false, ENROLLED))
        // history is not checked at all when relation type does not consider it
        Assertions.assertFalse(RequiredRelatedCourses.isSatisfiedByHistory(null, { throw new AssertionError('must not be called') } as BooleanSupplier))
    }

    @Test
    void testIsRequiredInCheckout() {
        Assertions.assertTrue(RequiredRelatedCourses.isRequiredInCheckout(ADD_NO_REMOVAL, true, NOT_ENROLLED))
        Assertions.assertTrue(RequiredRelatedCourses.isRequiredInCheckout(ADD_ALLOW_REMOVAL, true, NOT_ENROLLED))
        Assertions.assertTrue(RequiredRelatedCourses.isRequiredInCheckout(ADD_NO_REMOVAL, false, ENROLLED))
        Assertions.assertFalse(RequiredRelatedCourses.isRequiredInCheckout(ADD_NO_REMOVAL, true, ENROLLED))
        Assertions.assertFalse(RequiredRelatedCourses.isRequiredInCheckout(SUGGESTION, false, NOT_ENROLLED))
        Assertions.assertFalse(RequiredRelatedCourses.isRequiredInCheckout(NO_ACTION, false, NOT_ENROLLED))
        Assertions.assertFalse(RequiredRelatedCourses.isRequiredInCheckout(null, false, NOT_ENROLLED))
    }

    @Test
    void testIsMandatory() {
        Assertions.assertTrue(RequiredRelatedCourses.isMandatory(ADD_NO_REMOVAL))
        Assertions.assertFalse(RequiredRelatedCourses.isMandatory(ADD_ALLOW_REMOVAL))
        Assertions.assertFalse(RequiredRelatedCourses.isMandatory(SUGGESTION))
        Assertions.assertFalse(RequiredRelatedCourses.isMandatory(NO_ACTION))
        Assertions.assertFalse(RequiredRelatedCourses.isMandatory(null))
    }

    @Test
    void testAddMandatoryCourse() {
        Map<String, Boolean> mandatory = [:]
        RequiredRelatedCourses.addMandatoryCourse(mandatory, 'CERT_III', ADD_NO_REMOVAL, true)
        RequiredRelatedCourses.addMandatoryCourse(mandatory, 'CERT_IV', ADD_NO_REMOVAL, null)
        RequiredRelatedCourses.addMandatoryCourse(mandatory, 'OPTIONAL', ADD_ALLOW_REMOVAL, true)
        RequiredRelatedCourses.addMandatoryCourse(mandatory, 'SUGGESTED', SUGGESTION, true)
        RequiredRelatedCourses.addMandatoryCourse(mandatory, null, ADD_NO_REMOVAL, true)
        Assertions.assertEquals(['CERT_III': true, 'CERT_IV': false], mandatory)

        RequiredRelatedCourses.addMandatoryCourse(mandatory, 'CERT_III', ADD_NO_REMOVAL, false)
        Assertions.assertEquals(['CERT_III': false, 'CERT_IV': false], mandatory)
    }

    @Test
    void testMandatoryCourseInCheckout() {
        Assertions.assertEquals([], RequiredRelatedCourses.getMissingCourses(['CERT_III': true], { String c -> false } as Predicate<String>, ['CERT_III']))
        Assertions.assertEquals([], RequiredRelatedCourses.getMissingCourses(['CERT_III': false], { String c -> false } as Predicate<String>, ['CERT_III']))
    }

    @Test
    void testMandatoryCourseInHistory() {
        Assertions.assertEquals([], RequiredRelatedCourses.getMissingCourses(['CERT_III': true], ENROLLED_IN_CERT_III, []))
        Assertions.assertEquals(['CERT_III'], RequiredRelatedCourses.getMissingCourses(['CERT_III': false], ENROLLED_IN_CERT_III, []))
    }

    @Test
    void testMandatoryCourseMissing() {
        Assertions.assertEquals(['CERT_IV'],
                RequiredRelatedCourses.getMissingCourses(['CERT_III': true, 'CERT_IV': true], ENROLLED_IN_CERT_III, null))
        Assertions.assertEquals([], RequiredRelatedCourses.getMissingCourses([:], ENROLLED_IN_CERT_III, null))
        Assertions.assertEquals([], RequiredRelatedCourses.getMissingCourses(null, ENROLLED_IN_CERT_III, null))
    }

    @Test
    void testGetMissingCourseNames() {
        Assertions.assertEquals(['Certificate IV'],
                RequiredRelatedCourses.getMissingCourseNames(['CERT_III': true, 'CERT_IV': true], ENROLLED_IN_CERT_III, [],
                        { String c -> c == 'CERT_IV' ? 'Certificate IV' : c } as Function<String, String>))
    }

    @Test
    void testErrorMessage() {
        Assertions.assertEquals('John Smith is unable to enrol in Diploma without an enrolment in Cert III, Cert IV. Please add it to the checkout.',
                RequiredRelatedCourses.getErrorMessage('John Smith', 'Diploma', ['Cert III', 'Cert IV']))
    }
}
