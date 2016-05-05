package ish.oncourse.enrol.utils;

import ish.oncourse.enrol.utils.EnrolContactValidator.ValidateLength;
import ish.oncourse.services.preference.PreferenceController;
import ish.validation.ContactErrorCode;
import ish.validation.ContactValidator;
import org.apache.tapestry5.ioc.Messages;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static ish.validation.ContactErrorCode.incorrectPropertyLength;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class ValidateLengthTest {

    private Messages messages;

    @Before
    public void setUp() throws Exception {
        messages = Mockito.mock(Messages.class);
        when(messages.format(eq("message-postcode-length"), anyObject())).thenReturn("Error");
    }

    @Test
    public void testIncorrectLength() throws Exception {
        ValidateLength lengthValidator = ValidateLength.valueOf(PreferenceController.FieldDescriptor.postcode,
                ContactValidator.Property.postcode.getLength(),
                incorrectPropertyLength,
                messages);

        assertNotNull(lengthValidator.getMessage());
    }

    @Test
    public void testCorrectLength() throws Exception {
        ValidateLength lengthValidator = ValidateLength.valueOf(PreferenceController.FieldDescriptor.postcode,
                ContactValidator.Property.postcode.getLength(),
                null,
                messages);

        assertNull(lengthValidator.getMessage());

        lengthValidator = ValidateLength.valueOf(PreferenceController.FieldDescriptor.postcode,
                ContactValidator.Property.postcode.getLength(),
                ContactErrorCode.lastNameNeedToBeProvided,
                messages);

        assertNull(lengthValidator.getMessage());
    }
}