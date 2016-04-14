package ish.oncourse.model;

import ish.oncourse.cayenne.ContactInterface;
import ish.validation.ContactErrorCode;
import org.apache.cayenne.validation.ValidationResult;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FillValidationResultTest {

    @Test
    public void testFillResult() throws Exception {
        ValidationResult result = new ValidationResult();
        Contact contact = Mockito.mock(Contact.class);
        Map<String, ContactErrorCode> errorCodeMap = new HashMap<>();

        errorCodeMap.put(ContactInterface.EMAIL_KEY, ContactErrorCode.incorrectEmailFormat);
        errorCodeMap.put(ContactInterface.LAST_NAME_KEY, ContactErrorCode.lastNameNeedToBeProvided);
        errorCodeMap.put(ContactInterface.FIRST_NAME_KEY, ContactErrorCode.firstNameNeedToBeProvided);
        errorCodeMap.put(ContactInterface.STREET_KEY, ContactErrorCode.incorrectPropertyLength);
        errorCodeMap.put(ContactInterface.STATE_KEY, ContactErrorCode.incorrectPropertyLength);
        errorCodeMap.put(ContactInterface.BIRTH_DATE_KEY, ContactErrorCode.birthDateCanNotBeInFuture);


        FillValidationResult fillValidationResult = FillValidationResult.valueOf(result, errorCodeMap, contact);
        fillValidationResult.fill();

        assertTrue(result.hasFailures());
        assertEquals(6, result.getFailures().size());
    }
}