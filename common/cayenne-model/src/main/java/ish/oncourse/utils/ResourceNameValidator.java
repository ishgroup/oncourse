package ish.oncourse.utils;


import org.apache.commons.lang3.StringUtils;

/**
 * The class is used to validate name property for webdav ressources like WebNode, WebContent, WebLayout.
 *
 */

public class ResourceNameValidator {

    private char[] excludeChars = null;

    private ResourceNameValidator() {}

    public static ResourceNameValidator valueOf() {
        return new ResourceNameValidator();
    }

    public static ResourceNameValidator valueOf(char[] excludeChars) {
        ResourceNameValidator obj = new ResourceNameValidator();
        obj.excludeChars = excludeChars;
        return obj;
    }
    
    public String validate(String name) {
        name = StringUtils.trimToEmpty(name);
        if (name.length() < 3) {
            return "Name length cannot be less than 3 characters.";
        }

        return ResourceInvalidCharsValidator.valueOf(excludeChars).validate(name);
    }
}
