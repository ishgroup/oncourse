package ish.oncourse.server.print.transformations

import ish.oncourse.server.cayenne.Contact
import org.apache.cayenne.DataRow
import static org.apache.cayenne.PersistenceState.COMMITTED
import static org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito
import static org.mockito.Mockito.when

class ContactDataRowDelegatorTest {

    @Test
    void test() {
        Contact contact = Mockito.mock(Contact.class)
        DataRow dataRow = Mockito.mock(DataRow.class)
        when(contact.firstName).thenReturn("FirstName")
        when(dataRow.OWING).thenReturn(100.00)
        when(contact.getPersistenceState()).thenReturn(COMMITTED)

        ContactDataRowDelegator delegator = new ContactDataRowDelegator(contact: contact, dataRow: dataRow)
        assertEquals("FirstName", delegator.firstName)
        assertEquals(100.00, delegator.owing)
        assertEquals(100.00, delegator.OWING)
        assertEquals(COMMITTED, delegator.persistenceState)
    }
}
