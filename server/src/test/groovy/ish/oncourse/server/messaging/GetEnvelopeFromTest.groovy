package ish.oncourse.server.messaging

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test


/**
 * Created by anarut on 8/23/16.
 */
@CompileStatic
class GetEnvelopeFromTest {

    
    @Test
    void testGetEnvelopeFromFunction() {
        String emailAddressStringSimple = "test@test.te"

        GetEnvelopeFrom getEnvelopeFrom1 = GetEnvelopeFrom.valueOf(null)
        Assertions.assertNull(getEnvelopeFrom1.get())

        GetEnvelopeFrom getEnvelopeFrom2 = GetEnvelopeFrom.valueOf(emailAddressStringSimple)
        Assertions.assertEquals(emailAddressStringSimple, getEnvelopeFrom2.get())

        String emailBounceAddress = "testbounce@test.te"
        String plusTag = "tag"

        GetEnvelopeFrom getEnvelopeFrom3 = GetEnvelopeFrom.valueOf(emailAddressStringSimple, false, emailBounceAddress, plusTag)
        Assertions.assertEquals(emailAddressStringSimple, getEnvelopeFrom3.get())

        String expectedResult = "testbounce+tag@test.te"

        GetEnvelopeFrom getEnvelopeFrom4 = GetEnvelopeFrom.valueOf(emailAddressStringSimple, true, emailBounceAddress, plusTag)
        Assertions.assertEquals(expectedResult, getEnvelopeFrom4.get())
    }
}
