package ish.oncourse.utils;

import org.apache.commons.lang3.ArrayUtils;

public class ResourceInvalidCharsValidator {

    public final static char[] INVALID_CHARS = {
            '/',
            '\\',
            '%'

    };

    private char[] additionalChars;

    public static ResourceInvalidCharsValidator valueOf(char[] additionalInvalidChars) {
        ResourceInvalidCharsValidator obj = new ResourceInvalidCharsValidator();
        obj.additionalChars = additionalInvalidChars;
        return obj;
    }

    public String validate(String name) {
        for (char invalidChar :  ArrayUtils.addAll(additionalChars, INVALID_CHARS)) {
            if (name.indexOf(invalidChar) > -1)
                return String.format("Symbol '%c' cannot be used in name", invalidChar);
        }
        return null;
    }
}
