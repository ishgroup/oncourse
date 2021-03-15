/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.messaging

import static org.junit.Assert.assertEquals
import static org.junit.Assert.fail
import org.junit.Test

import javax.mail.internet.AddressException
import javax.mail.internet.InternetAddress

/**
 * contains lots of simplistic tests. I could not understand from the API how the InternetAddress relates/translates to the emails sent from javamail.<br>
 * also will work as safety, in case the libraries we use are upgraded or changed.
 *
 */
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
            assertEquals("checking the address ", emailAddressStringSimple, address.getAddress())
        } catch (AddressException e) {
            fail("cannot parse the address")
        }

		// simple, not strict
		try {
			InternetAddress address = new InternetAddress(emailAddressStringSimple, false)
            assertEquals("checking the address ", emailAddressStringSimple, address.getAddress())
        } catch (AddressException e) {
            fail("cannot parse the address")
        }

		// complex, strict
		try {
			InternetAddress address = new InternetAddress(emailAddressStringComplex)
            assertEquals("checking the address ", emailAddressStringSimple, address.getAddress())
        } catch (AddressException e) {
            fail("cannot parse the address")
        }

		// complex, not strict
		try {
			InternetAddress address = new InternetAddress(emailAddressStringComplex)
            assertEquals("checking the address ", emailAddressStringSimple, address.getAddress())
        } catch (AddressException e) {
            fail("cannot parse the address")
        }

		// simple, personal
		try {
			InternetAddress address = new InternetAddress(emailAddressStringSimple, emailAddressPersonal)
            assertEquals("checking the address ", emailAddressStringSimple, address.getAddress())
        } catch (UnsupportedEncodingException e) {
            fail("cannot parse the address")
        }

		// complex, personal
		try {
			InternetAddress address = new InternetAddress(emailAddressStringComplex, emailAddressPersonal)
            assertEquals("checking the address ", emailAddressStringComplex, address.getAddress())
        } catch (UnsupportedEncodingException e) {
            fail("cannot parse the address")
        }

		// complex, personal
		try {
			InternetAddress address = new InternetAddress(emailAddressStringComplex, "not matching")
            assertEquals("checking the address ", emailAddressStringComplex, address.getAddress())
        } catch (UnsupportedEncodingException e) {
            fail("cannot parse the address")
        }

	}
}
