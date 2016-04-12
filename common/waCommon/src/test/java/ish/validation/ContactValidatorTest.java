
package ish.validation;

import ish.math.Money;
import ish.oncourse.cayenne.ContactInterface;
import ish.oncourse.cayenne.InvoiceInterface;
import ish.oncourse.cayenne.PaymentInterface;
import ish.validation.ContactValidator.Property;
import org.apache.cayenne.validation.BeanValidationFailure;
import org.apache.cayenne.validation.ValidationResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class ContactValidatorTest {

    /**
     * birthDate same as new Date
     *
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

        assertEquals(1, validationResult.getFailures().size());
    }


    /**
     * birthDate is (new Date + 1day)
     *
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

        assertEquals(1, validationResult.getFailures().size());
        assertThat(validationResult.getFailures().get(0), instanceOf(BeanValidationFailure.class));
        assertEquals(ContactInterface.BIRTH_DATE_KEY, ((BeanValidationFailure) validationResult.getFailures().get(0)).getProperty());
    }


    /**
     * birthDate is (new Date - 1day)
     *
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

        assertEquals(0, validationResult.getFailures().size());
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

        assertEquals(2, validationResult.getFailures().size());
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

        assertEquals(0, validationResult.getFailures().size());
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

        assertEquals(0, validationResult.getFailures().size());
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

        assertEquals(1, validationResult.getFailures().size());
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

        assertEquals(2, validationResult.getFailures().size());
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

        assertEquals(0, validationResult.getFailures().size());
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

        assertEquals(1, validationResult.getFailures().size());
        assertEquals(String.format(ContactValidator.LENGTH_FAILURE_MESSAGE, ContactInterface.STREET_KEY, Property.street.getLength(), street.length())
                , validationResult.getFailures().get(0).getDescription());
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
     *
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
     *
     * @throws Exception
     */
    @Test
    public void testIncorrectEmail2() throws Exception {
        ContactInterface contact = Mockito.mock(ContactInterface.class);
        String firstName = "first";
        String lastName = "last";
        String email = StringUtils.repeat("test", 30).concat("@com.au");
        when(contact.getFirstName()).thenReturn(firstName);
        when(contact.getLastName()).thenReturn(lastName);
        when(contact.getEmail()).thenReturn(email);

        ValidationResult validationResult = new ValidationResult();
        ContactValidator contactValidator = ContactValidator.valueOf(contact, validationResult);

        contactValidator.validate();

        assertEquals(1, validationResult.getFailures().size());
        assertEquals(String.format(ContactValidator.LENGTH_FAILURE_MESSAGE, ContactInterface.EMAIL_KEY, Property.email.getLength(), email.length()),
                validationResult.getFailures().get(0).getDescription());
    }

    @Test
    public void validateWillowAngelPropertyLength() throws Exception {
        ContactInterface contact = Mockito.mock(ContactInterface.class);
        String firstName = "first";
        String lastName = "last";
        String postCode = StringUtils.repeat("a", Property.postcode.getLength() + 1);
        String state = StringUtils.repeat("a", Property.state.getLength() + 1);
        String mobilePhone = StringUtils.repeat("a", Property.mobilePhone.getLength() + 1);
        String homePhone = StringUtils.repeat("a", Property.homePhone.getLength() + 1);
        String fax = StringUtils.repeat("a", Property.fax.getLength() + 1);


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

    /**
     * test creation of ContactInterface as inner class
     */
    @Test
    public void testContactInterface() {

        ContactInterface contactInterface = new ContactInterface() {
            @Override
            public String getCalendarUrl() {
                return null;
            }

            @Override
            public String getEmail() {
                return null;
            }

            @Override
            public String getFirstName() {
                return "first";
            }

            @Override
            public String getName() {
                return null;
            }

            @Override
            public String getName(boolean firstNameFirst) {
                return null;
            }

            @Override
            public String getLastName() {
                return "second";
            }

            @Override
            public String getMobilePhone() {
                return null;
            }

            @Override
            public String getPostcode() {
                return null;
            }

            @Override
            public String getStreet() {
                return null;
            }

            @Override
            public String getSuburb() {
                return null;
            }

            @Override
            public Money getTotalOwing() {
                return null;
            }

            @Override
            public List<PaymentInterface> getPayments() {
                return null;
            }

            @Override
            public List<? extends InvoiceInterface> getOwingInvoices() {
                return null;
            }

            @Override
            public List<? extends InvoiceInterface> getInvoices() {
                return null;
            }

            @Override
            public Date getBirthDate() {
                return null;
            }

            @Override
            public Boolean getIsCompany() {
                return null;
            }

            @Override
            public String getState() {
                return null;
            }

            @Override
            public String getHomePhone() {
                return null;
            }

            @Override
            public String getFax() {
                return null;
            }
        };

        ValidationResult validationResult = new ValidationResult();
        ContactValidator contactValidator = ContactValidator.valueOf(contactInterface, validationResult);

        contactValidator.validate();
    }
}