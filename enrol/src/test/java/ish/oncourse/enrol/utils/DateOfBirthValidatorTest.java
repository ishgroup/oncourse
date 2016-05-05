package ish.oncourse.enrol.utils;

import ish.oncourse.enrol.utils.EnrolContactValidator.DateOfBirthValidator;
import ish.validation.ContactErrorCode;
import org.apache.tapestry5.ioc.Messages;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static ish.oncourse.enrol.utils.EnrolContactValidator.KEY_ERROR_dateOfBirth_shouldBeInPast;
import static ish.oncourse.enrol.utils.EnrolContactValidator.KEY_ERROR_MESSAGE_birthdate_old;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class DateOfBirthValidatorTest {

    private static final String DATE_OF_BIRTH_SHOULD_BE_IN_PAST_MESSAGE = "Your date of birth must be in past.";
    private static final String OLD_BIRTH_DATE_MESSAGE = "Only date of birth in format DD/MM/YYYY and after 01/01/1900 are valid";


    private Messages messages;

    @Before
    public void setUp() throws Exception {
        messages = Mockito.mock(Messages.class);
        when(messages.get(KEY_ERROR_dateOfBirth_shouldBeInPast)).thenReturn(DATE_OF_BIRTH_SHOULD_BE_IN_PAST_MESSAGE);
        when(messages.get(KEY_ERROR_MESSAGE_birthdate_old)).thenReturn(OLD_BIRTH_DATE_MESSAGE);

    }

    @Test
    public void testFutureBirthDate() throws Exception {
        DateOfBirthValidator dateOfBirthValidator = DateOfBirthValidator.valueOf(new Date(),
                ContactErrorCode.birthDateCanNotBeInFuture, messages).validate();

        assertEquals(DATE_OF_BIRTH_SHOULD_BE_IN_PAST_MESSAGE, dateOfBirthValidator.getMessage());
    }

    @Test
    public void testOldBirthDate() throws Exception {
        DateFormat formatter = new SimpleDateFormat("dd/mm/yyyy");
        Date date = formatter.parse("1/1/1899");

        DateOfBirthValidator dateOfBirthValidator = DateOfBirthValidator.valueOf(date,
                null, messages).validate();

        assertEquals(OLD_BIRTH_DATE_MESSAGE, dateOfBirthValidator.getMessage());
    }

    @Test
    public void testCorrectBirthDate() throws Exception {
        DateFormat formatter = new SimpleDateFormat("dd/mm/yyyy");
        Date date = formatter.parse("1/1/1995");

        DateOfBirthValidator dateOfBirthValidator = DateOfBirthValidator.valueOf(date,
                null, messages);

        assertNull(dateOfBirthValidator.getMessage());
    }
}