package ish.oncourse.utils;


import org.apache.commons.lang3.StringUtils;

/**
 * The class is used to validate name property for webdav ressources like WebNode, WebContent, WebLayout.
 *
 */

public class ResourceNameValidator {
    
    public String validate(String name) {
        name = StringUtils.trimToEmpty(name);
        if (name.length() < 3) {
            return "Name length cannot be less than 3 characters.";
        }

        return ResourceInvalidCharsValidator.valueOf(null).validate(name);
    }
}
