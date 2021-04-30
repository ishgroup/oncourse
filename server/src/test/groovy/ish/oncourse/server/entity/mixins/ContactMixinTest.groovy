package ish.oncourse.server.entity.mixins

import ish.IshTestCase
import ish.oncourse.server.cayenne.Contact
import org.junit.jupiter.api.Test
import org.mockito.Mockito

import static org.junit.Assert.assertEquals

/**
 * Created by anarut on 7/6/16.
 */
class ContactMixinTest extends IshTestCase {

    @Test
    void testPortalLoginURL() {
        Contact contactWitoutEmail = Mockito.mock(Contact)
        Mockito.when(contactWitoutEmail.email).thenReturn(null)
        assertEquals("https://www.skillsoncourse.com.au/portal/login", contactWitoutEmail.portalLoginURL)

        Contact contactWithEmail = Mockito.mock(Contact)
        Mockito.when(contactWithEmail.email).thenReturn("test.email@ish.com.au")
        assertEquals("https://www.skillsoncourse.com.au/portal/login?e=test.email@ish.com.au", contactWithEmail.portalLoginURL)
    }
}
