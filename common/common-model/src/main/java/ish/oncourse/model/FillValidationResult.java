package ish.oncourse.model;

import ish.validation.ContactErrorCode;
import org.apache.cayenne.validation.ValidationResult;

import java.util.Map;

public class FillValidationResult {

    public static final String INVALID_EMAIL_MESSAGE = "The email address does not appear to be valid.";
    public static final String LENGTH_FAILURE_MESSAGE = "Field %s exceeds maximum allowed length";

    private ValidationResult result;
    private Map<String, ContactErrorCode> errorCodeMap;
    private Contact contact;


    private FillValidationResult() {
    }

    public static FillValidationResult valueOf(ValidationResult result, Map<String, ContactErrorCode> errorCodeMap, Contact contact) {
        FillValidationResult validationFailureWriter = new FillValidationResult();
        validationFailureWriter.result = result;
        validationFailureWriter.errorCodeMap = errorCodeMap;
        validationFailureWriter.contact = contact;

        return validationFailureWriter;
    }

    public ValidationResult fill() {
        for (Map.Entry<String, ContactErrorCode> entry : errorCodeMap.entrySet()) {
            switch(entry.getValue()) {
                case birthDateCanNotBeInFuture:
                    result.addFailure(ValidationFailure.validationFailure(contact, entry.getKey(),
                            "The birth date cannot be in the future."));
                    break;
                case firstNameNeedToBeProvided:
                    result.addFailure(ValidationFailure.validationFailure(contact, entry.getKey(),
                            "First name is required."));
                    break;
                case lastNameNeedToBeProvided:
                    result.addFailure(ValidationFailure.validationFailure(contact,  entry.getKey(),
                            "Last name is required."));
                    break;
                case incorrectEmailFormat:
                    result.addFailure(ValidationFailure.validationFailure(contact,  entry.getKey(),
                            INVALID_EMAIL_MESSAGE));
                    break;
                case incorrectPropertyLength:
                    result.addFailure(ValidationFailure.validationFailure(contact, entry.getKey(),
                            String.format(LENGTH_FAILURE_MESSAGE, entry.getKey())));
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }
        return result;
    }
}
