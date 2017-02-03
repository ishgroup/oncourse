package ish.oncourse.enrol.utils;

import ish.oncourse.util.contact.WillowContactValidator;
import ish.validation.ContactErrorCode;
import org.apache.tapestry5.ioc.Messages;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class ValidatePostcodeTest {

    private static final String POSTCODE_LENGTH_MESSAGE = "Incorrect postcode length";
    private static final String POSTCODE_FORMAT_MESSAGE = "Enter 4 digit postcode for Australian postcodes.";

    private Messages messages;

    @Before
    public void setUp() throws Exception {
        messages = Mockito.mock(Messages.class);
        when(messages.format(eq("message-postcode-length"), anyObject())).thenReturn(POSTCODE_LENGTH_MESSAGE);
        when(messages.get(eq("message-postcode-4-digits"))).thenReturn(POSTCODE_FORMAT_MESSAGE);
      }

    @Test
    public void testPostcodeIncorrectLength() throws Exception {
        WillowContactValidator.ValidatePostcode postCodeValidator = WillowContactValidator.ValidatePostcode.valueOf("011", false, ContactErrorCode.incorrectPropertyLength, messages);
        assertEquals(POSTCODE_LENGTH_MESSAGE, postCodeValidator.getMessage());
    }

    @Test
    public void testPostcodeCorrectLength() throws Exception {
        WillowContactValidator.ValidatePostcode postCodeValidator = WillowContactValidator.ValidatePostcode.valueOf("011", false, null, messages);
        assertNotEquals(POSTCODE_LENGTH_MESSAGE, postCodeValidator.getMessage());
    }

    @Test
    public void testIncorrectPostcodeFormat() throws Exception {
        WillowContactValidator.ValidatePostcode postCodeValidator = WillowContactValidator.ValidatePostcode.valueOf("11111", true, null, messages);
        assertEquals(POSTCODE_FORMAT_MESSAGE, postCodeValidator.getMessage());
    }

    @Test
    public void testCorrectPostcodeFormat() throws Exception {
        WillowContactValidator.ValidatePostcode postCodeValidator = WillowContactValidator.ValidatePostcode.valueOf("1111", true, null, messages);
        assertNotEquals(POSTCODE_FORMAT_MESSAGE, postCodeValidator.getMessage());
    }
}