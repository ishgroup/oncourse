package ish.oncourse.server.entity.mixins

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import ish.TestWithBootique
import ish.oncourse.server.cayenne.Contact
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito

@CompileStatic
class ContactMixinTest extends TestWithBootique {

    @CompileDynamic
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
