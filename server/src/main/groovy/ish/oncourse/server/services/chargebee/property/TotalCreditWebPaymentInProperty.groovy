/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.services.chargebee.property

import ish.common.types.PaymentSource
import ish.common.chargebee.ChargebeePropertyType

class TotalCreditWebPaymentInProperty extends ChargebeeSimplePropertyProcessor{
    private static final String WEB_CREDIT_AMOUNT_QUERY_FORMAT = "SELECT SUM(pi.amount) AS value" +
            "          FROM PaymentIn pi JOIN PaymentMethod pm on pi.paymentMethodId = pm.id" +
            "          WHERE pm.type = 2 " +
            "          AND pi.createdOn >= '%s'" +
            "          AND pi.createdOn < '%s'" +
            "          AND pi.status IN (3, 6)" +
            "          AND pi.source = '$PaymentSource.SOURCE_WEB.databaseValue'"

    TotalCreditWebPaymentInProperty(Date startDate, Date endDate) {
        super(startDate, endDate)
    }

    @Override
    String getQuery() {
        return String.format(WEB_CREDIT_AMOUNT_QUERY_FORMAT, formattedStartDate, formattedEndDate)
    }

    @Override
    ChargebeePropertyType getType() {
        return ChargebeePropertyType.TOTAL_CREDIT_WEB_PAYMENT_IN
    }
}
