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

package ish.oncourse.server.scripting.api

import ish.oncourse.API
import ish.oncourse.server.cayenne.Contact

/**
 * SMS message sending API.
 *
 * Usage example:
 * ```
 * sms {
 *     to contact1, contact2
 *     text "test SMS text"
 * }
 * ```
 */
@API
class SmsSpec {

    List<Contact> recipients = []
    String text

    /**
     * Set SMS message text.
     *
     * @param text message text
     */
    @API
    def text(String text) {
        this.text = text.take(160)
    }

    /**
     * Set recipients for the SMS message.
     *
     * @param recipients recipients of the message
     */
    @API
    def to(Contact... recipients) {
        this.recipients = recipients.toList()
    }

    /**
     * Set list of recipients for the SMS message.
     *
     * @param recipients list of recipients of the message
     */
    @API
    def to(List<Contact> recipients) {
        this.recipients = recipients.toList()
    }
}
