package ish.oncourse.server.entity.mixins

import groovy.transform.CompileDynamic
import ish.TestWithBootique
import ish.oncourse.server.cayenne.Contact
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito

// Note that bootique is needed here to add mixins to the model
@CompileDynamic
class ContactMixinTest extends TestWithBootique {

    @Test
    void testPortalLoginURL() {
        Contact contactWitoutEmail = Mockito.mock(Contact)
        Mockito.when(contactWitoutEmail.email).thenReturn(null)
        Assertions.assertEquals("https://www.skillsoncourse.com.au/portal/login", contactWitoutEmail.portalLoginURL)

        Contact contactWithEmail = Mockito.mock(Contact)
        Mockito.when(contactWithEmail.email).thenReturn("test.email@ish.com.au")
        Assertions.assertEquals("https://www.skillsoncourse.com.au/portal/login?e=test.email@ish.com.au", contactWithEmail.portalLoginURL)
    }
}
