/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.services.chargebee.property

import ish.common.chargebee.ChargebeePropertyType

class SmsChargebeeProperty extends ChargebeeSimplePropertyProcessor {
    private static final int SMS_LENGTH = 160
    private static final String SMS_LENGTH_QUERY_FORMAT = "SELECT COALESCE(SUM((length(m.smsText) DIV $SMS_LENGTH) + 1), 0) AS credits" +
            "          FROM Message m" +
            "          WHERE m.smsText IS NOT NULL AND m.smsText <> ''" +
            "          AND m.createdOn >= '%s'" +
            "          AND m.createdOn < '%s'"

    SmsChargebeeProperty(Date startDate, Date endDate) {
        super(startDate, endDate)
    }

    @Override
    ChargebeePropertyType getType() {
        return ChargebeePropertyType.SMS
    }

    @Override
    String getQuery() {
        return String.format(SMS_LENGTH_QUERY_FORMAT, formattedStartDate, formattedEndDate)
    }
}
