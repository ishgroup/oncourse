package ish.oncourse.utils;


import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * The class is used to validate name property for webdav ressources like WebNode, WebContent, WebLayout.
 *
 */

public class ResourceNameValidator {
    public final static char[] INVALID_CHARS = {
            '/',
            '\\',
            '%'
            
    };

    private char[] additionalChars = {};

    public ResourceNameValidator excludeChars(char[] chars) {
        additionalChars = chars;
        return this;
    }
    
    public String validate(String name) {
        name = StringUtils.trimToEmpty(name);
        if (name.length() < 3) {
            return "Name length cannot be less than 3 characters.";
        }

        for (char invalidChar :  ArrayUtils.addAll(additionalChars, INVALID_CHARS)) {
            if (name.indexOf(invalidChar) > -1)
                return String.format("Symbol '%c' cannot be used in name", invalidChar);
        }
        return null;
    }
}
