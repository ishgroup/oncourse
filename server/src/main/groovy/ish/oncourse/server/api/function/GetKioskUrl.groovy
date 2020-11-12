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

package ish.oncourse.server.api.function

import org.apache.commons.lang3.StringUtils

import java.util.regex.Matcher
import java.util.regex.Pattern


class GetKioskUrl {

    static String getKioskUrl(String collegeURL, String entity, Long id) {
        if (StringUtils.trimToNull(collegeURL)) {
            Matcher matcher = Pattern.compile('https?://.+').matcher(collegeURL.toLowerCase())
            return "${matcher.matches() ? '' : 'https://'}${collegeURL}${collegeURL.endsWith('/') ? '' : '/'}${entity}/kiosk/${id}".toString()
        }
        return null
    }


}
