package ish.oncourse.server.messaging

import org.junit.jupiter.api.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNull

/**
 * Created by anarut on 8/23/16.
 */
class GetEnvelopeFromTest {

    @Test
    void testGetEnvelopeFromFunction() {
        String emailAddressStringSimple = "test@test.te"

        GetEnvelopeFrom getEnvelopeFrom1 = GetEnvelopeFrom.valueOf(null)
        assertNull(getEnvelopeFrom1.get())

        GetEnvelopeFrom getEnvelopeFrom2 = GetEnvelopeFrom.valueOf(emailAddressStringSimple)
        assertEquals(emailAddressStringSimple, getEnvelopeFrom2.get())

        String emailBounceAddress = "testbounce@test.te"
        String plusTag = "tag"

        GetEnvelopeFrom getEnvelopeFrom3 = GetEnvelopeFrom.valueOf(emailAddressStringSimple, false, emailBounceAddress, plusTag)
        assertEquals(emailAddressStringSimple, getEnvelopeFrom3.get())

        String expectedResult = "testbounce+tag@test.te"

        GetEnvelopeFrom getEnvelopeFrom4 = GetEnvelopeFrom.valueOf(emailAddressStringSimple, true, emailBounceAddress, plusTag)
        assertEquals(expectedResult, getEnvelopeFrom4.get())
    }
}
