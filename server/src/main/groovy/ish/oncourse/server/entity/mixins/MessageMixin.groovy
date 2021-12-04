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

package ish.oncourse.server.entity.mixins

import ish.oncourse.server.cayenne.Message
import org.apache.cayenne.query.ObjectSelect
import org.apache.commons.lang3.StringUtils

class MessageMixin {
    static String getRecipientsString(Message self) {
        StringBuilder sentToBuilder = new StringBuilder()
        if(self.contact)
            return self.contact.getFullName()

        return sentToBuilder.toString()
    }


    static boolean getIsEmail(Message self) {
        return StringUtils.trimToNull(self.getEmailSubject()) != null &&
                (StringUtils.trimToNull(self.getEmailBody()) != null || StringUtils.trimToNull(self.getEmailHtmlBody()) != null)
    }

    static boolean getIsSms(Message self) {
        return StringUtils.trimToNull(self.getSmsText()) != null
    }

    static boolean getIsPost(Message self) {
        return StringUtils.trimToNull(self.getPostDescription()) != null
    }
}
