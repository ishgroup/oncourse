package ish.oncourse.enrol.checkout;

import ish.oncourse.enrol.checkout.contact.ContactCredentials;
import ish.oncourse.enrol.services.student.IStudentService;
import ish.oncourse.model.College;
import ish.oncourse.model.Contact;
import org.apache.cayenne.ObjectContext;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

/**
 * Created by akoiro on 15/03/2016.
 */
public class ContactCredentialsEncoderTest {
    private College college = mock(College.class);
    private Contact newContact = mock(Contact.class);
    private Contact existingContact = mock(Contact.class);

    private ObjectContext context = mock(ObjectContext.class);
    private IStudentService service = mock(IStudentService.class);


    @Before
    public void before() {
        when(context.newObject(Contact.class)).thenReturn(newContact);
        when(context.localObject(college)).thenReturn(college);
        when(context.localObject(existingContact)).thenReturn(existingContact);
        when(service.getContact(anyString(), anyString(), anyString(), anyBoolean())).thenReturn(null);
    }

    @Test
    public void testNotAllowCreateContact() {
        ContactCredentials credentials = getContactCredentials();
        ContactCredentialsEncoder contactCredentialsEncoder =
                ContactCredentialsEncoder.valueOf(credentials, false, college, context, service, false);
        Contact result = contactCredentialsEncoder.encode();
        verify(context, never()).newObject(Contact.class);
        assertNull(result);
    }

    @Test
    public void testExistingContact() {

        ContactCredentials credentials = getContactCredentials();

        when(service.getContact(credentials.getFirstName(), credentials.getLastName(), credentials.getEmail(), false)).thenReturn(existingContact);

        ContactCredentialsEncoder contactCredentialsEncoder =
                ContactCredentialsEncoder.valueOf(credentials, false, college, context, service, true);

        Contact result = contactCredentialsEncoder.encode();

        verify(context, never()).newObject(Contact.class);
        verify(existingContact).getStudent();
        verify(existingContact).createNewStudent();

        assertEquals(existingContact, result);
    }

    @Test
    public void testNewCompany() {
        ContactCredentials credentials = getContactCredentials();

        ContactCredentialsEncoder contactCredentialsEncoder =
                ContactCredentialsEncoder.valueOf(credentials, true, college, context, service, true);

        Contact result = contactCredentialsEncoder.encode();

        verify(context).newObject(Contact.class);
        verify(newContact).setCollege(college);
        verify(newContact).setGivenName(credentials.getFirstName());
        verify(newContact).setFamilyName(credentials.getLastName());
        verify(newContact).setEmailAddress(credentials.getEmail());
        verify(newContact).setIsCompany(true);
        verify(newContact).setIsMarketingViaEmailAllowed(true);
        verify(newContact).setIsMarketingViaPostAllowed(true);
        verify(newContact).setIsMarketingViaSMSAllowed(true);
        verify(newContact, never()).createNewStudent();

        assertEquals(newContact, result);
    }

    private ContactCredentials getContactCredentials() {
        ContactCredentials credentials = new ContactCredentials();
        credentials.setFirstName("FirstName");
        credentials.setLastName("LastName");
        credentials.setEmail("FirstName.LastName@LastName.com");
        return credentials;
    }


    @Test
    public void testNewStudent() {
        ContactCredentials credentials = getContactCredentials();

        ContactCredentialsEncoder contactCredentialsEncoder =
                ContactCredentialsEncoder.valueOf(credentials, false, college, context, service, true);

        Contact result = contactCredentialsEncoder.encode();

        verify(context).newObject(Contact.class);
        verify(newContact).setCollege(college);
        verify(newContact).setGivenName(credentials.getFirstName());
        verify(newContact).setFamilyName(credentials.getLastName());
        verify(newContact).setEmailAddress(credentials.getEmail());
        verify(newContact).createNewStudent();
        verify(newContact).setIsMarketingViaEmailAllowed(true);
        verify(newContact).setIsMarketingViaPostAllowed(true);
        verify(newContact).setIsMarketingViaSMSAllowed(true);


        assertEquals(newContact, result);
    }
}
