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
import ish.oncourse.server.cayenne.Tag

@CompileDynamic
class GetUnsubscribeLink {

    private static final String MAILING_LIST_UNSUBSCRIBE_URL = 'https://www.skillsoncourse.com.au/portal/unsubscribe/'

    private Message message

    static GetUnsubscribeLink valueOf(Message message) {
        GetUnsubscribeLink getUnsubscribeLink = new GetUnsubscribeLink()
        getUnsubscribeLink.message = message
        getUnsubscribeLink
    }

    String get() {
        if (!message.message.taggingRelations.empty) {
            Tag mailingList = message.message.taggingRelations[0].tag

            if (mailingList && mailingList.willowId) {
                return "${MAILING_LIST_UNSUBSCRIBE_URL}${mailingList.willowId}-${message.contact.uniqueCode}".toString()
            }
        }

        ''
    }
}
