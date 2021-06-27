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
class ValidationUtilTest {
	
	private static final String[] validEmails = [
			"testEmail@domain.org",
			"test+Email@domain.org",
			"test.Email@domain.org",
			"test/Email@domain.org",
			"test|Email@domain.org",
			"test_Email@domain.org",
			"testEmail@domain",
			"testEmail@111.111.111.111",
			"test\\Email@domain.org"
		]

	private static final String[] invalidEmails = [
			null,
			"",
			"testEmail-domain.org",
			"@domain.org",
			"testEmail@",
			"test_Email",
			"testEmail@domain./org",
			"testEmail@domain.|org",
			"testEmail@domain.org.",
			"testEmail@domain.org?",
			"testEmail@domain.org,com",
			"testEmail@domain.\\org",
			"testEmail@domain.org,,,",
			"testEmail@domain.org,;"
	]

	@Test
	void testEmail() {
		for (String validEmail : validEmails) {
			Assertions.assertTrue(ValidationUtil.isValidEmailAddress(validEmail), String.format("This email should be valid %s", validEmail))
		}

		for (String invalidEmail : invalidEmails) {
			Assertions.assertFalse(ValidationUtil.isValidEmailAddress(invalidEmail), String.format("This email should be invalid %s", invalidEmail))
		}
	}
}
