/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.validation

import ish.oncourse.server.api.v1.model.ForbiddenTagNamesDTO

import java.util.regex.Pattern

class TagValidation {

    static String validateShortName(String shortName) {
        if (ForbiddenTagNamesDTO.fromValue(shortName.toLowerCase())) {
            'Your chosen tag name is reserved by the onCourse system and cannot be used.'
        } else if (shortName.contains('\"')) {
            'Double quotes are not permitted in the short name.'
        } else if (shortName.contains('+')) {
            'Plus sign is not permitted in the short name.'
        } else if (shortName.contains('\\')) {
            'Backslash is not permitted in the short name.'
        } else if (shortName.contains('/')) {
            'Forward slash is not permitted in the short name.'
        } else if (Pattern.matches('/[0-9].*', shortName)) {
            'This short name is not allowed.'
        } else if (shortName.startsWith('template-')) {
            'This short name is not allowed.'
        }
        null
    }
}
