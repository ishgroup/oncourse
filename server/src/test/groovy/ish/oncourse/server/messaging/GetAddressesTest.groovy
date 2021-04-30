package ish.oncourse.server.messaging

import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.MessagePerson
import org.junit.jupiter.api.Test
import org.mockito.Mockito

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

/**
 * Created by anarut on 8/23/16.
 */
class GetAddressesTest {

    @Test
    void testGetAddressesTestFunction() {
        String recipientEmail = 'test.onCourse@ish.com.au'

        GetAddresses empty = GetAddresses.empty()
        assertEquals(0, empty.get().size())



        GetAddresses getAddresses1 = GetAddresses.valueOf(recipientEmail)
        assertEquals(1, getAddresses1.get().size())
        assertEquals(recipientEmail, getAddresses1.get()[0].toString())



        List list = new ArrayList()
        list.add('test1.onCourse@ish.com.au')
        list.add('test2.onCourse@ish.com.au')
        list.add('test2.onCourse@ish.com.au')

        String expectedResult2 = '[test1.onCourse@ish.com.au, test2.onCourse@ish.com.au]'

        GetAddresses getAddresses2 = GetAddresses.valueOf(list)
        assertEquals(2, getAddresses2.get().size())
        assertEquals(expectedResult2, getAddresses2.get().toString())



        Set set = new HashSet()
        set.add('test1.onCourse@ish.com.au')
        set.add('test2.onCourse@ish.com.au')
        set.add('test3.onCourse@ish.com.au')

        def expectedResults3 = [
                '[test1.onCourse@ish.com.au, test2.onCourse@ish.com.au, test3.onCourse@ish.com.au]',
                '[test1.onCourse@ish.com.au, test3.onCourse@ish.com.au, test2.onCourse@ish.com.au]',
                '[test2.onCourse@ish.com.au, test1.onCourse@ish.com.au, test3.onCourse@ish.com.au]',
                '[test2.onCourse@ish.com.au, test3.onCourse@ish.com.au, test1.onCourse@ish.com.au]',
                '[test3.onCourse@ish.com.au, test1.onCourse@ish.com.au, test2.onCourse@ish.com.au]',
                '[test3.onCourse@ish.com.au, test2.onCourse@ish.com.au, test1.onCourse@ish.com.au]'
        ]

        GetAddresses getAddresses3 = GetAddresses.valueOf(set)
        assertEquals(3, getAddresses3.get().size())
        assertTrue(expectedResults3.contains(getAddresses3.get().toString()))



        MessagePerson messagePerson1 = getMessagePerson(true)
        String expectedResult4 = 'testLastName <destinationAddress@ish.com.au>'
        GetAddresses getAddresses4 = GetAddresses.valueOf(messagePerson1)
        assertEquals(1, getAddresses4.get().size())
        assertEquals(expectedResult4, getAddresses4.get()[0].toString())



        MessagePerson messagePerson2 = getMessagePerson(false)
        String expectedResult5 = 'testFirstName testLastName <destinationAddress@ish.com.au>'
        GetAddresses getAddresses5 = GetAddresses.valueOf(messagePerson2)
        assertEquals(expectedResult5, getAddresses5.get()[0].toString())
    }


    private MessagePerson getMessagePerson(boolean isCompany) {
        Contact contact = Mockito.mock(Contact)
        Mockito.when(contact.firstName).thenReturn('testFirstName')
        Mockito.when(contact.lastName).thenReturn('testLastName')
        Mockito.when(contact.isCompany).thenReturn(isCompany)

        MessagePerson messagePerson = Mockito.mock(MessagePerson)
        Mockito.when(messagePerson.destinationAddress).thenReturn('destinationAddress@ish.com.au')
        Mockito.when(messagePerson.contact).thenReturn(contact)

        messagePerson
    }
}
