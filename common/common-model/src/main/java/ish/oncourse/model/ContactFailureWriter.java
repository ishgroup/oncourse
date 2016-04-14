package ish.oncourse.model;

import ish.validation.ContactErrorCode;
import org.apache.cayenne.validation.ValidationResult;

import java.util.Map;

public class ContactFailureWriter {

    public static final String INVALID_EMAIL_MESSAGE = "The email address does not appear to be valid.";
    public static final String LENGTH_FAILURE_MESSAGE = "Field %s exceeds maximum allowed length";

    private ValidationResult result;
    private Map<String, ContactErrorCode> errorCodeMap;
    private Object source;


    private ContactFailureWriter() {
    }

    public static ContactFailureWriter valueOf(ValidationResult result, Map<String, ContactErrorCode> errorCodeMap, Object source) {
        ContactFailureWriter validationFailureWriter = new ContactFailureWriter();
        validationFailureWriter.result = result;
        validationFailureWriter.errorCodeMap = errorCodeMap;
        validationFailureWriter.source = source;

        return validationFailureWriter;
    }

    public void write() {
        for (Map.Entry<String, ContactErrorCode> errorCodeEntry : errorCodeMap.entrySet()) {
            switch(errorCodeEntry.getValue()) {
                case birthDateCanNotBeInFuture:
                    result.addFailure(ValidationFailure.validationFailure(source, errorCodeEntry.getKey(),
                            "The birth date cannot be in the future."));
                    break;
                case firstNameNeedToBeProvided:
                    result.addFailure(ValidationFailure.validationFailure(source, errorCodeEntry.getKey(),
                            "First name is required."));
                    break;
                case lastNameNeedToBeProvided:
                    result.addFailure(ValidationFailure.validationFailure(source,  errorCodeEntry.getKey(),
                            "Last name is required."));
                    break;
                case incorrectEmailFormat:
                    result.addFailure(ValidationFailure.validationFailure(source,  errorCodeEntry.getKey(),
                            INVALID_EMAIL_MESSAGE));
                    break;
                case incorrectPropertyLength:
                    result.addFailure(ValidationFailure.validationFailure(source, errorCodeEntry.getKey(),
                            String.format(LENGTH_FAILURE_MESSAGE, errorCodeEntry.getKey())));
                    break;
            }
        }
    }
}
