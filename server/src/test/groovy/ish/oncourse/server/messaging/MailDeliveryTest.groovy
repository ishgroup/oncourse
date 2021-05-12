/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.messaging

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

import javax.mail.internet.AddressException
import javax.mail.internet.InternetAddress

import static org.junit.jupiter.api.Assertions.fail

/**
 * contains lots of simplistic tests. I could not understand from the API how the InternetAddress relates/translates to the emails sent from javamail.<br>
 * also will work as safety, in case the libraries we use are upgraded or changed.
 *
 */
@CompileStatic
class MailDeliveryTest {

    /**
     * A silly test to check the internetaddress class, it seems not to work as expected
     */
    @Test
    void testInternetAddress() {
        String emailAddressStringSimple = "test@test.te"
        String emailAddressPersonal = "Test Test"
        String emailAddressStringComplex = emailAddressPersonal + " <" + emailAddressStringSimple + ">"

        // simple, strict
        try {
            InternetAddress address = new InternetAddress(emailAddressStringSimple)
            Assertions.assertEquals(emailAddressStringSimple, address.getAddress(), "checking the address ")
        } catch (AddressException e) {
            fail("cannot parse the address")
        }

        // simple, not strict
        try {
            InternetAddress address = new InternetAddress(emailAddressStringSimple, false)
            Assertions.assertEquals(emailAddressStringSimple, address.getAddress(), "checking the address ")
        } catch (AddressException e) {
            fail("cannot parse the address")
        }

        // complex, strict
        try {
            InternetAddress address = new InternetAddress(emailAddressStringComplex)
            Assertions.assertEquals(emailAddressStringSimple, address.getAddress(), "checking the address ")
        } catch (AddressException e) {
            fail("cannot parse the address")
        }

        // complex, not strict
        try {
            InternetAddress address = new InternetAddress(emailAddressStringComplex)
            Assertions.assertEquals(emailAddressStringSimple, address.getAddress(), "checking the address ")
        } catch (AddressException e) {
            fail("cannot parse the address")
        }

        // simple, personal
        try {
            InternetAddress address = new InternetAddress(emailAddressStringSimple, emailAddressPersonal)
            Assertions.assertEquals(emailAddressStringSimple, address.getAddress(), "checking the address ")
        } catch (UnsupportedEncodingException e) {
            fail("cannot parse the address")
        }

        // complex, personal
        try {
            InternetAddress address = new InternetAddress(emailAddressStringComplex, emailAddressPersonal)
            Assertions.assertEquals(emailAddressStringComplex, address.getAddress(), "checking the address ")
        } catch (UnsupportedEncodingException e) {
            fail("cannot parse the address")
        }

        // complex, personal
        try {
            InternetAddress address = new InternetAddress(emailAddressStringComplex, "not matching")
            Assertions.assertEquals(emailAddressStringComplex, address.getAddress(), "checking the address ")
        } catch (UnsupportedEncodingException e) {
            fail("cannot parse the address")
        }

    }
}
