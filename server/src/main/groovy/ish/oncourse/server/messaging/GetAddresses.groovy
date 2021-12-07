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
import ish.oncourse.server.cayenne.Message

import javax.mail.Address
import javax.mail.internet.InternetAddress

@CompileDynamic
class GetAddresses {

    private Set<String> recipients
    private Message message

    private GetAddresses() {

    }

    static GetAddresses empty() {
        new GetAddresses()
    }

    static GetAddresses valueOf(Collection<String> recipients) {
        GetAddresses getAddresses = new GetAddresses()
        getAddresses.recipients = recipients
        getAddresses
    }

    static GetAddresses valueOf(Message message) {
        GetAddresses getAddresses = new GetAddresses()
        getAddresses.message = message
        getAddresses
    }

    static GetAddresses valueOf(String recipient) {
        GetAddresses getAddresses = new GetAddresses()
        getAddresses.recipients = Collections.singleton(recipient)
        getAddresses
    }

    Address[] get() {
        if (message) {
            String personal = message.contact.isCompany ? message.contact.lastName : "${message.contact.firstName} ${message.contact.lastName}".toString()
            [new InternetAddress(message.destinationAddress, personal)]
        } else if (recipients && !recipients.empty) {
            recipients.collect { new InternetAddress(it) }
        } else {
            []
        }
    }
}
