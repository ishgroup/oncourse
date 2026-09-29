/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.v1.function

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.*

@CompileStatic
class ContactFunctionsTest {

    @Test
    void testIsValidEmailAddress_validEmail() {
        assertTrue(ContactFunctions.isValidEmailAddress("test@example.com"))
    }

    @Test
    void testIsValidEmailAddress_anotherValidEmail() {
        assertTrue(ContactFunctions.isValidEmailAddress("user.name+tag@sub.domain.org"))
    }

    @Test
    void testIsValidEmailAddress_invalidEmail() {
        assertFalse(ContactFunctions.isValidEmailAddress("not-an-email"))
    }

    @Test
    void testIsValidEmailAddress_invalidEmailMissingDomain() {
        assertFalse(ContactFunctions.isValidEmailAddress("user@"))
    }

    @Test
    void testIsValidEmailAddress_blankString() {
        assertFalse(ContactFunctions.isValidEmailAddress(""))
    }

    @Test
    void testIsValidEmailAddress_whitespaceOnly() {
        assertFalse(ContactFunctions.isValidEmailAddress("   "))
    }

    @Test
    void testIsValidEmailAddress_null() {
        assertFalse(ContactFunctions.isValidEmailAddress(null))
    }
}
