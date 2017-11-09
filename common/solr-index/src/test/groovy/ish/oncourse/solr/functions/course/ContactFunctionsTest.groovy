package ish.oncourse.solr.functions.course

import com.github.javafaker.Faker
import com.github.javafaker.Name
import com.github.javafaker.Number
import ish.oncourse.model.Contact
import ish.oncourse.model.Tutor
import ish.oncourse.solr.model.SContact
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

import static org.junit.Assert.assertEquals
import static org.mockito.Mockito.when

/**
 * User: akoiro
 * Date: 4/11/17
 */
class ContactFunctionsTest {
    private static final Name nameFaker = new Faker().name()
    private static final Number numberFaker = new Faker().number()

    private Contact contact

    @Before
    void before() {
        contact = contact()
    }

    @Test
    void test_contact_id_tutor_id() {
        use(ContactFunctions) {
            SContact sContact = contact.getSContact()
            assertEquals(contact.id, sContact.id)
            assertEquals(contact.tutor.id, sContact.tutorId)
        }

    }

    @Test
    void test_givenName_familyName() {
        use(ContactFunctions) {
            SContact sContact = contact.getSContact()
            assertEquals("${contact.givenName} ${contact.familyName}".toString(),
                    sContact.name)
        }
    }

    @Test
    void test_givenName() {
        when(contact.familyName).thenReturn(null)

        use(ContactFunctions) {
            SContact sContact = contact.getSContact()
            assertEquals("${contact.givenName}".toString(),
                    sContact.name)
        }
    }

    @Test
    void test_familyName() {
        when(contact.givenName).thenReturn(null)

        use(ContactFunctions) {
            SContact sContact = contact.getSContact()
            assertEquals("${contact.familyName}".toString(),
                    sContact.name)
        }
    }

    static Contact contact() {
        Tutor tutor = Mockito.mock(Tutor)
        when(tutor.id).thenReturn(numberFaker.numberBetween(2001l, 3000l))

        Contact contact = Mockito.mock(Contact)
        when(contact.id).thenReturn(numberFaker.numberBetween(1000l, 2000l))
        when(contact.tutor).thenReturn(tutor)
        when(contact.givenName).thenReturn(nameFaker.firstName())
        when(contact.familyName).thenReturn(nameFaker.lastName())
        contact
    }
}
