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
import ish.oncourse.server.cayenne.MessagePerson
import ish.oncourse.server.cayenne.Tag

@CompileDynamic
class GetUnsubscribeLink {

    private static final String MAILING_LIST_UNSUBSCRIBE_URL = 'https://www.skillsoncourse.com.au/portal/unsubscribe/'

    private MessagePerson messagePerson

    static GetUnsubscribeLink valueOf(MessagePerson messagePerson) {
        GetUnsubscribeLink getUnsubscribeLink = new GetUnsubscribeLink()
        getUnsubscribeLink.messagePerson = messagePerson
        getUnsubscribeLink
    }

    String get() {
        if (!messagePerson.message.taggingRelations.empty) {
            Tag mailingList = messagePerson.message.taggingRelations[0].tag

            if (mailingList && mailingList.willowId) {
                return "${MAILING_LIST_UNSUBSCRIBE_URL}${mailingList.willowId}-${messagePerson.contact.uniqueCode}".toString()
            }
        }

        ''
    }
}
