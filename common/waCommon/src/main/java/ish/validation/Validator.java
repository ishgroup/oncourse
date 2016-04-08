package ish.validation;

import org.apache.cayenne.validation.ValidationResult;

public interface Validator {

    ValidationResult validate();
}
