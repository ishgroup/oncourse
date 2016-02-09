package ish.oncourse.portal.usi;

import org.apache.tapestry5.ioc.Messages;

import java.util.HashMap;
import java.util.Map;

public class ValidationResult {

    private Map<String, String> errors = new HashMap<>();
    private Map<String, String> warnings = new HashMap<>();

    public void addError(String messageKey, Messages messages, Object... params) {
        errors.put(messageKey, messages.format(messageKey, params));
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public Map<String, String> getWarnings() {
        return warnings;
    }

    public void setErrors(Map<String, String> errors) {
        this.errors.clear();
        this.errors.putAll(errors);
    }

    public void setWarnings(Map<String, String> warnings) {
        this.warnings.clear();
        this.errors.putAll(warnings);
    }

    public void clear()
    {
        this.errors.clear();
        this.warnings.clear();
    }

    public boolean hasErrors()
    {
        return !this.errors.isEmpty();
    }

    public boolean hasWarnings()
    {
        return !this.warnings.isEmpty();
    }
}
