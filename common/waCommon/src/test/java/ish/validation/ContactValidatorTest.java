
package ish.validation;

import ish.oncourse.cayenne.ContactInterface;
import org.apache.cayenne.validation.BeanValidationFailure;
import org.apache.cayenne.validation.ValidationResult;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Date;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class ContactValidatorTest {

    /**
     * birthDate same as new Date
     * @throws Exception
     */

    @Test
    public void testBirthDateValidation1() throws Exception {

        ContactInterface contact = Mockito.mock(ContactInterface.class);
        when(contact.getBirthDate()).thenReturn(new Date());

        String lastName = "test";
        String firstName = "test";
        when(contact.getLastName()).thenReturn(lastName);
        when(contact.getFirstName()).thenReturn(firstName);

        ContactValidator contactValidator = ContactValidator.valueOf(contact);

        ValidationResult validationResult = contactValidator.validate();

        assertEquals(1 ,validationResult.getFailures().size());
    }


    /**
     * birthDate is (new Date + 1day)
     * @throws Exception
     */

    @Test
    public void testBirthDateValidation2() throws Exception {

        ContactInterface contact = Mockito.mock(ContactInterface.class);
        Date birthDate = DateUtils.addDays(new Date(), 1);
        when(contact.getBirthDate()).thenReturn(birthDate);

        String lastName = "test";
        String firstName = "test";
        when(contact.getLastName()).thenReturn(lastName);
        when(contact.getFirstName()).thenReturn(firstName);

        ContactValidator contactValidator = ContactValidator.valueOf(contact);

        ValidationResult validationResult = contactValidator.validate();

        assertEquals(1 ,validationResult.getFailures().size());
        assertThat(validationResult.getFailures().get(0), instanceOf(BeanValidationFailure.class));
        assertEquals(ContactInterface.BIRTH_DATE_PROPERTY, ((BeanValidationFailure)validationResult.getFailures().get(0)).getProperty());
    }


    /**
     * birthDate is (new Date - 1day)
     * @throws Exception
     */

    @Test
    public void testBirthDateValidation3() throws Exception {

        ContactInterface contact = Mockito.mock(ContactInterface.class);
        Date birthDate = DateUtils.addDays(new Date(), -1);
        when(contact.getBirthDate()).thenReturn(birthDate);

        String lastName = "test";
        String firstName = "test";
        when(contact.getLastName()).thenReturn(lastName);
        when(contact.getFirstName()).thenReturn(firstName);

        ContactValidator contactValidator = ContactValidator.valueOf(contact);

        ValidationResult validationResult = contactValidator.validate();

        assertEquals(0 ,validationResult.getFailures().size());
    }

    @Test
    public void testIncorrectFirstNameLastName() throws Exception {

        ContactInterface contact = Mockito.mock(ContactInterface.class);
        String lastName = StringUtils.repeat("a", 250);
        String firstName = StringUtils.repeat("a", 250);
        when(contact.getLastName()).thenReturn(lastName);
        when(contact.getFirstName()).thenReturn(firstName);

        ContactValidator contactValidator = ContactValidator.valueOf(contact);

        ValidationResult validationResult = contactValidator.validate();

        assertEquals(2 ,validationResult.getFailures().size());
    }

    @Test
    public void testCorrectFirstNameLastName() throws Exception {

        ContactInterface contact = Mockito.mock(ContactInterface.class);
        String lastName = StringUtils.repeat("a", 127);
        String firstName = StringUtils.repeat("a", 127);

        when(contact.getLastName()).thenReturn(lastName);
        when(contact.getFirstName()).thenReturn(firstName);

        ContactValidator contactValidator = ContactValidator.valueOf(contact);

        ValidationResult validationResult = contactValidator.validate();

        assertEquals(0 ,validationResult.getFailures().size());
    }

    @Test
    public void testCorrectCompanyName() throws Exception {
        ContactInterface contact = Mockito.mock(ContactInterface.class);
        String lastName = "CompanyName";
        when(contact.getLastName()).thenReturn(lastName);
        when(contact.getIsCompany()).thenReturn(true);

        ContactValidator contactValidator = ContactValidator.valueOf(contact);

        ValidationResult validationResult = contactValidator.validate();

        assertEquals(0 ,validationResult.getFailures().size());
    }

    @Test
    public void testIncorrectCompanyName() throws Exception {
        ContactInterface contact = Mockito.mock(ContactInterface.class);
        String lastName = "";
        when(contact.getLastName()).thenReturn(lastName);
        when(contact.getIsCompany()).thenReturn(true);

        ContactValidator contactValidator = ContactValidator.valueOf(contact);

        ValidationResult validationResult = contactValidator.validate();

        assertEquals(1 ,validationResult.getFailures().size());
    }

    @Test
    public void testEmptyFirstNameLastName() throws Exception {
        ContactInterface contact = Mockito.mock(ContactInterface.class);
        String firstName = "";
        String lastName = "";
        when(contact.getFirstName()).thenReturn(firstName);
        when(contact.getLastName()).thenReturn(lastName);

        ContactValidator contactValidator = ContactValidator.valueOf(contact);

        ValidationResult validationResult = contactValidator.validate();

        assertEquals(2 ,validationResult.getFailures().size());
    }

    @Test
    public void testStreet() throws Exception {
        ContactInterface contact = Mockito.mock(ContactInterface.class);
        String firstName = "first";
        String lastName = "last";
        String street = "street";
        when(contact.getFirstName()).thenReturn(firstName);
        when(contact.getLastName()).thenReturn(lastName);
        when(contact.getStreet()).thenReturn(street);

        ContactValidator contactValidator = ContactValidator.valueOf(contact);

        ValidationResult validationResult = contactValidator.validate();

        assertEquals(0 ,validationResult.getFailures().size());
    }

    @Test
    public void testIncorrectStreet() throws Exception {
        ContactInterface contact = Mockito.mock(ContactInterface.class);
        String firstName = "first";
        String lastName = "last";
        String street = StringUtils.repeat("street", 34);
        when(contact.getFirstName()).thenReturn(firstName);
        when(contact.getLastName()).thenReturn(lastName);
        when(contact.getStreet()).thenReturn(street);

        ContactValidator contactValidator = ContactValidator.valueOf(contact);

        ValidationResult validationResult = contactValidator.validate();

        assertEquals(1 ,validationResult.getFailures().size());
        assertEquals("Street addresses are restricted to 200 characters." ,validationResult.getFailures().get(0).getDescription());
    }
}
