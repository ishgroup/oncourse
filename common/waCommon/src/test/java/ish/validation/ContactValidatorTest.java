
package ish.validation;

import ish.oncourse.cayenne.ContactInterface;
import org.apache.cayenne.validation.BeanValidationFailure;
import org.apache.cayenne.validation.ValidationResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
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

        ValidationResult validationResult = new ValidationResult();
        ContactValidator contactValidator = ContactValidator.valueOf(contact, validationResult);

        contactValidator.validate();

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

        ValidationResult validationResult = new ValidationResult();
        ContactValidator contactValidator = ContactValidator.valueOf(contact, validationResult);

        contactValidator.validate();

        assertEquals(1 ,validationResult.getFailures().size());
        assertThat(validationResult.getFailures().get(0), instanceOf(BeanValidationFailure.class));
        assertEquals(ContactInterface.BIRTH_DATE_KEY, ((BeanValidationFailure)validationResult.getFailures().get(0)).getProperty());
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

        ValidationResult validationResult = new ValidationResult();
        ContactValidator contactValidator = ContactValidator.valueOf(contact, validationResult);

        contactValidator.validate();

        assertEquals(0 ,validationResult.getFailures().size());
    }

    @Test
    public void testIncorrectFirstNameLastName() throws Exception {

        ContactInterface contact = Mockito.mock(ContactInterface.class);
        String lastName = StringUtils.repeat("a", 250);
        String firstName = StringUtils.repeat("a", 250);
        when(contact.getLastName()).thenReturn(lastName);
        when(contact.getFirstName()).thenReturn(firstName);

        ValidationResult validationResult = new ValidationResult();
        ContactValidator contactValidator = ContactValidator.valueOf(contact, validationResult);

        contactValidator.validate();

        assertEquals(2 ,validationResult.getFailures().size());
    }

    @Test
    public void testCorrectFirstNameLastName() throws Exception {

        ContactInterface contact = Mockito.mock(ContactInterface.class);
        String lastName = StringUtils.repeat("a", 127);
        String firstName = StringUtils.repeat("a", 127);

        when(contact.getLastName()).thenReturn(lastName);
        when(contact.getFirstName()).thenReturn(firstName);

        ValidationResult validationResult = new ValidationResult();
        ContactValidator contactValidator = ContactValidator.valueOf(contact, validationResult);

        contactValidator.validate();

        assertEquals(0 ,validationResult.getFailures().size());
    }

    @Test
    public void testCorrectCompanyName() throws Exception {
        ContactInterface contact = Mockito.mock(ContactInterface.class);
        String lastName = "CompanyName";
        when(contact.getLastName()).thenReturn(lastName);
        when(contact.getIsCompany()).thenReturn(true);

        ValidationResult validationResult = new ValidationResult();
        ContactValidator contactValidator = ContactValidator.valueOf(contact, validationResult);

        contactValidator.validate();

        assertEquals(0 ,validationResult.getFailures().size());
    }

    @Test
    public void testIncorrectCompanyName() throws Exception {
        ContactInterface contact = Mockito.mock(ContactInterface.class);
        String lastName = "";
        when(contact.getLastName()).thenReturn(lastName);
        when(contact.getIsCompany()).thenReturn(true);

        ValidationResult validationResult = new ValidationResult();
        ContactValidator contactValidator = ContactValidator.valueOf(contact, validationResult);

        contactValidator.validate();

        assertEquals(1 ,validationResult.getFailures().size());
    }

    @Test
    public void testEmptyFirstNameLastName() throws Exception {
        ContactInterface contact = Mockito.mock(ContactInterface.class);
        String firstName = "";
        String lastName = "";
        when(contact.getFirstName()).thenReturn(firstName);
        when(contact.getLastName()).thenReturn(lastName);

        ValidationResult validationResult = new ValidationResult();
        ContactValidator contactValidator = ContactValidator.valueOf(contact, validationResult);

        contactValidator.validate();

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

        ValidationResult validationResult = new ValidationResult();
        ContactValidator contactValidator = ContactValidator.valueOf(contact, validationResult);

        contactValidator.validate();

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

        ValidationResult validationResult = new ValidationResult();
        ContactValidator contactValidator = ContactValidator.valueOf(contact, validationResult);

        contactValidator.validate();

        assertEquals(1 ,validationResult.getFailures().size());
        assertEquals("Street addresses are restricted to 200 characters." ,validationResult.getFailures().get(0).getDescription());
    }

    @Test
    public void testCorrectEmail() throws Exception {
        ContactInterface contact = Mockito.mock(ContactInterface.class);
        String firstName = "first";
        String lastName = "last";
        String email = "test@com.au";
        when(contact.getFirstName()).thenReturn(firstName);
        when(contact.getLastName()).thenReturn(lastName);
        when(contact.getEmail()).thenReturn(email);

        ValidationResult validationResult = new ValidationResult();
        ContactValidator contactValidator = ContactValidator.valueOf(contact, validationResult);

        contactValidator.validate();

        assertEquals(0, validationResult.getFailures().size());
    }

    /**
     * test incorrect email
     * @throws Exception
     */
    @Test
    public void testIncorrectEmail1() throws Exception {
        ContactInterface contact = Mockito.mock(ContactInterface.class);
        String firstName = "first";
        String lastName = "last";
        String email = "test@com.au@au";
        when(contact.getFirstName()).thenReturn(firstName);
        when(contact.getLastName()).thenReturn(lastName);
        when(contact.getEmail()).thenReturn(email);

        ValidationResult validationResult = new ValidationResult();
        ContactValidator contactValidator = ContactValidator.valueOf(contact, validationResult);

        contactValidator.validate();

        assertEquals(1, validationResult.getFailures().size());
        assertEquals("Please enter an email address in the correct format.", validationResult.getFailures().get(0).getDescription());
    }

    /**
     * test too long email
     * @throws Exception
     */
    @Test
    public void testIncorrectEmail2() throws Exception {
        ContactInterface contact = Mockito.mock(ContactInterface.class);
        String firstName = "first";
        String lastName = "last";
        String email = StringUtils.repeat("test",30).concat("@com.au");
        when(contact.getFirstName()).thenReturn(firstName);
        when(contact.getLastName()).thenReturn(lastName);
        when(contact.getEmail()).thenReturn(email);

        ValidationResult validationResult = new ValidationResult();
        ContactValidator contactValidator = ContactValidator.valueOf(contact, validationResult);

        contactValidator.validate();

        assertEquals(1, validationResult.getFailures().size());
        assertEquals("Email addresses are restricted to 100 characters.", validationResult.getFailures().get(0).getDescription());
    }

    @Test
    public void validateWillowAngelPropertyLength() throws Exception {
        ContactInterface contact = Mockito.mock(ContactInterface.class);
        String firstName = "first";
        String lastName = "last";
        String postCode = StringUtils.repeat("a",ContactValidator.POST_CODE_MAX_LENGTH + 1);
        String state = StringUtils.repeat("a",ContactValidator.STATE_MAX_LENGTH + 1);
        String mobilePhone = StringUtils.repeat("a",ContactValidator.MOBILE_PHONE_NUMBER_MAX_LENGTH + 1);
        String homePhone = StringUtils.repeat("a",ContactValidator.HOME_PHONE_NUMBER_MAX_LENGTH + 1);
        String fax = StringUtils.repeat("a",ContactValidator.FAX_MAX_LENGTH + 1);


        when(contact.getFirstName()).thenReturn(firstName);
        when(contact.getLastName()).thenReturn(lastName);
        when(contact.getPostcode()).thenReturn(postCode);
        when(contact.getState()).thenReturn(state);
        when(contact.getMobilePhone()).thenReturn(mobilePhone);
        when(contact.getHomePhone()).thenReturn(homePhone);
        when(contact.getFax()).thenReturn(fax);

        ValidationResult validationResult = new ValidationResult();
        ContactValidator contactValidator = ContactValidator.valueOf(contact, validationResult);

        contactValidator.validate();

        assertEquals(5, validationResult.getFailures().size());
    }

    @Test
    public void testNullProperties() throws Exception {
        ContactInterface contact = Mockito.mock(ContactInterface.class);
        when(contact.getFirstName()).thenReturn(null);
        when(contact.getLastName()).thenReturn(null);
        when(contact.getPostcode()).thenReturn(null);
        when(contact.getState()).thenReturn(null);
        when(contact.getMobilePhone()).thenReturn(null);
        when(contact.getHomePhone()).thenReturn(null);
        when(contact.getFax()).thenReturn(null);
        when(contact.getEmail()).thenReturn(null);

        ValidationResult validationResult = new ValidationResult();
        ContactValidator contactValidator = ContactValidator.valueOf(contact, validationResult);

        contactValidator.validate();

        assertEquals(2, validationResult.getFailures().size());
    }
}