package ish.oncourse.utils;

import org.apache.commons.lang3.StringUtils;

public class ResourceEmptyNameValidator {

    private ResourceEmptyNameValidator() {}

    public static ResourceEmptyNameValidator valueOf() {
        return new ResourceEmptyNameValidator();
    }

    public String validate(String name) {
        name = StringUtils.trimToEmpty(name);
        if (name.length() == 0) {
            return "Name cannot be empty.";
        }

        return ResourceInvalidCharsValidator.valueOf(null).validate(name);
    }
}
