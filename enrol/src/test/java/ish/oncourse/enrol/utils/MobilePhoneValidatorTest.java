package ish.oncourse.enrol.utils;

import ish.oncourse.enrol.utils.EnrolContactValidator.MobilePhoneValidator;
import ish.validation.ContactErrorCode;
import ish.validation.ContactValidator.Property;
import org.apache.tapestry5.ioc.Messages;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static ish.oncourse.services.preference.PreferenceController.FieldDescriptor.mobilePhoneNumber;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class MobilePhoneValidatorTest {

    private Messages messages;

    private static final String MOBILE_PHONE_LENGTH_MESSAGE = "Max length of mobile phone number can be %d characters.";

    @Before
    public void setUp() throws Exception {
        messages = Mockito.mock(Messages.class);
        when(messages.get("message-mobilePhoneNumber-length")).thenReturn(MOBILE_PHONE_LENGTH_MESSAGE);
        when(messages.format(eq("message-mobilePhoneNumber-length"), anyInt())).thenReturn(String.format(MOBILE_PHONE_LENGTH_MESSAGE, Property.mobilePhone.getLength()));
    }

    @Test
    public void testEmptyMobilePhone() throws Exception {
        Map<String, ContactErrorCode> errorCodes = new HashMap<>();
        MobilePhoneValidator mobilePhoneValidator = MobilePhoneValidator.valueOf(mobilePhoneNumber, Property.mobilePhone.getLength(),
                "", false, errorCodes, messages).validate();
        assertNull(mobilePhoneValidator.getMessage());
    }

    @Test
    public void testIncorrectLength() throws Exception {
        Map<String, ContactErrorCode> errorCodes = new HashMap<>();
        errorCodes.put(Property.mobilePhone.name(), ContactErrorCode.incorrectPropertyLength);
        MobilePhoneValidator mobilePhoneValidator = MobilePhoneValidator.valueOf(mobilePhoneNumber, Property.mobilePhone.getLength(),
                "11112222333344441", false, errorCodes, messages).validate();
        assertEquals(String.format(MOBILE_PHONE_LENGTH_MESSAGE, Property.mobilePhone.getLength()), mobilePhoneValidator.getMessage());
    }

    @Test
    public void testIncorrectMobilePhoneFormat() throws Exception {
        Map<String, ContactErrorCode> errorCodes = new HashMap<>();
        errorCodes.put(Property.mobilePhone.name(), null);
        MobilePhoneValidator mobilePhoneValidator = MobilePhoneValidator.valueOf(mobilePhoneNumber, Property.mobilePhone.getLength(),
                "111 22 22", true, errorCodes, messages).validate();

        assertNotNull(mobilePhoneValidator.getMessage());
    }

    @Test
    public void testCorrectMobilePhoneFormat() throws Exception {
        Map<String, ContactErrorCode> errorCodes = new HashMap<>();
        errorCodes.put(Property.mobilePhone.name(), null);
        MobilePhoneValidator mobilePhoneValidator = MobilePhoneValidator.valueOf(mobilePhoneNumber, Property.mobilePhone.getLength(),
                "04 2222 333 4", true, errorCodes, messages).validate();

        assertNull(mobilePhoneValidator.getMessage());
        assertEquals("0422223334", mobilePhoneValidator.getValue());
    }
}