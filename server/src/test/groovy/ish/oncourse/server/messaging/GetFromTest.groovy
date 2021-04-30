package ish.oncourse.server.messaging

import org.junit.jupiter.api.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNull

/**
 * Created by anarut on 8/23/16.
 */
class GetFromTest {

    @Test
    void testGetFromFunction() {
        String emailAddressStringSimple = "test@test.te"
        String emailAddressPersonal = "Test Test"

        // test no params provided
        GetFrom getFrom1 = GetFrom.valueOf(null)
        assertNull("checking the address ", getFrom1.get())

        // test no params provided
        GetFrom getFrom2 = GetFrom.valueOf(null, null)
        assertNull("checking the address ", getFrom2.get())

        // test email provided
        GetFrom getFrom3 = GetFrom.valueOf(emailAddressStringSimple)
        assertEquals("checking the address ", emailAddressStringSimple, getFrom3.get().address)

        // test no personal data provided
        GetFrom getFrom4 = GetFrom.valueOf(emailAddressStringSimple, null)
        assertEquals("checking the address ", emailAddressStringSimple, getFrom4.get().address)

        // test no email provided
        GetFrom getFrom5 = GetFrom.valueOf(null, emailAddressPersonal)
        assertNull("checking the address ", getFrom5.get())

        // test email and personal provided
        GetFrom getFrom6 = GetFrom.valueOf(emailAddressStringSimple, emailAddressPersonal)
        assertEquals("checking the address ", emailAddressStringSimple, getFrom6.get().address)
        assertEquals("checking the personal ", emailAddressPersonal, getFrom6.get().personal)

        // test invalid email provided, no personal info
        try {
            GetFrom.valueOf("corrupted@email@address", null).get()
            fail("shouldn't be able to parse address")
        } catch (Exception ignored) {

        }

        // test invalid email provided, with personal info
        try {
            GetFrom.valueOf("corrupted@email@address", emailAddressPersonal).get()
            fail("shouldn't be able to parse address")
        } catch (Exception ignored) {

        }
    }
}