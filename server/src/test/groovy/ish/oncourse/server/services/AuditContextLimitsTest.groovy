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

package ish.oncourse.server.services

import groovy.transform.CompileStatic
import ish.oncourse.types.AuditSource
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable

import java.nio.charset.StandardCharsets

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertNull
import static org.junit.jupiter.api.Assertions.assertSame
import static org.junit.jupiter.api.Assertions.assertThrows
import static org.junit.jupiter.api.Assertions.assertTrue

/**
 * The context values have to fit the Audit columns, otherwise the async insert fails and the entry is lost.
 */
@CompileStatic
class AuditContextLimitsTest {

    @Test
    void testEverySourceFitsTheColumn() {
        AuditSource.values().each { AuditSource source ->
            assertTrue(source.databaseValue.length() <= AuditContext.SOURCE_MAX_LENGTH, source.name())
            assertEquals(source, AuditContext.of(source).source)
        }
    }

    @Test
    void testChangeSetRefOfColumnLengthIsKept() {
        String ref = 'r' * AuditContext.CHANGE_SET_REF_MAX_LENGTH

        assertEquals(ref, AuditContext.assistant('request', ref).changeSetRef)
    }

    @Test
    void testTooLongChangeSetRefIsRejected() {
        String ref = 'r' * (AuditContext.CHANGE_SET_REF_MAX_LENGTH + 1)

        IllegalArgumentException e = assertThrows(IllegalArgumentException,
                { AuditContext.assistant('request', ref) } as Executable)
        assertTrue(e.message.contains(String.valueOf(AuditContext.CHANGE_SET_REF_MAX_LENGTH)))
        assertThrows(IllegalArgumentException,
                { new AuditContext(AuditSource.USER, null, ref, false) } as Executable)
    }

    @Test
    void testShortRequestTextIsKept() {
        String text = 'Move tomorrow\'s evening classes at Sydney to room 2'

        assertSame(text, AuditContext.assistant(text, 'ref').requestText)
        assertNull(AuditContext.assistant(null, 'ref').requestText)
    }

    @Test
    void testRequestTextOfColumnSizeIsKept() {
        String text = 'a' * AuditContext.REQUEST_TEXT_MAX_BYTES

        assertEquals(text, AuditContext.assistant(text, 'ref').requestText)
    }

    @Test
    void testLongRequestTextIsTruncatedToColumnSize() {
        String text = 'a' * (AuditContext.REQUEST_TEXT_MAX_BYTES + 100)

        String stored = AuditContext.assistant(text, 'ref').requestText

        assertEquals(AuditContext.REQUEST_TEXT_MAX_BYTES, stored.length())
        assertTrue(text.startsWith(stored))
    }

    @Test
    void testTruncationCountsBytesAndKeepsCharactersWhole() {
        // 'я' is 2 bytes in UTF-8, '😀' is 4 bytes and 2 chars
        String cyrillic = 'я' * AuditContext.REQUEST_TEXT_MAX_BYTES
        String stored = AuditContext.assistant(cyrillic, 'ref').requestText
        assertEquals(AuditContext.REQUEST_TEXT_MAX_BYTES >> 1, stored.length())

        String emoji = 'a' + '😀' * AuditContext.REQUEST_TEXT_MAX_BYTES
        stored = AuditContext.assistant(emoji, 'ref').requestText
        int bytes = stored.getBytes(StandardCharsets.UTF_8).length
        assertTrue(bytes <= AuditContext.REQUEST_TEXT_MAX_BYTES)
        assertTrue(bytes > AuditContext.REQUEST_TEXT_MAX_BYTES - 4)
        assertTrue(emoji.startsWith(stored))
        assertTrue(Character.isSurrogatePair(stored.charAt(stored.length() - 2), stored.charAt(stored.length() - 1)))
    }
}
