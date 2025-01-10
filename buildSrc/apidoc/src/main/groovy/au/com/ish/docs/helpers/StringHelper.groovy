/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package au.com.ish.docs.helpers

import com.github.jknack.handlebars.Options
import org.apache.commons.lang3.StringUtils

/**
 * The {@code StringHelper} class provides utility methods for working with strings, specifically
 * for use within Handlebars template generators.
 *
 * <p> This class groups functionality that enhancing the flexibility and customization of template rendering. </p>
 */
class StringHelper {

    @Helper
    def static removePrefix(String value, Options options) {
        String prefix = options.params.length != 0 ? "(?i)^${options.param(0)}" : ""
        String newValue = value.replaceFirst(prefix, "")
        return newValue.isEmpty() ? value : newValue
    }

    @Helper
    def static capitalize(String text, Options options) {
        if (text.length() <= 2) {
            return text.toLowerCase()
        }
        return text.charAt(0).toLowerCase().toString() + (text.substring(1))
    }

    @Helper
    def static trim(String value, Options options) {
        return value.trim()
    }

    @Helper
    def static isEmpty(String value, Options options) {
        return StringUtils.isEmpty(value)
    }

}
