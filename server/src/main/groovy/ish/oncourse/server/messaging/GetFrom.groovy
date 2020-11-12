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

package ish.oncourse.server.messaging

import groovy.transform.CompileDynamic
import org.apache.commons.lang3.StringUtils

import javax.mail.internet.InternetAddress

@CompileDynamic
class GetFrom {

    private String fromEmail
    private String fromName

    private GetFrom() {

    }

    static GetFrom valueOf(String fromEmail) {
        valueOf(fromEmail, null)
    }

    static GetFrom valueOf(String fromEmail, String fromName) {
        GetFrom getFrom = new GetFrom()
        getFrom.fromEmail = fromEmail
        getFrom.fromName = fromName
        getFrom
    }

    InternetAddress get() {
        if (StringUtils.isNotBlank(fromEmail)) {
            InternetAddress address = new InternetAddress(fromEmail)
            if (StringUtils.isNotBlank(fromName)) {
                address.setPersonal(fromName)
            }
            return address
        }

        null
    }

}
