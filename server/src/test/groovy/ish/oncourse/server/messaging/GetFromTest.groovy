package ish.oncourse.server.messaging


import groovy.transform.CompileStatic
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@CompileStatic
class GetFromTest {

    
    @Test
    void testGetFromFunction() {
        String emailAddressStringSimple = "test@test.te"
        String emailAddressPersonal = "Test Test"

        // test no params provided
        GetFrom getFrom1 = GetFrom.valueOf(null)
        Assertions.assertNull(getFrom1.get(), "checking the address ")

        // test no params provided
        GetFrom getFrom2 = GetFrom.valueOf(null, null)
        Assertions.assertNull(getFrom2.get(), "checking the address ")

        // test email provided
        GetFrom getFrom3 = GetFrom.valueOf(emailAddressStringSimple)
        Assertions.assertEquals(emailAddressStringSimple, getFrom3.get().address, "checking the address ")

        // test no personal data provided
        GetFrom getFrom4 = GetFrom.valueOf(emailAddressStringSimple, null)
        Assertions.assertEquals(emailAddressStringSimple, getFrom4.get().address, "checking the address ")

        // test no email provided
        GetFrom getFrom5 = GetFrom.valueOf(null, emailAddressPersonal)
        Assertions.assertNull(getFrom5.get(), "checking the address ")

        // test email and personal provided
        GetFrom getFrom6 = GetFrom.valueOf(emailAddressStringSimple, emailAddressPersonal)
        Assertions.assertEquals("checking the address ", emailAddressStringSimple, getFrom6.get().address)
        Assertions.assertEquals(emailAddressPersonal, getFrom6.get().personal, "checking the personal ")

        // test invalid email provided, no personal info
        try {
            GetFrom.valueOf("corrupted@email@address", null).get()
            Assertions.fail("shouldn't be able to parse address")
        } catch (Exception ignored) {}

        // test invalid email provided, with personal info
        try {
            GetFrom.valueOf("corrupted@email@address", emailAddressPersonal).get()
            Assertions.fail("shouldn't be able to parse address")
        } catch (Exception ignored) {}
    }
}