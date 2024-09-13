/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.services.chargebee.property

import ish.common.chargebee.ChargebeePropertyType
import ish.common.types.AccountTransactionType
import ish.common.types.PaymentSource
import ish.oncourse.server.cayenne.PaymentIn
import ish.oncourse.server.cayenne.PaymentInLine
import ish.oncourse.server.cayenne.PaymentOut
import ish.oncourse.server.cayenne.PaymentOutLine
import org.apache.commons.lang3.StringUtils

import javax.sql.DataSource

import static ish.oncourse.server.util.DbConnectionUtils.getLongForDbQuery

class TotalOfficePaymentInProperty extends ChargebeePropertyProcessor{
    private static final String QUERY_FORMAT = "SELECT COUNT(p) AS value" +
            "          FROM PaymentIn p " +
            "          JOIN PaymentMethod pm on p.paymentMethodId = pm.id" +
            "          WHERE pm.type = 2 AND p.source = $PaymentSource.SOURCE_ONCOURSE.databaseValue"

    TotalOfficePaymentInProperty(Date startDate, Date endDate) {
        super(startDate, endDate)
    }

    @Override
    Long getValue(DataSource dataSource) {
        String paymentsInQuery = String.format(QUERY_FORMAT, AccountTransactionType.PAYMENT_IN_LINE, PaymentInLine.simpleName,
                PaymentIn.simpleName, StringUtils.toRootLowerCase(PaymentIn.simpleName), formattedStartDate, formattedEndDate)
        def paymentsInTotal = getLongForDbQuery(paymentsInQuery, dataSource)

        String paymentsOutQuery = String.format(QUERY_FORMAT, AccountTransactionType.PAYMENT_OUT_LINE, PaymentOutLine.simpleName,
                PaymentOut.simpleName, StringUtils.toRootLowerCase(PaymentOut.simpleName), formattedStartDate, formattedEndDate)
        def paymentsOutTotal = getLongForDbQuery(paymentsOutQuery, dataSource)

        return paymentsInTotal + paymentsOutTotal
    }

    @Override
    ChargebeePropertyType getType() {
        return ChargebeePropertyType.TOTAL_OFFICE_PAYMENT_IN_NUMBER
    }
}
