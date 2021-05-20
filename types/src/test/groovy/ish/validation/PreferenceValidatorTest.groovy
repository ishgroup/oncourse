/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.validation

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test


@CompileStatic
class PreferenceValidatorTest {

	@Test
    void testAccountInvoiceTermsValidation() {
		Assertions.assertTrue(PreferenceValidator.isValidAccountInvoiceTerms(0))
        Assertions.assertTrue(PreferenceValidator.isValidAccountInvoiceTerms(7))
        Assertions.assertTrue(PreferenceValidator.isValidAccountInvoiceTerms(100))
        Assertions.assertFalse(PreferenceValidator.isValidAccountInvoiceTerms(null))
        Assertions.assertFalse(PreferenceValidator.isValidAccountInvoiceTerms(-30))
    }
}
