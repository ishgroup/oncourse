package ish.oncourse.enrol.utils;

import ish.oncourse.util.contact.WillowContactValidator;
import ish.validation.ContactErrorCode;
import org.apache.tapestry5.ioc.Messages;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class PhoneValidatorTest {

    private static final String INCORRECT_PHONE_FORMAT_MESSAGE = "Enter 10 digit %s phone number including area code for Australian numbers";

    private Messages messages;

    @Before
    public void setUp() throws Exception {
        messages = Mockito.mock(Messages.class);
    }

    @Test
    public void testIncorrectLength() throws Exception {
        WillowContactValidator.PhoneValidator phoneValidator = WillowContactValidator.PhoneValidator.homePhoneValidator("111112222333344441", false, ContactErrorCode.incorrectPropertyLength, messages).validate();
        assertNull(phoneValidator.getMessage());
    }

    @Test
    public void testIncorrectFormat() throws Exception {
        WillowContactValidator.PhoneValidator phoneValidator = WillowContactValidator.PhoneValidator.homePhoneValidator("12345678901", true, null, messages).validate();
        assertEquals(String.format(INCORRECT_PHONE_FORMAT_MESSAGE, "home phone"), phoneValidator.getMessage());
    }

    @Test
    public void testCorrectFormat() throws Exception {
        WillowContactValidator.PhoneValidator phoneValidator = WillowContactValidator.PhoneValidator.homePhoneValidator("1234 5678 90", true, null, messages).validate();
        assertNotNull(phoneValidator.getMessage());
        assertEquals("1234567890", phoneValidator.getValue());
    }
}