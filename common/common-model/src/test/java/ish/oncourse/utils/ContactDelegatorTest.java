package ish.oncourse.utils;

import ish.oncourse.model.Contact;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ContactDelegatorTest {

    @Test
    public void testContactDelegator() throws Exception {
        Contact contact = mock(Contact.class);
        when(contact.getGivenName()).thenReturn("givenName");
        when(contact.getFamilyName()).thenReturn("familyName");
        when(contact.getMobilePhoneNumber()).thenReturn("mobilePhone");
        when(contact.getPostcode()).thenReturn("postCode");
        when(contact.getStreet()).thenReturn("street");
        Date currentDate = new Date();
        when(contact.getDateOfBirth()).thenReturn(currentDate);
        when(contact.getIsCompany()).thenReturn(true);
        when(contact.getState()).thenReturn("state");
        when(contact.getHomePhoneNumber()).thenReturn("homePhone");
        when(contact.getFaxNumber()).thenReturn("fax");
        when(contact.getEmailAddress()).thenReturn("test@com.au");

        ContactDelegator contactDelegator = ContactDelegator.valueOf(contact);

        assertEquals(contact.getGivenName(), contactDelegator.getFirstName());
        assertEquals(contact.getFamilyName(), contactDelegator.getLastName());
        assertEquals(contact.getMobilePhoneNumber(), contactDelegator.getMobilePhone());
        assertEquals(contact.getPostcode(), contactDelegator.getPostcode());
        assertEquals(contact.getStreet(), contactDelegator.getStreet());
        assertEquals(contact.getDateOfBirth(), contactDelegator.getDateOfBirth());
        assertEquals(contact.getIsCompany(), contactDelegator.getIsCompany());
        assertEquals(contact.getState(), contactDelegator.getState());
        assertEquals(contact.getHomePhoneNumber(), contactDelegator.getHomePhone());
        assertEquals(contact.getFaxNumber(), contactDelegator.getFax());
        assertEquals(contact.getEmailAddress(), contactDelegator.getEmail());
    }
}