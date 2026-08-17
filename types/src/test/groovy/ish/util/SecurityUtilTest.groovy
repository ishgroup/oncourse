/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.util

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@CompileStatic
class SecurityUtilTest {

    @Test
    void testHashByteArrayProducesValidHexLength() {
        // SHA-1 always produces 20 bytes = exactly 40 lowercase hex characters
        byte[] input = "test-password-123".bytes
        String hash = SecurityUtil.hashByteArray(input)
        Assertions.assertNotNull(hash)
        Assertions.assertEquals(40, hash.length(), "SHA-1 hash must be 40 hex chars")
        Assertions.assertTrue(hash ==~ /[0-9a-f]+/, "Hash must contain only lowercase hex characters")
    }

    @Test
    void testHashByteArrayDifferentInputsDifferentHashes() {
        String hash1 = SecurityUtil.hashByteArray("password1".bytes)
        String hash2 = SecurityUtil.hashByteArray("password2".bytes)
        Assertions.assertNotNull(hash1)
        Assertions.assertNotNull(hash2)
        Assertions.assertNotEquals(hash1, hash2, "Different inputs must produce different hashes")
    }

    @Test
    void generateUSISoftwareId() {
        String id = SecurityUtil.generateUSISoftwareId()
        Assertions.assertEquals(id.length(), 10)
        int checkSumm = Character.getNumericValue(id.charAt(9))
        int summ = 0
        for (char it :id.substring(0,9).toCharArray()) {
            summ += Character.getNumericValue(it)
        }
        Assertions.assertEquals(checkSumm, summ % 10)
    }
}
